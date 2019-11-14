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
import atrix.st.model.MastersModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("mastersDao")
public class MastersDaoImpl extends JdbcDaoSupport implements MastersDao {

    @Autowired
    MastersDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;

    @Override
    public MastersModel getCurrLocalization() {
        String sql = "SELECT n_units, v_prefix FROM CNFG_CURRENCY_LOCALIZATION";
        MastersModel mst = new MastersModel();
        try {
            mst = getJdbcTemplate().queryForObject(
                    sql, new Object[]{}, new RowMapper<MastersModel>() {
                        @Override
                        public MastersModel mapRow(ResultSet rs, int i) throws SQLException {
                            MastersModel obj = new MastersModel();
                            obj.setCol1(rs.getString(1));
                            obj.setCol2(rs.getString(2));
                            return obj;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            mst.setCol1("");
            mst.setCol2("");
        } catch (IncorrectResultSizeDataAccessException e) {
            mst.setCol1("");
            mst.setCol2("");
        }
        return mst;
    }

    @Override
    public void updateCurrLocalization(MastersModel model) {
        String query = "UPDATE CNFG_CURRENCY_LOCALIZATION SET n_units = ?, v_prefix = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol1(), model.getCol2()});
    }
    
    @Override
    public Map<String, String> getMasterTables(String view) {
        String query;
        if(view.equals("edit")) {
            query = "SELECT v_tab_name, v_tab_desc FROM CNFG_MASTER_DATA WHERE f_active = 'Y' AND f_edit = 'Y'";
        } else {
            query = "SELECT v_tab_name, v_tab_desc FROM CNFG_MASTER_DATA WHERE f_active = 'Y' AND f_view = 'Y'";
        }
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        Map<String, String> options = new LinkedHashMap<String, String>();
        for (Map row : rows) {
            options.put(fmt.ToString(row.get("v_tab_name")), fmt.ToString(row.get("v_tab_desc")));
        }
        return options;
    }
    
    @Override
    public GridPage<MastersModel> listOtherIncome(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("oth_income_type", "ci_ratio",
                "tnfr_rate"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "oth_income_type";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_OTH_INCOME " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT oth_income_type, ci_ratio, tnfr_rate "
                + "FROM MST_OTH_INCOME "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setPk(fmt.ToString(row.get("oth_income_type")));
            mst.setCol1(fmt.ToString(row.get("oth_income_type")));
            mst.setCol2(fmt.ToString(row.get("ci_ratio")));
            mst.setCol3(fmt.ToString(row.get("tnfr_rate")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }

    @Override
    public void updateOtherIncome(MastersModel model) {
        String query = "UPDATE MST_OTH_INCOME SET ci_ratio = ?, tnfr_rate= ? "
                + "WHERE oth_income_type = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol2(), model.getCol3(), model.getPk()});
    }
    
    @Override
    public GridPage<MastersModel> listCCF(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_code", "v_facility_type",
                "n_limit_lower", "n_limit_upper", "n_utilization_lower", "n_utilization_upper", "n_drawn_ccf",
                "n_undrawn_ccf"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_code";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_CCF " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_code, v_facility_type, n_limit_lower, n_limit_upper, n_utilization_lower, "
                + "n_utilization_upper, n_drawn_ccf, n_undrawn_ccf "
                + "FROM MST_CCF "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setCol1(fmt.ToString(row.get("v_code")));
            mst.setCol2(fmt.ToString(row.get("v_facility_type")));
            mst.setCol3(fmt.ToString(row.get("n_limit_lower")));
            mst.setCol4(fmt.ToString(row.get("n_limit_upper")));
            mst.setCol5(fmt.ToString(row.get("n_utilization_lower")));
            mst.setCol6(fmt.ToString(row.get("n_utilization_upper")));
            mst.setCol7(fmt.ToString(row.get("n_drawn_ccf")));
            mst.setCol8(fmt.ToString(row.get("n_undrawn_ccf")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }
    
    @Override
    public void updateCCF(MastersModel model) {
        String query = "UPDATE MST_CCF SET n_drawn_ccf = ?, n_undrawn_ccf_lte_1 = ?, n_undrawn_ccf_gt_1 = ?, "
                + "v_facility_type = ? "
                + "WHERE v_code = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol2(), model.getCol3(), model.getCol4(),
                model.getCol5(), model.getPk()});
    }
    
    @Override
    public GridPage<MastersModel> listRaroc(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("n_code", "v_code", "n_value"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_code";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_RAROC " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT n_code, v_code, n_value "
                + "FROM MST_RAROC "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setPk(fmt.ToString(row.get("n_code")));
            mst.setCol1(fmt.ToString(row.get("v_code")));
            mst.setCol4(fmt.ToString(row.get("n_value")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }
    
    @Override
    public void updateRaroc(MastersModel model) {
        String query = "UPDATE MST_RAROC SET n_value = ?, d_mis_date = sysdate "
                + "WHERE n_code = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol4(), model.getPk()});
    }
    
    @Override
    public GridPage<MastersModel> listGuarRw(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_code", "n_value"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_code";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_GUARANTOR " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_code, n_value "
                + "FROM MST_GUARANTOR "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setPk(fmt.ToString(row.get("v_code")));
            mst.setCol1(fmt.ToString(row.get("v_code")));
            mst.setCol2(fmt.ToString(row.get("n_value")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }
    
    @Override
    public void updateGuarRw(MastersModel model) {
        String query = "UPDATE MST_GUARANTOR SET n_value = ? WHERE v_code = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol2(), model.getPk()});
    }
    
    @Override
    public GridPage<MastersModel> listRestRw(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_code", "n_rw"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_code";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_RESTRUCTURED_RW " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_code, n_rw "
                + "FROM MST_RESTRUCTURED_RW "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setPk(fmt.ToString(row.get("v_code")));
            mst.setCol1(fmt.ToString(row.get("v_code")));
            mst.setCol2(fmt.ToString(row.get("n_rw")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }
    
    @Override
    public void updateRestRw(MastersModel model) {
        String query = "UPDATE MST_RESTRUCTURED_RW SET n_rw = ? WHERE v_code = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol2(), model.getPk()});
    }
    
    @Override
    public GridPage<MastersModel> listOpex(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_facility", "v_segment",
                "n_lower_level", "n_upper_level", "n_fixed_cost", "n_variable_cost"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_facility";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);        
        String query = "SELECT count(*) FROM MST_OPERATING_EXPENSE " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_facility||v_segment||n_lower_level||n_upper_level pk, "
                + "v_facility, v_segment, n_lower_level, n_upper_level, n_fixed_cost, n_variable_cost "
                + "FROM MST_OPERATING_EXPENSE "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";        
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setPk(fmt.ToString(row.get("pk")));
            mst.setCol1(fmt.ToString(row.get("v_facility")));
            mst.setCol2(fmt.ToString(row.get("v_segment")));
            mst.setCol3(fmt.ToString(row.get("n_lower_level")));
            mst.setCol4(fmt.ToString(row.get("n_upper_level")));
            mst.setCol5(fmt.ToString(row.get("n_fixed_cost")));
            mst.setCol6(fmt.ToString(row.get("n_variable_cost")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }
    
    @Override
    public void updateOpex(MastersModel model) {
        String query = "UPDATE MST_OPERATING_EXPENSE SET v_facility = ?, v_segment = ?, "
                + "n_lower_level = ?, n_upper_level = ?, n_fixed_cost = ?, n_variable_cost = ? "
                + "WHERE v_facility||v_segment||n_lower_level||n_upper_level = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol1(), model.getCol2(), model.getCol3(),
                    model.getCol4(), model.getCol5(), model.getCol6(), model.getPk()});
    }
    
    @Override
    public void insertOpex(MastersModel model) {
        String query = "INSERT INTO MST_OPERATING_EXPENSE "
                + "(v_facility, v_segment, n_lower_level, n_upper_level, n_fixed_cost, n_variable_cost) "
                + " VALUES (?, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{model.getCol1(), model.getCol2(), model.getCol3(),
                    model.getCol4(), model.getCol5(), model.getCol6()});
    }

    @Override
    public void deleteOpex(String pk) {
        String query = "DELETE FROM MST_OPERATING_EXPENSE "
                + "WHERE v_facility||v_segment||n_lower_level||n_upper_level = ?";
        getJdbcTemplate().update(query, new Object[]{pk});
    }
    
    @Override
    public GridPage<MastersModel> listSensitivity(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("n_value", "v_type"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_type";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_SENSITIVITY_ITERATIONS " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT n_value||v_type pk, n_value, v_type "
                + "FROM MST_SENSITIVITY_ITERATIONS "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<MastersModel> masters = new ArrayList<MastersModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            MastersModel mst = new MastersModel();
            mst.setId(fmt.ToString(row.get("rnum")));
            mst.setPk(fmt.ToString(row.get("pk")));
            mst.setCol1(fmt.ToString(row.get("n_value")));
            mst.setCol2(fmt.ToString(row.get("v_type")));
            masters.add(mst);
        }
        return new GridPage<MastersModel>(masters, page, max, rowCount);
    }
    
    @Override
    public void updateSensitivity(MastersModel model) {
        String query = "UPDATE MST_SENSITIVITY_ITERATIONS SET n_value = ?, v_type = ? "
                + "WHERE n_value||v_type = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCol1(), model.getCol2(), model.getPk()});
    }
    
    @Override
    public void insertSensitivity(MastersModel model) {
        String query = "INSERT INTO MST_SENSITIVITY_ITERATIONS (n_value, v_type) "
                + " VALUES (?, ?)";
        getJdbcTemplate().update(query, new Object[]{model.getCol1(), model.getCol2()});
    }

    @Override
    public void deleteSensitivity(String pk) {
        String query = "DELETE FROM MST_SENSITIVITY_ITERATIONS "
                + "WHERE n_value||v_type = ?";
        getJdbcTemplate().update(query, new Object[]{pk});
    }
}