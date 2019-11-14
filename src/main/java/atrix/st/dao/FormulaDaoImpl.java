/*
 * © 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 * 
 * Asymmetrix Solutions Private Limited
 * 115, Bldg 2, Sector 3, Millennium Business Park,
 * Navi Mumbai, India, 410701
 */
package atrix.st.dao;

import atrix.common.model.QueryBuilderModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesFxTreeModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("formulaDao")
public class FormulaDaoImpl extends JdbcDaoSupport implements FormulaDao {

    @Autowired
    FormulaDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    @Autowired
    private LobHandler lobHandler;
    
    @Override
    public Integer checkFxName(String fxName) {
        String sql = "SELECT count(*) FROM CNFG_FORMULA_TABLE WHERE v_formula_name = ?";
        Integer val = getJdbcTemplate().queryForObject(sql, new Object[]{fxName},Integer.class);
        return val;
    }

    @Override
    public GridPage<RulesFxGridModel> listFx(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type) {
        String query1, query2, formulaType;
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_formula_id", "v_formula_name",
                "v_aggregation_type"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_formula_name";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        if (type.equals("edit")) {
            formulaType = "'UDF'";
        } else {
            formulaType = "'UDF','PDF'";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        query1 = "SELECT count(*) FROM CNFG_FORMULA_TABLE WHERE v_formula_type IN (" + formulaType + ") "
                + qObj.getCondition();
        query2 = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_formula_id, v_formula_name, case when v_formula_type = 'PDF' then 'Predefined' "
                + "else 'User Defined' end v_formula_type, v_aggregation_type "
                + "FROM CNFG_FORMULA_TABLE WHERE v_formula_type IN (" + formulaType + ") "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        int rowCount = getJdbcTemplate().queryForObject(query1, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        List<RulesFxGridModel> fxs = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query2, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            RulesFxGridModel fx = new RulesFxGridModel();
            fx.setId(fmt.ToString(row.get("rnum")));
            fx.setFormulaId(fmt.ToString(row.get("v_formula_id")));
            fx.setFormulaName(fmt.ToString(row.get("v_formula_name")));
            fx.setFormulaType(fmt.ToString(row.get("v_formula_type")));
            fx.setAggregateType(fmt.ToString(row.get("v_aggregation_type")));
            fxs.add(fx);
        }
        return new GridPage<>(fxs, page, max, rowCount);
    }

    @Override
    public List<RulesFxTreeModel> listRulesTable() {
        String query = "SELECT distinct v_table_id, v_table_name FROM CNFG_MODEL_MASTER ORDER BY v_table_name";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
        List<RulesFxTreeModel> lTables = new ArrayList<RulesFxTreeModel>();
        for (Map row : rows) {
            RulesFxTreeModel obj = new RulesFxTreeModel();
            obj.setKey(fmt.ToString(row.get("v_table_id")));
            obj.setTitle(fmt.ToString(row.get("v_table_name")));
            lTables.add(obj);
        }
        return lTables;
    }

    @Override
    public List<RulesFxTreeModel> listRulesColumns(String key) {
        String query = "SELECT v_desc_column FROM CNFG_MODEL_MASTER WHERE v_table_id = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{key});
        List<RulesFxTreeModel> lColumns = new ArrayList<RulesFxTreeModel>();
        for (Map row : rows) {
            RulesFxTreeModel obj = new RulesFxTreeModel();
            obj.setKey(fmt.ToString(row.get("v_desc_column")));
            obj.setTitle(fmt.ToString(row.get("v_desc_column")));
            lColumns.add(obj);
        }
        return lColumns;
    }

    @Override
    public List<RulesFxTreeModel> listFxType() {
        String query = "SELECT distinct v_formula_type, case when v_formula_type = 'PDF' then 'Predefined' "
                + "else 'User Defined' end v_formula_desc FROM CNFG_FORMULA_TABLE";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
        List<RulesFxTreeModel> lTables = new ArrayList<RulesFxTreeModel>();
        for (Map row : rows) {
            RulesFxTreeModel obj = new RulesFxTreeModel();
            obj.setKey(fmt.ToString(row.get("v_formula_type")));
            obj.setTitle(fmt.ToString(row.get("v_formula_desc")));
            lTables.add(obj);
        }
        return lTables;
    }

