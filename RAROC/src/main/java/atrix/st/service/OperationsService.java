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

import atrix.common.model.OptionsModel;
import atrix.common.model.TaskMonitorModel;
import atrix.common.util.GridPage;
import atrix.st.model.OperationsModel;
import atrix.st.model.TaskModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface OperationsService {
    
    public String checkJobName(String jobName);
    
    public GridPage<OperationsModel> listJobs(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);       
    
    public OperationsModel insertJob(OperationsModel jobForm, HttpServletRequest request);
    
    public OperationsModel copyJob(OperationsModel jobForm, String jobId, HttpServletRequest request);
    
    public void deleteJob(String jobId, HttpServletRequest request);
    
    public GridPage<TaskModel> listTasks(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String jobId);
    
    public List<OptionsModel> listTaskNames(String objType);
    
    public void insertTask(String jobId, TaskModel model, HttpServletRequest request);
    
    public void updateTask(String jobId, TaskModel model, HttpServletRequest request);
    
    public void deleteTask(String jobId, Integer pk, HttpServletRequest request);
    
    public GridPage<TaskMonitorModel> listExecutionLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public GridPage<TaskMonitorModel> listTaskLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String executionId);
    
    public void callJobExecutor(String iDate, String jobCd, String jobName, HttpServletRequest request);
}