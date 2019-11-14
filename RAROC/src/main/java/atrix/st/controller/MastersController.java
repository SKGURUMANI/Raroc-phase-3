/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.controller;

import atrix.common.service.CSRFTokenService;
import atrix.common.util.GridPage;
import atrix.st.dao.MastersDao;
import atrix.st.model.MastersModel;
import atrix.st.service.MastersService;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vaio
 */
@Controller
@RequestMapping(value = "/masters")
public class MastersController {
    
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private MastersDao mastersDao;
    @Autowired
    private MastersService masterService;
    private static final Logger logger = Logger.getLogger(MastersController.class);
    
    @RequestMapping(method=RequestMethod.GET)
    public String listMasters (Map<String, Object> map) {      
      map.put("breadcrumbMessage", "Operations >> Master Data");
      map.put("options", mastersDao.getMasterTables("edit"));
      map.put("viewType", "edit");  
      return "operations/masters/listMasters";
    }
    
    @RequestMapping(value = "/edit/{table}",method=RequestMethod.GET)
    public String gridPage (Map<String, Object> map, @PathVariable("table") String table) {      
      map.put("js", table);  
      return "operations/masters/gridMasters";
    }
    
    @RequestMapping(value = "/grid/table/{table}", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<MastersModel> masterGrid(
            @PathVariable("table") String table,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "1") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            HttpServletRequest request, HttpServletResponse response) {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        return masterService.masterGrid(page, max, sidx, sord, searchField, searchOper, searchString, table);
    }

    @RequestMapping(value = "/grid/table/{table}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putMaster(@PathVariable("table") String table,
            @RequestBody MastersModel model, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        try {
            masterService.putMaster(model, table, request);
        } catch (DuplicateKeyException ex) {
            System.out.println(ex);
            logger.error(ex);
            response.sendError(2001);
        } catch (DataAccessException ex) {
            System.out.println(ex);
            logger.error(ex);
            response.sendError(2002);
        } catch (Exception ex) {
            System.out.println(ex);
            logger.error(ex);
            response.sendError(2003);
        }
    }

    @RequestMapping(value = "/grid/table/{table}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void postMaster(@PathVariable("table") String table, @RequestBody MastersModel model,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (model.getOper().equals("edit")) {                
                masterService.putMaster(model, table, request);
            } else {
                masterService.postMaster(model, table, request);
            }
        } catch (DuplicateKeyException ex) {
            System.out.println(ex);
            logger.error(ex);
            response.sendError(2001);
        } catch (DataAccessException ex) {
            System.out.println(ex);
            logger.error(ex);
            response.sendError(2002);
        } catch (Exception ex) {
            System.out.println(ex);
            logger.error(ex);
            response.sendError(2003);
        }
    }

    @RequestMapping(value = "/grid/table/{table}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaster(@PathVariable("table") String table,
            @RequestParam("pk") String pk, HttpServletRequest request) {
        masterService.deleteMaster(pk, table, request);
    }
    
}