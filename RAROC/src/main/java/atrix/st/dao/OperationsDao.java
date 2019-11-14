/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.model.OptionsModel;
import atrix.common.model.TaskMonitorModel;
import atrix.common.util.GridPage;
import atrix.st.model.OperationsModel;
import atrix.st.model.TaskModel;
import java.util.List;

/**
 *
 * @author vaio
 */
public interface OperationsDao {

    public Integer checkJobName(String jobName);

    public GridPage<OperationsModel> listJobs(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);

    public String getNewJobCode();

    public void insertJob(String jobId, String jobName, String auditCd);
    
    public void deleteAllTasks(String jobId);
    
    public void deleteJob(String jobId);
    
    public void copyJob(String oldJob, String newJob, String jobName, String auditCd);
    
    public void copyTasks(String oldJob, String newJob, String auditCd);

    public List<OptionsModel> listObjects(String objType);

    public List<OptionsModel> listRules();
    
    public GridPage<TaskModel> listTasks(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String jobId);
    
    public void insertTask(String jobId, TaskModel model, String auditCd);
    
    public void deleteTask(String jobId, Integer pk);
    
    public void moveTasksUp(String jobId, Integer order);
    
    public void moveTasksUp(String jobId, Integer order1, Integer order2);
    
    public void moveTasksDown(String jobId, Integer order);
    
    public void moveTasksDown(String jobId, Integer order1, Integer order2);
    
    public GridPage<TaskMonitorModel> listExecutionLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public GridPage<TaskMonitorModel> listTaskLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String executionId);
    
    public String getExecutionCode();
    
    public void insertMstExecution(String iDate, String execId, String auditCd, String jobId, String jobName);
    
    public void callJobExecutorProc(String jobCd, String executionId);
}