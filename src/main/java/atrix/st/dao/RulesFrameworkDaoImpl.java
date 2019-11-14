/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.model.QueryBuilderModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import atrix.st.model.RulesFrameworkModel;
import atrix.st.model.RulesOptionModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("rulesFrameworkDao")
public class RulesFrameworkDaoImpl extends JdbcDaoSupport implements RulesFrameworkDao {

    @Autowired
    RulesFrameworkDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    private @Value("${jdbc.username}")
    String schemaName;    
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    private static final Logger log = Logger.getLogger(RulesFrameworkDaoImpl.class);

    @Override
    public RulesFrameworkModel getRule(String ruleName) {
        String sql = "SELECT v_rule_name, v_update_table, v_s_table1, v_s_column1, v_bc1, v_s_table2, v_s_column2, v_bc2, "
                + "v_s_table3, v_s_column3, v_bc3, v_s_table4, v_s_column4, v_bc4, v_s_table5, v_s_column5, v_bc5, "
                + "v_d_table, v_d_column, decode(v_s_table1,null,0,1) + decode(v_s_table2,null,0,1) + "
                + "decode(v_s_table3,null,0,1) + decode(v_s_table4,null,0,1) + decode(v_s_table5,null,0,1) v_col_count, "
                + "v_rule_category, v_rule_desc "
                + "FROM CNFG_RULE_MASTER WHERE v_rule_name = ?";
        RulesFrameworkModel rule = (RulesFrameworkModel) getJdbcTemplate().queryForObject(
                sql, new Object[]{ruleName}, new RowMapper<RulesFrameworkModel>() {

            @Override
            public RulesFrameworkModel mapRow(ResultSet rs, int i) throws SQLException {
                RulesFrameworkModel rule = new RulesFrameworkModel();
                rule.setRuleName(rs.getString("v_rule_name"));
                rule.setUpdateTable(rs.getString("v_update_table"));
                rule.setsTable1(rs.getString("v_s_table1"));
                rule.setsColumn1(rs.getString("v_s_column1"));
                rule.setBc1(rs.getString("v_bc1"));
                rule.setsTable2(rs.getString("v_s_table2"));
                rule.setsColumn2(rs.getString("v_s_column2"));
                rule.setBc2(rs.getString("v_bc2"));
                rule.setsTable3(rs.getString("v_s_table3"));
                rule.setsColumn3(rs.getString("v_s_column3"));
                rule.setBc3(rs.getString("v_bc3"));
                rule.setsTable4(rs.getString("v_s_table4"));
                rule.setsColumn4(rs.getString("v_s_column4"));
                rule.setBc4(rs.getString("v_bc4"));
                rule.setsTable5(rs.getString("v_s_table5"));
                rule.setsColumn5(rs.getString("v_s_column5"));
                rule.setBc5(rs.getString("v_bc5"));
                rule.setdTable(rs.getString("v_d_table"));
                rule.setdColumn(rs.getString("v_d_column"));
                rule.setColCount(rs.getInt("v_col_count"));
                rule.setRuleCat(rs.getString("v_rule_category"));
                rule.setRuleDesc(rs.getString("v_rule_desc"));
                return rule;
            }
        });
        return rule;
    }

    @Override
    public List<RulesOptionModel> listTables(String option) {
        String query;
        List<Map<String, Object>> rows;
        if (option.equals("result")) {
            query = "SELECT distinct v_table_name, v_table_desc "
                    + "FROM CNFG_MODEL_MASTER WHERE substr(v_table_name,1,4) = ? ORDER BY 1";
            rows = getJdbcTemplate().queryForList(query, new Object[]{"RSLT"});
        } else if (option.equals("master")) {
            query = "SELECT distinct v_table_name, v_table_desc "
                    + "FROM CNFG_MODEL_MASTER WHERE substr(v_table_name,1,3) = ? ORDER BY 1";
            rows = getJdbcTemplate().queryForList(query, new Object[]{"MST"});
        } else {
            query = "SELECT distinct v_table_name, v_table_desc "
                    + "FROM CNFG_MODEL_MASTER WHERE substr(v_table_name,1,4) = ? "
                    + "OR substr(v_table_name,1,3) = ? ORDER BY 1";
            rows = getJdbcTemplate().queryForList(query, new Object[]{"RSLT", "MST"});
        }
        List<RulesOptionModel> ltable = new ArrayList<RulesOptionModel>();
        for (Map row : rows) {
            RulesOptionModel obj = new RulesOptionModel();
            obj.setCode(fmt.ToString(row.get("v_table_name")));
            obj.setDesc(fmt.ToString(row.get("v_table_desc")));
            ltable.add(obj);
        }
        return ltable;
    }

