/* © 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 *
 * Asymmetrix Solutions Private Limited
 * 115, Bldg 2, Sector 3,
 * Millennium Business Park,
 * Navi Mumbai, India, 410701
 */
package atrix.common.controller;

import atrix.common.model.*;
import atrix.common.service.CSRFTokenService;
import atrix.common.service.UserService;
import atrix.common.util.GridPage;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling all administration activity requests
 *
 * @author Vinoy Nambiar
 */
@RequestMapping(value = "/admin")
@Controller
public class AdminController {

    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private UserService userService;
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private MessageSource messageSource;
    private static final Logger logger = Logger.getLogger(AdminController.class);
    @Value("${auth.type}")
    private String authType;
    @Value("${user.sessionTime}")
    private String sessionTime;
    @Value("${user.failedAttempt}")
    private String failedAttempt;
    @Value("${user.passExpiry}")
    private String passExpiry;
    @Value("${user.homePage}")
    private String homePage;

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(Map <String, Object> map) {
        map.put("menu", "admin");
        return "admin/adminMain";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createUserGet(Model model, Map<String, List> mapL, Map<String, String> mapS,
            HttpServletRequest request) {
        UserModel userModel = new UserModel();
        userModel.setActive(1);
        userModel.setEnabled(1);
        model.addAttribute("userModel", userModel);
        mapL.put("locale", userService.getLocales());
        mapS.put("authType", authType);
        mapS.put("sessionTime", sessionTime);
        mapS.put("failedAttempt", failedAttempt);
        mapS.put("homePage", homePage);
        mapS.put("passExpiry", passExpiry);
        return "admin/createUser";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody
    MessageModel createUserPost(@Valid UserModel userModel, BindingResult result,
            HttpServletRequest request) {
        MessageModel m = new MessageModel();
        if (result.hasErrors()) {
            String error = "";
            List<FieldError> fes = result.getFieldErrors();
            for (FieldError fe : fes) {
                System.out.println("I am here");
                error += fe.getDefaultMessage() + "<br />";
            }
            m.setMesgType("error");
            m.setMesgValue(error);
            return m;
        }
        String ret = userService.CreateUser(userModel, request);
        if (ret.equals("success")) {
            csrfTokenService.removeTokenFromSession(request);
            m.setMesgType(ret);
            m.setMesgValue(messageSource.getMessage("admin.user.create", new Object[]{userModel.getUserName()}, null, null));
            return m;
        } else if (ret.equals("duplicate")) {
            m.setMesgType(ret);
            m.setMesgValue(messageSource.getMessage("admin.user.duplicate", null, null));
            return m;
        } else {
            csrfTokenService.removeTokenFromSession(request);
            m.setMesgType("error");
            m.setMesgValue(messageSource.getMessage("internalError", null, null));
            return m;
        }
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public String createModifyGet(HttpServletRequest request) {
        return "admin/modifyUser";
    }

    @RequestMapping(value = "/listUser", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<UserModel> listUsers(
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "max", required = false) int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<UserModel> list = userService.listUsers(page, max, sidx, sord, searchField,
                searchOper, searchString);
        return list;
    }

    @RequestMapping(value = "/listUser/{id}", method = RequestMethod.GET)
    public String listUserId(@PathVariable("id") String id, Model model, Map<String, List> map,
            Map<String, String> mapS) {        
        model.addAttribute("userModel", userService.getUserDetails(id));
        map.put("locale", userService.getLocales());
        map.put("roleList", userService.getUserRoles(id));        
        mapS.put("authType", authType);
        return "admin/modifyUserForm";
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public @ResponseBody
    MessageModel createModifyPost(@Valid UserModel userModel, BindingResult result,
            HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        MessageModel m = new MessageModel();
        if (result.hasErrors()) {
            String error = "";
            List<FieldError> fes = result.getFieldErrors();
            for (FieldError fe : fes) {
                error += fe.getDefaultMessage() + "<br />";
            }
            m.setMesgType("error");
            m.setMesgValue(error);
            return m;
        }
        String ret = userService.ModifyUser(userModel, request);
        if (ret.equals("success")) {
            m.setMesgType(ret);
            m.setMesgValue(messageSource.getMessage("admin.user.modify", new Object[]{userModel.getUserName()}, null, null));
            return m;
        } else {
            m.setMesgType(ret);
            m.setMesgValue(messageSource.getMessage("internalError", null, null));
            return m;
        }
    }

    @RequestMapping(value = "/sack", method = RequestMethod.GET)
    public String sackUserGet(Model model) {
        model.addAttribute("userModel", new SecurityModel());
        return "admin/sackUser";
    }

    @RequestMapping(value = "/sack", method = RequestMethod.POST)
    public @ResponseBody
    MessageModel sackUser(@ModelAttribute SecurityModel userModel, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        MessageModel m = new MessageModel();
        String status = "admin.sack.noSession";
        String principal;
        for (Object user : sessionRegistry.getAllPrincipals()) {
            List<SessionInformation> userSessions = sessionRegistry.getAllSessions(user, false);
            for (SessionInformation userSession : userSessions) {
                UserDetails userDetails = (UserDetails) userSession.getPrincipal();
                principal = userDetails.getUsername();
                if (userModel.getUsername().equals(principal)) {
                    userSession.expireNow();
                    status = "admin.sack.session";
                }
            }
        }
        m.setMesgType("success");
        m.setMesgValue(messageSource.getMessage(status, new Object[]{StringEscapeUtils.escapeXml(userModel.getUsername())}, null, null));
        return m;
    }

    @RequestMapping(value = "/sysLog", method = RequestMethod.GET)
    public String sysLogPage(Map<String, Object> map,
            @RequestParam(value = "viewtype", required = false) String viewtype,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        if (viewtype == null) {
            return "admin/listSysLog";
        } else if (viewtype.equals("xls")) {
            map.put("list", userService.docSysLog(fromDate, toDate, searchField, searchOper, searchString));
            return "XlsSysLog";
        } else {
            map.put("list", userService.docSysLog(fromDate, toDate, searchField, searchOper, searchString));
            return "PdfSysLog";
        }
    }

    @RequestMapping(value = "/sysLog/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<UserModel> sysLogGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "n_serial_no") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        final GridPage<UserModel> list = userService.listSysLog(page, max, sidx, sord, searchField,
                searchOper, searchString, fromDate, toDate);
        return list;
    }

    @RequestMapping(value = "/opsLog", method = RequestMethod.GET)
    public String opsLogPage(Map<String, Object> map,
            @RequestParam(value = "viewtype", required = false) String viewtype,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        if (viewtype == null) {
            return "admin/listOpsLog";
        } else if (viewtype.equals("xls")) {
            map.put("list", userService.docOpsLog(fromDate, toDate, searchField, searchOper, searchString));
            return "XlsOpsLog";
        } else {
            map.put("list", userService.docOpsLog(fromDate, toDate, searchField, searchOper, searchString));
            return "PdfOpsLog";
        }
    }

    @RequestMapping(value = "/opsLog/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<TaskMonitorModel> opsLogGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "d_change_dt") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "desc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        final GridPage<TaskMonitorModel> list = userService.listOpsLog(page, max, sidx, sord, searchField,
                searchOper, searchString, fromDate, toDate);
        return list;
    }

    @RequestMapping(value = "/prsLog", method = RequestMethod.GET)
    public String prsLogPage() {
        return "admin/listPrsLog";
    }

    @RequestMapping(value = "/prsLog/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<TaskMonitorModel> prsLogGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "t_start") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "desc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString) {
        final GridPage<TaskMonitorModel> list = userService.listPrsLog(page, max, sidx, sord, searchField,
                searchOper, searchString);
        return list;
    }

    @RequestMapping(value = "/defrag", method = RequestMethod.GET)
    public String defragPage(HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        return "admin/listDefrag";
    }

    @RequestMapping(value = "/defrag/schemaList", method = RequestMethod.GET)
    public String schemaList(Map<String, Object> map) {
        map.put("list", userService.listSchemaNames());
        return "common/options";
    }

    @RequestMapping(value = "/defrag/tableList/{schema}", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> tableList(@PathVariable("schema") String schema,
            Map<String, Object> map) {
        return userService.listTableNames(schema);
    }

    @RequestMapping(value = "/defrag/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<DefragModel> defragGrid(
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
        final GridPage<DefragModel> list = userService.listDefragTables(page, max, sidx, sord, searchField,
                searchOper, searchString);
        return list;
    }

    @RequestMapping(value = "/defrag/grid", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void defragGridPost(@RequestBody DefragModel model) {
        if (model.getOper().equals("add")) {
            userService.postDefrag(model);
        }
    }

    @RequestMapping(value = "/defrag/grid/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void defragGridDelete(@PathVariable("id") String id) {
        userService.deleteDefrag(id);
    }

    @RequestMapping(value = "/defrag/execute", method = RequestMethod.GET)
    public @ResponseBody
    MessageModel callDefragProc() {
        MessageModel m = new MessageModel();
        userService.callDefragProc();
        m.setMesgValue(messageSource.getMessage("admin.defrag.fired", null, null));
        return m;
    }
}