/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.controller;

import atrix.common.model.FileModel;
import atrix.common.model.OptionsModel;
import atrix.common.service.CSRFTokenService;
import atrix.common.util.GridPage;
import atrix.st.dao.DataLoadDao;
import atrix.st.model.DataLoadModel;
import atrix.st.model.DataQualityModel;
import atrix.st.service.JobExecutorService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vaio
 */
@Controller
@RequestMapping(value = "/operations")
public class DataLoadController {

    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private JobExecutorService jobExecutor;
    @Autowired
    private DataLoadDao dataLoadDao;
    private static final Logger logger = Logger.getLogger(DataLoadController.class);

    @RequestMapping(value = "upload", method = RequestMethod.GET)
    public String UploadFile(Model model) {
        model.addAttribute("fileModel", new FileModel());
        return "operations/dataLoad/fileUpload";
    }

    //Upload files
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public @ResponseBody
    String registerEventAttachment(@ModelAttribute("fileModel") FileModel fileModel,
            HttpServletRequest request) throws IOException {        
        if (fileModel.getFile() != null) {
            jobExecutor.saveFile(fileModel, request);
        } else {
            logger.info("No File to load");
        }
        return null;
    }

    @RequestMapping(value = "/upload/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<FileModel> fileListGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "n_serial_no") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            HttpServletRequest request, HttpServletResponse response) {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        final GridPage<FileModel> list = jobExecutor.listFiles(page, max, sidx, sord, searchString, "dump");
        return list;
    }

    @RequestMapping(value = "/upload/grid", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@RequestParam("name") String name, HttpServletRequest request) {
        jobExecutor.DeleteFile(name, request);
    }

    @RequestMapping(value = "etl/dataLoadView", method = RequestMethod.GET)
    public String dataLoadView() {
        return "operations/dataLoad/metaDataList";
    }

    @RequestMapping(value = "etl/dataLoadAdd", method = RequestMethod.GET)
    public String dataLoadAdd(HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        return "operations/dataLoad/metaDataAdd";
    }

    @RequestMapping(value = "etl/jobList/intial/{type}", method = RequestMethod.GET)
    public String jobListInitial(@PathVariable("type") String type, Map<String, Object> map) {
        map.put("list", jobExecutor.listJobs(type));
        return "common/options";
    }

    @RequestMapping(value = "etl/jobList/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> jobList(@PathVariable("type") String type) {
        return jobExecutor.listJobs(type);
    }

    @RequestMapping(value = "/etl/dataLoadView/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<DataLoadModel> dataLoadGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "n_serial_no") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<DataLoadModel> list = dataLoadDao.listTransformations(page, max, sidx, sord, searchField,
                searchOper, searchString);
        return list;
    }

    @RequestMapping(value = "/etl/dataLoadView/grid", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void defragGridPost(@RequestBody DataLoadModel model) {
        if (model.getOper().equals("add")) {
            dataLoadDao.addTrans(model);
        }
    }

    @RequestMapping(value = "/etl/dataLoadView/grid", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void defragGridDelete(@RequestParam("pk") String pk) {
        dataLoadDao.deleteTrans(pk);
    }

    @RequestMapping(value = "/etl/execute", method = RequestMethod.GET)
    public @ResponseBody
    String dataLoad(@RequestParam("name") String name,
            @RequestParam("type") String type, HttpServletRequest request) {
        return jobExecutor.callKettleTransformation(name, type, request);
    }

    @RequestMapping(value = "/etl/dataLoadStatus", method = RequestMethod.GET)
    public String LogTablePage() {
        return "operations/dataLoad/logTable";
    }

    @RequestMapping(value = "/etl/status/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<DataLoadModel> logTableListGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<DataLoadModel> list = dataLoadDao.listTransformationLog(page, max, sidx, sord, searchField,
                searchOper, searchString);
        return list;
    }

    @RequestMapping(value = "/etl/dataLoadLogs", method = RequestMethod.GET)
    public String LogFilePage() {
        return "operations/dataLoad/logFile";
    }
    
    @RequestMapping(value = "/etl/dataLoadDqLogs", method = RequestMethod.GET)
    public String LogDqPage() {
        return "operations/dataLoad/dqLog";
    }
    
    @RequestMapping(value = "/etl/dataLoadDqLogs/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<DataQualityModel> logDqGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "n_serial_no") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<DataQualityModel> list = dataLoadDao.listDqLog(page, max, sidx, sord, searchField, searchOper, searchString);
        return list;
    }

    @RequestMapping(value = "/etl/logs/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<FileModel> logFileListGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "n_serial_no") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<FileModel> list = jobExecutor.listFiles(page, max, sidx, sord, searchString, "log");
        return list;
    }        

    @RequestMapping(value = "/etl/logs/file/download", method = RequestMethod.GET)
    public void LogFilePage(@RequestParam(value = "fileName") String fileName, HttpServletResponse response) {
        jobExecutor.DownloadFile(fileName, response);
    }
}