    @Override
    public List<RulesOptionModel> listColumns(String table) {
        String query = "SELECT v_code_column, v_desc_column FROM CNFG_MODEL_MASTER "
                + "WHERE v_table_name = ? ORDER BY 2";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{table});
        List<RulesOptionModel> lcolumn = new ArrayList<RulesOptionModel>();
        for (Map row : rows) {
            RulesOptionModel obj = new RulesOptionModel();
            obj.setCode(fmt.ToString(row.get("v_code_column")));
            obj.setDesc(fmt.ToString(row.get("v_desc_column")));
            lcolumn.add(obj);
        }
        return lcolumn;
    }

    @Override
    public List<RulesOptionModel> listPkColumns(String table) {
        String query = "SELECT v_code_column, v_desc_column FROM CNFG_MODEL_MASTER WHERE v_table_name = ? AND f_pk_flag = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{table, "Y"});
        List<RulesOptionModel> lcolumn = new ArrayList<RulesOptionModel>();
        for (Map row : rows) {
            RulesOptionModel obj = new RulesOptionModel();
            obj.setCode(fmt.ToString(row.get("v_code_column")));
            obj.setDesc(fmt.ToString(row.get("v_desc_column")));
            lcolumn.add(obj);
        }
        return lcolumn;
    }

    @Override
    public String listConditions(String tab, String col) {
        String query = "SELECT v_data_type FROM CNFG_MODEL_MASTER "
                + "WHERE v_table_name = ? AND v_code_column = ?";
        return getJdbcTemplate().queryForObject(query, new Object[]{tab, col}, String.class);
    }

    @Override
    public Integer checkRuleName(String ruleName) {
        String sql = "SELECT count(*) FROM CNFG_RULE_MASTER WHERE v_rule_name = ?";
        Integer val = getJdbcTemplate().queryForObject(sql, new Object[]{ruleName},Integer.class);
        return val;
    }

    @Override
    public Integer addRule(RulesFrameworkModel rulesModel, String audCd) {
        String query;
        Integer val = 1;
        try {
            query = "INSERT INTO CNFG_RULE_MASTER (v_rule_name, v_update_table, v_s_table1, v_s_column1, v_bc1, "
                    + "v_s_table2, v_s_column2, v_bc2, v_s_table3, v_s_column3, v_bc3, v_s_table4, v_s_column4, "
                    + "v_bc4, v_s_table5, v_s_column5, v_bc5, v_d_table, v_d_column, v_rule_type, v_rule_category, "
                    + "d_created, v_audit_cd, v_rule_desc) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, ?, ?)";
            getJdbcTemplate().update(query, new Object[]{
                        rulesModel.getRuleName(), rulesModel.getUpdateTable(),
                        rulesModel.getsTable1(), rulesModel.getsColumn1(), rulesModel.getBc1(),
                        rulesModel.getsTable2(), rulesModel.getsColumn2(), rulesModel.getBc2(),
                        rulesModel.getsTable3(), rulesModel.getsColumn3(), rulesModel.getBc3(),
                        rulesModel.getsTable4(), rulesModel.getsColumn4(), rulesModel.getBc4(),
                        rulesModel.getsTable5(), rulesModel.getsColumn5(), rulesModel.getBc5(),
                        rulesModel.getdTable(), rulesModel.getdColumn(), "UDF", rulesModel.getRuleCat(),
                        audCd, rulesModel.getRuleDesc()
                    });
        } catch (Exception e) {
            log.debug(e);
            val = 0;
        }
        return val;
    }
    
    @Override
    public Integer modifyRule(RulesFrameworkModel rulesModel, String audCd) {
        String query;
        Integer val = 1;
        try {
            query = "UPDATE CNFG_RULE_MASTER SET v_update_table = ?, v_s_table1 = ?, v_s_column1 = ?, v_bc1 = ?, "
                    + "v_s_table2 = ?, v_s_column2 = ?, v_bc2 = ?, v_s_table3 = ?, v_s_column3 = ?, v_bc3 = ?, "
                    + "v_s_table4 = ?, v_s_column4 = ?, v_bc4 = ?, v_s_table5 = ?, v_s_column5 = ?, v_bc5 = ?, "
                    + "v_d_table = ?, v_d_column = ?, v_rule_type = ?, v_rule_category = ?, d_created = sysdate, "
                    + "v_audit_cd = ?, v_rule_desc = ? "
                    + "WHERE v_rule_name = ?";
            getJdbcTemplate().update(query, new Object[]{
                        rulesModel.getUpdateTable(), rulesModel.getsTable1(), rulesModel.getsColumn1(), rulesModel.getBc1(),
                        rulesModel.getsTable2(), rulesModel.getsColumn2(), rulesModel.getBc2(),
                        rulesModel.getsTable3(), rulesModel.getsColumn3(), rulesModel.getBc3(),
                        rulesModel.getsTable4(), rulesModel.getsColumn4(), rulesModel.getBc4(),
                        rulesModel.getsTable5(), rulesModel.getsColumn5(), rulesModel.getBc5(),
                        rulesModel.getdTable(), rulesModel.getdColumn(), "UDF", rulesModel.getRuleCat(),
                        audCd, rulesModel.getRuleDesc(), rulesModel.getRuleName()
                    });
        } catch (Exception e) {
            log.debug(e);
            val = 0;
        }
        return val;
    }

    @Override
    public void copyRuleMaster(RulesFrameworkModel model, String ruleName, String audCd) {
        String query = "INSERT INTO CNFG_RULE_MASTER (v_rule_name, v_update_table, v_s_table1, v_s_column1, v_bc1, "
                + "v_s_table2, v_s_column2, v_bc2, v_s_table3, v_s_column3, v_bc3, v_s_table4, v_s_column4, v_bc4, "
                + "v_s_table5, v_s_column5, v_bc5, v_d_table, v_d_column, v_rule_type, v_rule_category, d_created, "
                + "v_audit_cd, v_rule_desc) "
                + "SELECT '" + model.getRuleName() + "', v_update_table, v_s_table1, v_s_column1, v_bc1, "
                + "v_s_table2, v_s_column2, v_bc2, v_s_table3, v_s_column3, v_bc3, v_s_table4, v_s_column4, v_bc4, "
                + "v_s_table5, v_s_column5, v_bc5, v_d_table, v_d_column, v_rule_type, v_rule_category, sysdate, "
                + "'" + audCd + "', '" + model.getRuleDesc() + "'"
                + "FROM CNFG_RULE_MASTER WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{ruleName});
    }

    @Override
    public GridPage<RulesFrameworkModel> listRules(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type) {
        String query1, query2, formulaType;
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_rule_name", "d_created",
                "d_modified", "d_commit"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_rule_name";
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
        query1 = "SELECT count(*) FROM CNFG_RULE_MASTER WHERE v_rule_type IN (" + formulaType + ") " + qObj.getCondition();
        query2 = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_rule_name, case when v_rule_category = 'VM' then 'Transition' else 'Sensitivity' end "
                + "v_rule_category, case when v_rule_type = 'PDF' then 'Predefined' else 'User defined' end v_rule_type, "
                + "to_char(d_created,'yyyy-mm-dd hh:mm:ss') d_created, to_char(d_modified,'yyyy-mm-dd hh:mm:ss') d_modified, "
                + "to_char(d_commit,'yyyy-mm-dd hh:mm:ss') d_commit "
                + "FROM CNFG_RULE_MASTER WHERE v_rule_type IN (" + formulaType + ") "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        int rowCount = getJdbcTemplate().queryForObject(query1, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        List<RulesFrameworkModel> rules = new ArrayList<RulesFrameworkModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query2, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            RulesFrameworkModel rule = new RulesFrameworkModel();
            rule.setRuleName(fmt.ToString(row.get("v_rule_name")));
            rule.setRuleCat(fmt.ToString(row.get("v_rule_category")));
            rule.setRuleType(fmt.ToString(row.get("v_rule_type")));
            rule.setdCreated(fmt.ToString(row.get("d_created")));
            rule.setdModified(fmt.ToString(row.get("d_modified")));
            rule.setdCommit(fmt.ToString(row.get("d_commit")));
            rules.add(rule);
        }
        return new GridPage<RulesFrameworkModel>(rules, page, max, rowCount);
    }

    @Override
    public void deleteRule(String rulename) {
        String query = "DELETE FROM CNFG_RULE_MASTER WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{rulename});
    }

    @Override
    public String commitRule(String rulename) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withFunctionName("CNFG_RULE_SCRIPT_SYS")
                .withReturnValue()
                .useInParameterNames("lv_task_nm")
                .declareParameters(new SqlParameter("lv_task_nm", java.sql.Types.VARCHAR));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lv_task_nm", rulename);
        return jdbcCall.executeFunction(String.class, map);
    }

    @Override
    public void updateCommitDate(String rulename, String audseq) {
        String query = "UPDATE CNFG_RULE_MASTER SET d_commit = sysdate, v_audit_cd = ? "
                + "WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{audseq, rulename});
    }    
}