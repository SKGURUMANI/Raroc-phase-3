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
import atrix.common.util.DataTablesResponse;
import atrix.common.util.GridPage;
import atrix.common.model.DataTypeModel;
import atrix.common.util.GridWithColType;
import atrix.st.model.MstExecutionModel;
import atrix.st.model.ReportModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
@Repository("reportDao")
public class ReportDaoImpl extends JdbcDaoSupport implements ReportDao {
    
    @Autowired
    ReportDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    
    @Override
    public DataTablesResponse<MstExecutionModel> listMstExecution(Integer sEcho, Integer start, Integer length, Integer sidx,
            String sord, String search) {
        int startidx = start + 1, endidx, rowCount, displayCount;
        String query, sortText;
        List<Map<String, Object>> rows;
        List<MstExecutionModel> ids = new ArrayList<MstExecutionModel>();
        String sql = "SELECT count(distinct v_execution_id) FROM MST_EXECUTION";
        rowCount = getJdbcTemplate().queryForObject(sql,Integer.class);
        displayCount = rowCount;
        if (rowCount < start + length) {
            endidx = rowCount;
        } else {
            endidx = start + length;
        }
        if (sidx == 0) {
            sortText = "m.v_execution_id";
        } else if (sidx == 1) {
            sortText = "m.v_execution_name";
        } else if (sidx == 2) {
            sortText = "c.v_job_name";
        } else if (sidx == 3) {
            sortText = "to_date(m.d_information_date)";
        } else {
            sortText = "to_date(m.d_execution_date)";
        }
        if (search == null || search.equals("")) {
            query = "SELECT * FROM "
                    + "(SELECT a.*, rownum rnum FROM "
                    + "(SELECT m.v_execution_id, m.v_execution_name, c.v_job_name, "
                    + "to_char(m.d_information_date,'dd-Mon-yyyy') info_date, "
                    + "to_char(m.d_execution_date,'dd-Mon-yyyy') exec_date "
                    + "FROM MST_EXECUTION m, CNFG_JOB_MASTER c "
                    + "WHERE c.v_job_id = m.v_job_id "
                    + "ORDER BY " + sortText + " " + sord + ") a "
                    + "WHERE rownum <= ?) WHERE rnum >= ?";
            rows = getJdbcTemplate().queryForList(query, new Object[]{endidx, startidx});
        } else {
            query = "SELECT count(distinct m.v_execution_id) rowCount "
                    + "FROM MST_EXECUTION m, CNFG_JOB_MASTER c "
                    + "WHERE c.v_job_id = m.v_job_id "
                    + "AND (upper(m.v_execution_id) like upper('%" + search + "%') "
                    + "OR upper(m.v_execution_name) like upper('%" + search + "%') "
                    + "OR upper(c.v_job_name) like upper('%" + search + "%') "
                    + "OR to_char(m.d_information_date,'dd-MON-yyyy') like upper('%" + search + "%') "
                    + "OR to_char(m.d_execution_date,'dd-MON-yyyy') like upper('%" + search + "%'))";
            displayCount = getJdbcTemplate().queryForObject(query,Integer.class);
            query = "SELECT * FROM "
                    + "(SELECT a.*, rownum rnum FROM "
                    + "(SELECT m.v_execution_id, m.v_execution_name, c.v_job_name, "
                    + "to_char(m.d_information_date,'dd-Mon-yyyy') info_date, "
                    + "to_char(m.d_execution_date,'dd-Mon-yyyy') exec_date "
                    + "FROM MST_EXECUTION m, CNFG_JOB_MASTER c "
                    + "WHERE c.v_job_id = m.v_job_id "
                    + "AND (upper(m.v_execution_id) like upper('%" + search + "%') "
                    + "OR upper(m.v_execution_name) like upper('%" + search + "%') "
                    + "OR upper(c.v_job_name) like upper('%" + search + "%') "
                    + "OR to_char(m.d_information_date,'dd-MON-yyyy') like upper('%" + search + "%') "
                    + "OR to_char(m.d_execution_date,'dd-MON-yyyy') like upper('%" + search + "%'))"
                    + "ORDER BY " + sortText + " " + sord + ") a "
                    + "WHERE rownum <= ?) WHERE rnum >= ?";
            rows = getJdbcTemplate().queryForList(query, new Object[]{endidx, startidx});
        }
        for (Map row : rows) {
            MstExecutionModel id = new MstExecutionModel();
            id.setExecutionId((String) row.get("v_execution_id"));
            id.setExecutionName((String) row.get("v_execution_name"));
            id.setJobName((String) row.get("v_job_name"));
            id.setInfoDate((String) row.get("info_date"));
            id.setExecDate((String) row.get("exec_date"));
            ids.add(id);
        }
        return new DataTablesResponse<MstExecutionModel>(sEcho, rowCount, displayCount, ids);
    }
    
