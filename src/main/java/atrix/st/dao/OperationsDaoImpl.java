/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.model.OptionsModel;
import atrix.common.model.QueryBuilderModel;
import atrix.common.model.TaskMonitorModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import atrix.st.model.OperationsModel;
import atrix.st.model.TaskModel;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("operationsDao")
public class OperationsDaoImpl extends JdbcDaoSupport implements OperationsDao {

    @Autowired
    OperationsDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    private @Value("${jdbc.username}")
    String schemaName;

    @Override
    public Integer checkJobName(String jobName) {
        String sql = "SELECT count(*) FROM CNFG_JOB_MASTER WHERE v_job_name = ?";
        return getJdbcTemplate().queryForObject(sql, new Object[]{jobName},Integer.class);
    }

    @Override
    public GridPage<OperationsModel> listJobs(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("c.v_job_id", "c.v_job_type", "c.v_job_name",
                "a.v_maker_cd", "a.d_change_dt"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "a.d_change_dt";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM CNFG_JOB_MASTER c LEFT OUTER JOIN "
                + "AUDIT_DETAILS a ON c.v_audit_cd = a.v_audit_cd " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT c.v_job_id, c.v_job_type, c.v_job_name, a.v_maker_cd, a.d_change_dt "
                + "FROM CNFG_JOB_MASTER c "
                + "LEFT OUTER JOIN AUDIT_DETAILS a ON c.v_audit_cd = a.v_audit_cd "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<OperationsModel> ops = new ArrayList<OperationsModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(),
                    endIdx, startIdx});
        for (Map row : rows) {
            OperationsModel obj = new OperationsModel();
            obj.setId(fmt.ToString(row.get("rnum")));
            obj.setJobId(fmt.ToString(row.get("v_job_id")));
            obj.setJobType(fmt.ToString(row.get("v_job_type")));
            obj.setJobName(fmt.ToString(row.get("v_job_name")));
            obj.setMaker(fmt.ToString(row.get("v_maker_cd")));
            obj.setDateCreated(fmt.ToString(row.get("d_change_dt")));
            ops.add(obj);
        }
        return new GridPage<OperationsModel>(ops, page, max, rowCount);
    }
    
    @Override
    public String getNewJobCode() {
        String query = "SELECT 'j'||job_cd.nextval FROM dual";
        return getJdbcTemplate().queryForObject(query, String.class);
    }
        
    @Override
    public void insertJob(String jobId, String jobName, String auditCd) {
        String query = "INSERT INTO CNFG_JOB_MASTER (v_job_id, v_job_type, v_job_name, v_audit_cd) "
                + "VALUES (?, ?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{jobId, "Stress Test", jobName, auditCd});
    }
    
    @Override
    public void deleteAllTasks(String jobId) {
        String query = "DELETE FROM CNFG_JOB_DETAILS WHERE v_job_id = ?";
        getJdbcTemplate().update(query, new Object[]{jobId});
    }
    
    @Override
    public void deleteJob(String jobId) {
        String query = "DELETE FROM CNFG_JOB_MASTER WHERE v_job_id = ?";
        getJdbcTemplate().update(query, new Object[]{jobId});
    }
    
    @Override
    public void copyJob(String oldJob, String newJob, String jobName, String auditCd) {
        String query = "INSERT INTO CNFG_JOB_MASTER (v_job_id, v_job_type, v_job_name, v_audit_cd) "
                + "SELECT '" + newJob + "', v_job_type, '" + jobName + "', '" + auditCd + "' "
                + "FROM CNFG_JOB_MASTER WHERE v_job_id = ?";
        getJdbcTemplate().update(query, new Object[]{oldJob});
    }

    @Override
    public void copyTasks(String oldJob, String newJob, String auditCd) {
        String query = "INSERT INTO CNFG_JOB_DETAILS (v_job_id, v_task_type, v_task_id, v_task_name, "
                + "n_order, v_audit_cd) "
                + "SELECT '" + newJob + "', v_task_type, v_task_id, v_task_name, n_order, '" + auditCd + "' "
                + "FROM CNFG_JOB_DETAILS WHERE v_job_id = ?";
        getJdbcTemplate().update(query, new Object[]{oldJob});
    }
    
    @Override
    public List<OptionsModel> listObjects(String objType) {        
        String query = "SELECT object_id, object_name FROM USER_OBJECTS WHERE object_type = ? "
                + "ORDER BY object_name";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{objType});                
        List<OptionsModel> lObj = new ArrayList<OptionsModel>();
        for (Map row : rows) {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("object_id")));
            obj.setValue(fmt.ToString(row.get("object_name")));
            lObj.add(obj);
        }
        return lObj;
    }
    
    @Override
    public List<OptionsModel> listRules() {        
        String query = "SELECT n_rule_id, v_rule_name FROM CNFG_RULE_DETAILS ORDER BY v_rule_name";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});                
        List<OptionsModel> lRules = new ArrayList<OptionsModel>();
        for (Map row : rows) {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("n_rule_id")));
            obj.setValue(fmt.ToString(row.get("v_rule_name")));
            lRules.add(obj);
        }
        return lRules;
    }
    
    @Override
    public GridPage<TaskModel> listTasks(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String jobId) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_task_type", "v_task_id", "v_task_name",
                "n_order"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "n_order";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM CNFG_JOB_DETAILS WHERE v_job_id = ? " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{jobId, qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_task_type, v_task_id, v_task_name, n_order "
                + "FROM CNFG_JOB_DETAILS "
                + "WHERE v_job_id = ? "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<TaskModel> ops = new ArrayList<TaskModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{jobId, qObj.getRegex(),
                    endIdx, startIdx});
        for (Map row : rows) {
            TaskModel obj = new TaskModel();
            obj.setId(fmt.ToString(row.get("rnum")));            
            obj.setTaskType(fmt.ToString(row.get("v_task_type")));
            obj.setTaskId(fmt.ToString(row.get("v_task_id")));
            obj.setTaskName(fmt.ToString(row.get("v_task_name")));
            obj.setOrder(fmt.ToInteger(row.get("n_order")));
            obj.setPk(fmt.ToInteger(row.get("n_order")));
            ops.add(obj);
        }
        return new GridPage<TaskModel>(ops, page, max, rowCount);
    }
    
    @Override
    public void insertTask(String jobId, TaskModel model, String auditCd) {
        String query = "INSERT INTO CNFG_JOB_DETAILS (v_job_id, v_task_type, v_task_id, v_task_name, "
                + "n_order, v_audit_cd) VALUES (?, ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{jobId, model.getTaskType(), model.getTaskId(), 
            model.getTaskName(), model.getOrder(), auditCd});
    }
    
    @Override
    public void deleteTask(String jobId, Integer pk) {
        String query = "DELETE FROM CNFG_JOB_DETAILS WHERE v_job_id = ? AND n_order = ?";
        getJdbcTemplate().update(query, new Object[]{jobId, pk});
    }
    
    @Override
    public void moveTasksUp(String jobId, Integer order) {
        String query = "UPDATE CNFG_JOB_DETAILS SET n_order = n_order - 1 "
                + "WHERE v_job_id = ? AND n_order > ?";
        getJdbcTemplate().update(query, new Object[]{jobId, order});
    }
    
    @Override
    public void moveTasksUp(String jobId, Integer order1, Integer order2) {
        String query = "UPDATE CNFG_JOB_DETAILS SET n_order = n_order - 1 "
                + "WHERE v_job_id = ? AND n_order BETWEEN ? AND ?";
        getJdbcTemplate().update(query, new Object[]{jobId, order1, order2});
    }
    
    @Override
    public void moveTasksDown(String jobId, Integer order) {
        String query = "UPDATE CNFG_JOB_DETAILS SET n_order = n_order + 1 "
                + "WHERE v_job_id = ? AND n_order >= ?";
        getJdbcTemplate().update(query, new Object[]{jobId, order});
    }
    
    @Override
    public void moveTasksDown(String jobId, Integer order1, Integer order2) {
        String query = "UPDATE CNFG_JOB_DETAILS SET n_order = n_order + 1 "
                + "WHERE v_job_id = ? AND n_order BETWEEN ? AND ?";
        getJdbcTemplate().update(query, new Object[]{jobId, order1, order2});
    }
    
    @Override
    public GridPage<TaskMonitorModel> listExecutionLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_job_id", "v_execution_id", 
                "v_execution_name", "d_information_date", "d_execution_date"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "d_execution_date";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM MST_EXECUTION "
                + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_job_id, v_execution_id, v_execution_name, d_information_date, d_execution_date "
                + "FROM MST_EXECUTION "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + " nulls last) a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<TaskMonitorModel> ids = new ArrayList<TaskMonitorModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(),
                    endIdx, startIdx});
        for (Map row : rows) {
            TaskMonitorModel id = new TaskMonitorModel();
            id.setId(fmt.ToString(row.get("rnum")));
            id.setBatchId(fmt.ToString(row.get("v_job_id")));            
            id.setUserid(fmt.ToString(row.get("v_execution_id")));
            id.setUserName(fmt.ToString(row.get("v_execution_name")));
            id.setStime(fmt.ToString(row.get("d_information_date")));
            id.setEtime(fmt.ToString(row.get("d_execution_date")));            
            ids.add(id);
        }
        return new GridPage<TaskMonitorModel>(ids, page, max, rowCount);
    }
    
    @Override
    public GridPage<TaskMonitorModel> listTaskLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String executionId) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_task_name", "v_status", "t_start", 
                "t_end", "v_remarks", "v_error_details"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "t_start";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM LOG_PROCESS_FLOW "
                + "WHERE v_task_name NOT IN ('PROC_JOB_EXECUTOR','PROC_DEFRAG') "
                + "AND v_execution_id = ? "
                + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{executionId, qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_execution_id, v_task_name, v_status, t_start, t_end, v_remarks, v_error_details "
                + "FROM LOG_PROCESS_FLOW "
                + "WHERE v_task_name NOT IN ('PROC_JOB_EXECUTOR','PROC_DEFRAG') "
                + "AND v_execution_id = ? "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + " nulls last) a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<TaskMonitorModel> ids = new ArrayList<TaskMonitorModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{executionId, qObj.getRegex(),
                    endIdx, startIdx});
        for (Map row : rows) {
            TaskMonitorModel id = new TaskMonitorModel();
            id.setId(fmt.ToString(row.get("rnum")));
            id.setTask(fmt.ToString(row.get("v_task_name")));
            id.setStime(fmt.ToString(row.get("t_start")));
            id.setEtime(fmt.ToString(row.get("t_end")));
            id.setStatus(fmt.ToString(row.get("v_status")));
            id.setRemarks(fmt.ToString(row.get("v_remarks")));
            id.setError(fmt.ToString(row.get("v_error_details")));
            ids.add(id);
        }
        return new GridPage<TaskMonitorModel>(ids, page, max, rowCount);
    }
    
    @Override
    public String getExecutionCode() {
        String query = "SELECT execution_id.nextval FROM dual";
        return getJdbcTemplate().queryForObject(query, String.class);
    }
    
    @Override
    public void insertMstExecution(String iDate, String execId, String auditCd, String jobId, String jobName) {
        String query = "INSERT INTO MST_EXECUTION (v_execution_id, d_information_date, d_execution_date, v_execution_type, "
                + "v_execution_name, v_audit_cd, v_job_id) "
                + "VALUES (?, to_date(?,'dd-Mon-yyyy'), sysdate, ?, ?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{execId, iDate, "Stress Test", jobName, 
            auditCd, jobId});
    }
    
    @Override
    public void callJobExecutorProc(String jobCd, String executionId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName).withProcedureName("PROC_JOB_EXECUTOR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("jobCd", java.sql.Types.VARCHAR),
                new SqlParameter("executionId", java.sql.Types.VARCHAR));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jobCd", jobCd);
        map.put("executionId", executionId);
        jdbcCall.execute(map);
    }
}