    @Override
    public List<RulesFxTreeModel> listFxName(String key) {
        String query = "SELECT v_formula_name FROM CNFG_FORMULA_TABLE WHERE v_formula_type = ? "
                + "ORDER BY v_formula_name";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{key});
        List<RulesFxTreeModel> lColumns = new ArrayList<RulesFxTreeModel>();
        for (Map row : rows) {
            RulesFxTreeModel obj = new RulesFxTreeModel();
            obj.setKey(fmt.ToString(row.get("v_formula_name")));
            obj.setTitle(fmt.ToString(row.get("v_formula_name")));
            lColumns.add(obj);
        }
        return lColumns;
    }

    @Override
    public List<RulesFxTreeModel> listOperatorType() {
        String query = "SELECT distinct v_type FROM CNFG_FORMULA_TREE ORDER BY v_type";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
        List<RulesFxTreeModel> lTables = new ArrayList<RulesFxTreeModel>();
        for (Map row : rows) {
            RulesFxTreeModel obj = new RulesFxTreeModel();
            obj.setKey(fmt.ToString(row.get("v_type")));
            obj.setTitle(fmt.ToString(row.get("v_type")));
            lTables.add(obj);
        }
        return lTables;
    }

    @Override
    public List<RulesFxTreeModel> listOperatorName(String key) {
        String query = "SELECT v_name FROM CNFG_FORMULA_TREE WHERE v_type = ? ORDER BY n_serial";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{key});
        List<RulesFxTreeModel> lColumns = new ArrayList<RulesFxTreeModel>();
        for (Map row : rows) {
            RulesFxTreeModel obj = new RulesFxTreeModel();
            obj.setKey(fmt.ToString(row.get("v_name")));
            obj.setTitle(fmt.ToString(row.get("v_name")));
            lColumns.add(obj);
        }
        return lColumns;
    }

    @Override
    public String getFxWarning(String fxId) {
        String query = "SELECT distinct v_rule_name FROM CNFG_RULE_MAPPING_MASTER WHERE c_fx like ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{"%|| " + fxId + " ||%"});
        String list = "";
        int i = 1;
        for (Map row : rows) {
            list = list + i + ". " + fmt.ToString(row.get("v_rule_name")) + "</br >";
            i++;
        }
        return list;
    }

    @Override
    public void insertFx(RulesFxGridModel expModel) {
        String query = "INSERT INTO CNFG_FORMULA_TABLE (v_formula_id, v_formula_name, c_formula_string, "
                + "v_aggregation_type, v_formula_type, v_audit_cd) VALUES "
                + "('F'||formulaid_sequence.nextval, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{expModel.getFormulaName(),
                    new SqlLobValue(expModel.getFormulaText(), lobHandler), expModel.getAggregateType(),
                    "UDF", "SYS"}, new int[]{Types.VARCHAR, Types.CLOB, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR});
    }

    @Override
    public RulesFxGridModel getFormula(String fxid) {
        String sql = "SELECT v_formula_id, v_formula_name, c_formula_string, v_aggregation_type, v_formula_type "
                + "FROM CNFG_FORMULA_TABLE WHERE v_formula_id = ?";
        RulesFxGridModel fx = (RulesFxGridModel) getJdbcTemplate().queryForObject(
                sql, new Object[]{fxid}, new RowMapper<RulesFxGridModel>() {

            @Override
            public RulesFxGridModel mapRow(ResultSet rs, int i) throws SQLException {
                RulesFxGridModel fx = new RulesFxGridModel();
                fx.setFormulaId(rs.getString("v_formula_id"));
                fx.setFormulaName(rs.getString("v_formula_name"));
                fx.setFormulaText(lobHandler.getClobAsString(rs, "c_formula_string"));
                fx.setAggregateType(rs.getString("v_aggregation_type"));
                fx.setFormulaType(rs.getString("v_formula_type"));
                return fx;
            }
        });
        return fx;
    }

    @Override
    public void deleteFx(String formulaId) {
        String query = "DELETE FROM CNFG_FORMULA_TABLE WHERE v_formula_id = ?";
        getJdbcTemplate().update(query, new Object[]{formulaId});
    }

    @Override
    public void updateFx(RulesFxGridModel expModel) {
        String query = "UPDATE CNFG_FORMULA_TABLE SET v_aggregation_type = ?, c_formula_string = ? "
                + "WHERE v_formula_id = ?";
        getJdbcTemplate().update(query, new Object[]{expModel.getAggregateType(),
                    new SqlLobValue(expModel.getFormulaText(), lobHandler), expModel.getFormulaId()},
                new int[]{Types.VARCHAR, Types.CLOB, Types.VARCHAR});
    }
}