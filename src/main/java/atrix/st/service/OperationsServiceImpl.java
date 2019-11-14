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
package atrix.st.service;

import atrix.common.dao.SecurityDao;
import atrix.common.model.OptionsModel;
import atrix.common.model.TaskMonitorModel;
import atrix.common.util.GridPage;
import atrix.st.dao.OperationsDao;
import atrix.st.model.OperationsModel;
import atrix.st.model.TaskModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vaio
 */
@Service("opsService")
public class OperationsServiceImpl implements OperationsService {

    @Autowired
    private OperationsDao operationsDao;
    @Autowired
    private SecurityDao securityDao;

    @Override
    public String checkJobName(String jobName) {
        if (operationsDao.checkJobName(jobName) == 0) {
            return "available";
        } else {
            return "not_available";
        }
    }

    @Override
    public GridPage<OperationsModel> listJobs(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        return operationsDao.listJobs(page, max, sidx, sord, searchField, searchOper, searchString);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public OperationsModel insertJob(OperationsModel jobForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        jobForm.setJobId(operationsDao.getNewJobCode());        
        operationsDao.insertJob(jobForm.getJobId(), jobForm.getJobName(), audseq);
        securityDao.insertOperAudit(audseq, "Create Job", (String) session.getAttribute("userid"),
                "success", "New job '" + jobForm.getJobName() + "' created");
        return jobForm;
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public OperationsModel copyJob(OperationsModel jobForm, String jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        jobForm.setJobId(operationsDao.getNewJobCode());        
        operationsDao.copyJob(jobId, jobForm.getJobId(), jobForm.getJobName(), audseq);
        operationsDao.copyTasks(jobId, jobForm.getJobId(), audseq);
        securityDao.insertOperAudit(audseq, "Create Job", (String) session.getAttribute("userid"),
                "success", "New job '" + jobForm.getJobName() + "' created");
        return jobForm;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteJob(String jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        operationsDao.deleteAllTasks(jobId);
        operationsDao.deleteJob(jobId);        
        securityDao.insertOperAudit(audseq, "Delete Job", (String) session.getAttribute("userid"),
                "success", "Job '" + jobId + "' deleted");
    }
    
    @Override
    public GridPage<TaskModel> listTasks(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String jobId) {
        return operationsDao.listTasks(page, max, sidx, sord, searchField, searchOper, searchString, jobId);
    }
    
    @Override
    public List<OptionsModel> listTaskNames(String objType) {
        if(objType.equals("RULE")) {
            return operationsDao.listRules();
        } else {
            return operationsDao.listObjects(objType);
        }        
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void insertTask(String jobId, TaskModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        if(model.getTaskName().contains("~#~")) {
            String[] temp = model.getTaskName().split("~#~");
            model.setTaskId(temp[0]);
            model.setTaskName(temp[1]);
        }  
        operationsDao.moveTasksDown(jobId, model.getOrder());
        operationsDao.insertTask(jobId, model, audseq);
        securityDao.insertOperAudit(audseq, "Add Task", (String) session.getAttribute("userid"),
                "success", "New task '" + model.getTaskId() + "' added to '" + jobId + "'");
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateTask(String jobId, TaskModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        if(model.getTaskName().contains("~#~")) {
            String[] temp = model.getTaskName().split("~#~");
            model.setTaskId(temp[0]);
            model.setTaskName(temp[1]);
        }
        if(model.getPk() > model.getOrder()) {
            operationsDao.deleteTask(jobId, model.getPk());
            operationsDao.moveTasksDown(jobId, model.getOrder(), model.getPk()-1);
        } else if (model.getPk() < model.getOrder()) {
            operationsDao.deleteTask(jobId, model.getPk());
            operationsDao.moveTasksUp(jobId, model.getPk()+1, model.getOrder());
        }
        operationsDao.insertTask(jobId, model, audseq);
        securityDao.insertOperAudit(audseq, "Add Task", (String) session.getAttribute("userid"),
                "success", "New task '" + model.getTaskId() + "' added to '" + jobId + "'");
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteTask(String jobId, Integer pk, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        operationsDao.deleteTask(jobId, pk);
        operationsDao.moveTasksUp(jobId, pk);
        securityDao.insertOperAudit(audseq, "Delete Task", (String) session.getAttribute("userid"),
                "success", "Task '" + pk + "' deleted from job '" + jobId + "'");
    }
    
    @Override
    public GridPage<TaskMonitorModel> listExecutionLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        return operationsDao.listExecutionLog(page, max, sidx, sord, searchField, searchOper, searchString);
    }
    
    @Override
    public GridPage<TaskMonitorModel> listTaskLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String executionId) {
        return operationsDao.listTaskLog(page, max, sidx, sord, searchField, searchOper, searchString, executionId);
        
    }
    
    //Spring 3.1.1 does not allow @Async and @Transactional together
    //@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Async
    @Override
    public void callJobExecutor(String iDate, String jobCd, String jobName, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        String execSeq = operationsDao.getExecutionCode();
        operationsDao.insertMstExecution(iDate, execSeq, audseq, jobCd, jobName);
        operationsDao.callJobExecutorProc(jobCd, execSeq);        
        securityDao.insertOperAudit(audseq, "Batch fired", (String)session.getAttribute("userid"), 
                "success", "Batch fired with execution id "+execSeq);
    }
}