    @Override
    public String getExecutionName(String execId) {
        String sql = "SELECT v_execution_name FROM MST_EXECUTION WHERE v_execution_id = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, new Object[]{execId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return "error:2004";
        } catch (IncorrectResultSizeDataAccessException e) {
            return "error:2005";
        }       
    }
    
    @Override
    public GridPage<ReportModel> listTests(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String id) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("d_mis_date", "v_scenario_name", "v_portfolio_name", 
                "v_stress_level", "n_ps_stressed_ead", "n_ps_stressed_rwa", "n_ps_stressed_gnpa", "n_ps_stressed_provision",
                "n_ps_base_ead", "n_ps_base_rwa", "n_ps_base_gnpa", "n_ps_base_provision", "n_incr_ead", "n_incr_rwa",
                "n_incr_gnpa", "n_incr_provision", "n_ta_base_ead", "n_ta_base_rwa", "n_ta_base_gnpa", "n_ta_base_prov",
                "n_ta_stressed_ead", "n_ta_stressed_rwa", "n_ta_stressed_gnpa", "n_ta_stressed_prov", "n_base_t1_cap",
                "n_base_t2_cap", "n_base_total_cap", "n_stressed_t1_cap", "n_stressed_t2_cap", "n_stressed_tot_cap",
                "n_base_t1_crar", "n_base_crar", "n_stressed_t1_crar", "n_stressed_crar"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_scenario_name";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM RSLT_STRESS_TEST WHERE v_execution_id = ? " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{id, qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT to_char(d_mis_date,'dd-Mon-yyyy') d_mis_date, v_scenario_name, v_portfolio_name, "
                + "v_stress_level, n_ps_stressed_ead, n_ps_stressed_rwa, n_ps_stressed_gnpa, n_ps_stressed_provision, "
                + "n_ps_base_ead, n_ps_base_rwa, n_ps_base_gnpa, n_ps_base_provision, n_incr_ead, n_incr_rwa, "
                + "n_incr_gnpa, n_incr_provision, n_ta_base_ead, n_ta_base_rwa, n_ta_base_gnpa, n_ta_base_prov, "
                + "n_ta_stressed_ead, n_ta_stressed_rwa, n_ta_stressed_gnpa, n_ta_stressed_prov, n_base_t1_cap, "
                + "n_base_t2_cap, n_base_total_cap, n_stressed_t1_cap, n_stressed_t2_cap, n_stressed_tot_cap, "
                + "n_base_t1_crar, n_base_crar, n_stressed_t1_crar, n_stressed_crar "
                + "FROM RSLT_STRESS_TEST "
                + "WHERE v_execution_id = ? "
                + qObj.getCondition() + " "
                + "ORDER BY to_char(" + sidx + ") " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";         
        List<ReportModel> data = getJdbcTemplate().query(query, 
                new Object[]{id, qObj.getRegex(), endIdx, startIdx}, new RowMapper<ReportModel>() {
            @Override
            public ReportModel mapRow(ResultSet rs, int i) throws SQLException {
                ReportModel row = new ReportModel();
                row.setId(rs.getString("rnum"));
                row.setCol1(rs.getString("d_mis_date"));
                row.setCol2(rs.getString("v_scenario_name"));
                row.setCol3(rs.getString("v_portfolio_name"));
                row.setCol4(rs.getString("v_stress_level"));
                row.setCol5(rs.getString("n_ps_stressed_ead"));
                row.setCol6(rs.getString("n_ps_stressed_rwa"));
                row.setCol7(rs.getString("n_ps_stressed_gnpa"));
                row.setCol8(rs.getString("n_ps_stressed_provision"));
                row.setCol9(rs.getString("n_ps_base_ead"));
                row.setCol10(rs.getString("n_ps_base_rwa"));
                row.setCol11(rs.getString("n_ps_base_gnpa"));
                row.setCol12(rs.getString("n_ps_base_provision"));
                row.setCol13(rs.getString("n_incr_ead"));
                row.setCol14(rs.getString("n_incr_rwa"));
                row.setCol15(rs.getString("n_incr_gnpa"));
                row.setCol16(rs.getString("n_incr_provision"));
                row.setCol17(rs.getString("n_ta_base_ead"));
                row.setCol18(rs.getString("n_ta_base_rwa"));
                row.setCol19(rs.getString("n_ta_base_gnpa"));
                row.setCol20(rs.getString("n_ta_base_prov"));
                row.setCol21(rs.getString("n_ta_stressed_ead"));
                row.setCol22(rs.getString("n_ta_stressed_rwa"));
                row.setCol23(rs.getString("n_ta_stressed_gnpa"));
                row.setCol24(rs.getString("n_ta_stressed_prov"));
                row.setCol25(rs.getString("n_base_t1_cap"));
                row.setCol26(rs.getString("n_base_t2_cap"));
                row.setCol27(rs.getString("n_base_total_cap"));
                row.setCol28(rs.getString("n_stressed_t1_cap"));
                row.setCol29(rs.getString("n_stressed_t2_cap"));
                row.setCol30(rs.getString("n_stressed_tot_cap"));
                row.setCol31(rs.getString("n_base_t1_crar"));
                row.setCol32(rs.getString("n_base_crar"));
                row.setCol33(rs.getString("n_stressed_t1_crar"));
                row.setCol34(rs.getString("n_stressed_crar"));
                return row;
            }
        });
        return new GridPage<ReportModel>(data, page, max, rowCount);
    }
    
    @Override
    public GridWithColType<ReportModel> listDetails(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String id) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("d_mis_date", "v_scenario_name", "v_scenario_details",
                "v_portfolio_name", "v_stress_level", "n_s_ead", "n_s_rwa", "n_s_gnpa", "n_s_provision", "n_b_ead", "n_b_rwa", 
                "n_b_gnpa", "n_b_provision"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_scenario_name";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM RSLT_STRESS_TEST_DETAILS WHERE v_execution_id = ? " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{id, qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT to_char(d_mis_date,'dd-Mon-yyyy') d_mis_date, v_scenario_name, v_scenario_details, "
                + "v_portfolio_name, v_stress_level, n_s_ead, n_s_rwa, n_s_gnpa, n_s_provision, n_b_ead, n_b_rwa, "
                + "n_b_gnpa, n_b_provision "
                + "FROM RSLT_STRESS_TEST_DETAILS "
                + "WHERE v_execution_id = ? "
                + qObj.getCondition() + " "
                + "ORDER BY to_char(" + sidx + ") " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        final DataTypeModel dm = new DataTypeModel();
        List<ReportModel> data = getJdbcTemplate().query(query,
                new Object[]{id, qObj.getRegex(), endIdx, startIdx}, new RowMapper<ReportModel>() {
            @Override
            public ReportModel mapRow(ResultSet rs, int i) throws SQLException {
                ReportModel row = new ReportModel();
                ResultSetMetaData md = rs.getMetaData();                
                row.setId(rs.getString("rnum"));
                row.setCol1(rs.getString("d_mis_date"));
                dm.setColType1(md.getColumnTypeName(1));
                row.setCol2(rs.getString("v_scenario_name"));
                dm.setColType2(md.getColumnTypeName(2));
                row.setCol3(rs.getString("v_scenario_details"));
                dm.setColType3(md.getColumnTypeName(3));
                row.setCol4(rs.getString("v_portfolio_name"));
                dm.setColType4(md.getColumnTypeName(4));
                row.setCol5(rs.getString("v_stress_level"));
                dm.setColType5(md.getColumnTypeName(5));
                row.setCol6(rs.getString("n_s_ead"));
                dm.setColType6(md.getColumnTypeName(6));
                row.setCol7(rs.getString("n_s_rwa"));
                dm.setColType7(md.getColumnTypeName(7));
                row.setCol8(rs.getString("n_s_gnpa"));
                dm.setColType8(md.getColumnTypeName(8));
                row.setCol9(rs.getString("n_s_provision"));
                dm.setColType9(md.getColumnTypeName(9));
                row.setCol10(rs.getString("n_b_ead"));
                dm.setColType10(md.getColumnTypeName(10));
                row.setCol11(rs.getString("n_b_rwa"));
                dm.setColType11(md.getColumnTypeName(11));
                row.setCol12(rs.getString("n_b_gnpa"));
                dm.setColType12(md.getColumnTypeName(12));
                row.setCol13(rs.getString("n_b_provision"));
                dm.setColType13(md.getColumnTypeName(13));
                return row;
            }
        });
        return new GridWithColType<ReportModel>(data, dm, page, max, rowCount);
    }
}