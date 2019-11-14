/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.controller;

import atrix.common.model.MessageModel;
import atrix.common.model.OptionsModel;
import atrix.common.model.TaskMonitorModel;
import atrix.common.service.CSRFTokenService;
import atrix.common.util.GridPage;
import atrix.st.model.OperationsModel;
import atrix.st.model.TaskModel;
import atrix.st.service.OperationsService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vaio
 */
@Controller
@RequestMapping(value = "/job")
public class OperationsController {

    @Autowired
    private OperationsService opsService;
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private MessageSource messageSource;
    private static final Logger logger = Logger.getLogger(OperationsController.class);

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createPage(Map<String, Object> model) {        
        model.put("jobForm", new OperationsModel());
        return "operations/createJob";
    }

    @RequestMapping(value = "/available", method = GET)
    public @ResponseBody
    MessageModel check(@RequestParam(value = "jobName") String jobName) {
        MessageModel m = new MessageModel();
        String ret = opsService.checkJobName(jobName);
        m.setMesgType(ret);
        if (ret.equals("not_available")) {
            m.setMesgValue(messageSource.getMessage("job.duplicate", null, null));
        }
        return m;
    }

    @RequestMapping(value = "/create/new", method = RequestMethod.POST)
    public String createNewPage(@ModelAttribute OperationsModel jobForm,
            Map<String, Object> map, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        map.put("jobForm", opsService.insertJob(jobForm, request));
        return "operations/taskList";
    }

    @RequestMapping(value = "/copy/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "operations/listPage";
    }

    @RequestMapping(value = "/create/copy", method = RequestMethod.POST)
    public String createCopyPage(@ModelAttribute OperationsModel jobForm,
            @RequestParam(value = "jobId") String jobId, Map<String, Object> map,
            HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        map.put("jobForm", opsService.copyJob(jobForm, jobId, request));
        return "operations/taskList";
    }

    @RequestMapping(value = "/list/batch", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<OperationsModel> listJobs(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            HttpServletRequest request, HttpServletResponse response) {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        final GridPage<OperationsModel> jobs = opsService.listJobs(page, max, sidx, sord, searchField, searchOper,
                searchString);
        return jobs;
    }

    @RequestMapping(value = "/list/batch/edit", method = RequestMethod.GET)
    public String editBatch(Map<String, Object> map, @RequestParam(value = "jobId") String jobId,
            @RequestParam(value = "jName") String jName) {        
        OperationsModel model = new OperationsModel();
        model.setJobId(jobId);
        model.setJobName(jName);
        map.put("jobForm", model);
        return "operations/taskList";
    }

    @RequestMapping(value = "/list/batch", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@RequestParam("jobId") String jobId, HttpServletRequest request) {
        opsService.deleteJob(jobId, request);
    }

    @RequestMapping(value = "/list/tasks", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<TaskModel> listTasks(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "jobId") String jobId,
            HttpServletRequest request, HttpServletResponse response) {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        final GridPage<TaskModel> tasks = opsService.listTasks(page, max, sidx, sord, searchField, searchOper,
                searchString, jobId);
        return tasks;
    }

    @RequestMapping(value = "/tasks/options/{objectType}", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> optionsTaskType(@PathVariable("objectType") String objectType) {
        return opsService.listTaskNames(objectType);
    }

    @RequestMapping(value = "/list/tasks", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addTask(@RequestParam("jobId") String jobId, @RequestBody TaskModel model,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if(model.getOper().equals("edit")) {
                opsService.updateTask(jobId, model, request);
            } else {
                opsService.insertTask(jobId, model, request);
            }
        } catch (DuplicateKeyException ex) {
            logger.error(ex);
            response.sendError(2001);
        } catch (DataAccessException ex) {
            logger.error(ex);
            response.sendError(2002);
        } catch (Exception ex) {
            logger.error(ex);
            response.sendError(2003);
        }                
    }
    
    @RequestMapping(value = "/list/tasks", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editTask(@RequestParam("jobId") String jobId, @RequestBody TaskModel model,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            opsService.updateTask(jobId, model, request);
        } catch (DuplicateKeyException ex) {
            logger.error(ex);
            response.sendError(2001);
        } catch (DataAccessException ex) {
            logger.error(ex);
            response.sendError(2002);
        } catch (Exception ex) {
            logger.error(ex);
            response.sendError(2003);
        }        
    }
    
    @RequestMapping(value = "/list/tasks", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@RequestParam("jobId") String jobId, @RequestParam("pk") Integer pk,
            HttpServletRequest request) {
        opsService.deleteTask(jobId, pk, request);
    }
    
    @RequestMapping(value = "/monitor", method = RequestMethod.GET)
    public String MonitorBatchPage() {
        return "operations/monitorBatchList";
    }
    
    @RequestMapping(value = "/monitor/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<TaskMonitorModel> listMstExecution(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<TaskMonitorModel> jobs = opsService.listExecutionLog(page, max, sidx, sord, searchField, searchOper,
                searchString);
        return jobs;
    }
    
    @RequestMapping(value = "/monitor/process", method = RequestMethod.GET)
    public String MonitorProcessPage(Map<String, Object> map,
            @RequestParam(value = "id") String id,
            @RequestParam(value = "name") String name) {
        map.put("id",id);
        map.put("jobName",name);
        return "operations/monitorTaskList";
    }
    
    @RequestMapping(value = "/monitor/process/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<TaskMonitorModel> listProcessLog(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "id", required = false) String id) {
        final GridPage<TaskMonitorModel> jobs = opsService.listTaskLog(page, max, sidx, sord, searchField, searchOper,
                searchString, id);
        return jobs;
    }
    
    @RequestMapping(value = "/execute", method = RequestMethod.GET)
    public String ExecuteJobPage(Map<String, Object> map) {
        return "operations/executeBatchList";
    }
    
    @RequestMapping(value = "/execute/fire", method = RequestMethod.GET)
    public @ResponseBody String executeJob(
            @RequestParam("jobId") String jobId, @RequestParam("jName") String jobName,
            @RequestParam("iDate") String iDate, HttpServletRequest request) {
        opsService.callJobExecutor(iDate, jobId, jobName, request);
        return messageSource.getMessage("job.fire.success", new Object[]{StringEscapeUtils.escapeXml(jobName)}, null, null);
    }
}