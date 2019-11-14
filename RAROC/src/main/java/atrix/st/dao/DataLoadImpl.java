/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.model.QueryBuilderModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import atrix.st.model.DataLoadModel;
import atrix.st.model.DataQualityModel;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("dataLoadDao")
public class DataLoadImpl extends JdbcDaoSupport implements DataLoadDao {

    @Autowired
    DataLoadImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    
    @Override
    public GridPage<DataLoadModel> listTransformations(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_trans_name", "v_trans_type",
                "v_desc"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_trans_name";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM CNFG_TRANSFORMATIONS " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_trans_name, v_trans_type, v_desc "
                + "FROM CNFG_TRANSFORMATIONS "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<DataLoadModel> ktrs = new ArrayList<DataLoadModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), 
            endIdx, startIdx});
        for (Map row : rows) {
            DataLoadModel ktr = new DataLoadModel();
            ktr.setId(fmt.ToString(row.get("rnum")));
            ktr.setPk(fmt.ToString(row.get("v_trans_name")));
            ktr.setTransName(fmt.ToString(row.get("v_trans_name")));
            ktr.setTransType(fmt.ToString(row.get("v_trans_type")));
            ktr.setDesc(fmt.ToString(row.get("v_desc")));
            ktrs.add(ktr);
        }
        return new GridPage<DataLoadModel>(ktrs, page, max, rowCount);
    }        
    
    @Override
    public void addTrans(DataLoadModel model) {
        String query = "INSERT INTO CNFG_TRANSFORMATIONS (v_trans_name, v_trans_type, v_desc)"
                + " VALUES(?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{model.getTransName(), model.getTransType(), 
            model.getDesc()});
    }
    
    @Override
    public void deleteTrans(String pk) {
        String query = "DELETE FROM CNFG_TRANSFORMATIONS WHERE v_trans_name = ?";
        getJdbcTemplate().update(query, new Object[]{pk});
    }        
    
    @Override
    public GridPage<DataLoadModel> listTransformationLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("transname", "status"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "id_batch";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM LOG_DATA_LOAD " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT id_batch, transname, status, lines_input, lines_output, lines_rejected, errors, "
                + "replaydate, logdate "
                + "FROM LOG_DATA_LOAD "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<DataLoadModel> list = new ArrayList<DataLoadModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), 
            endIdx, startIdx});
        for (Map row : rows) {
            DataLoadModel obj = new DataLoadModel();
            obj.setId(fmt.ToString(row.get("id_batch")));            
            obj.setTransName(fmt.ToString(row.get("transname")));
            obj.setDesc(fmt.ToString(row.get("status")));
            obj.setInput(fmt.ToInteger(row.get("lines_input")));
            obj.setOutput(fmt.ToInteger(row.get("lines_output")));
            obj.setRejected(fmt.ToInteger(row.get("lines_rejected")));
            obj.setErrors(fmt.ToInteger(row.get("errors")));
            obj.setStartDate(fmt.ToString(row.get("replaydate")));
            obj.setEndDate(fmt.ToString(row.get("logdate")));
            list.add(obj);
        }
        return new GridPage<DataLoadModel>(list, page, max, rowCount);
    }
    
    @Override
    public GridPage<DataQualityModel> listDqLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_load_name", "v_load_error_type",
                "v_load_error_details", "v_error_details_query"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "d_log_timestamp";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM LOG_DATA_EVC " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT d_log_timestamp, v_load_name, v_load_error_type, v_load_error_details, "
                + "substr(v_load_error_details,1,25) error, v_error_details_query, "
                + "substr(v_error_details_query,1,25) query "
                + "FROM LOG_DATA_EVC "
                + qObj.getCondition() + " "
                + "ORDER BY substr(" + sidx + ",1,400) " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<DataQualityModel> logs = new ArrayList<DataQualityModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx,
                    startIdx});
        for (Map row : rows) {
            DataQualityModel log = new DataQualityModel();
            log.setId(fmt.ToString(row.get("rnum")));
            log.setTimestamp(fmt.ToString(row.get("d_log_timestamp")));
            log.setLoadName(fmt.ToString(row.get("v_load_name")));
            log.setErrorType(fmt.ToString(row.get("v_load_error_type")));
            log.setErrorDetails(fmt.ToString(row.get("v_load_error_details")));
            log.setError(fmt.ToString(row.get("error")));
            log.setQueryDetails(fmt.ToString(row.get("v_error_details_query")));
            log.setQuery(fmt.ToString(row.get("query")));
            logs.add(log);
        }
        return new GridPage<DataQualityModel>(logs, page, max, rowCount);
    }
}