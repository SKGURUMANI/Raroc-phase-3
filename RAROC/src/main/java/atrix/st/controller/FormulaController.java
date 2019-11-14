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
package atrix.st.controller;

import atrix.common.service.CSRFTokenService;
import atrix.common.util.GridPage;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesFxTreeModel;
import atrix.st.model.TreeModel;
import atrix.st.service.FormulaService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vaio
 */
@Controller
@RequestMapping(value = "/rules/formula")
public class FormulaController {
    
    @Autowired
    private MessageSource msgSrc;
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private FormulaService fxService;

    //Formula landing page according to type
    @RequestMapping(value = "/{type}", method = GET)
    public String rulesFormula(@PathVariable("type") String type, Map<String, Object> map,
            HttpServletRequest request) {
        map.put("breadcrumbMessage", msgSrc.getMessage("breadcrumb.rfw.fx." + type, null, "Formula", null));
        map.put("type", type);
        if (type.equals("create")) {            
            return "rulesFramework/formulaNew";
        } else {
            return "rulesFramework/formulaList";
        }
    }

    //Formula list page for create
    @RequestMapping(value = "/create/list", method = GET)
    public String rulesFormulaCreateList(Map<String, Object> map) {
        map.put("breadcrumbMessage", msgSrc.getMessage("breadcrumb.rfw.fx.create", null, "Formula", null));
        map.put("type", "create");
        return "rulesFramework/formulaListNew";
    }

    //Formula grid
    @RequestMapping(value = "/grid/{type}", method = GET)
    public @ResponseBody
    GridPage<RulesFxGridModel> rulesFormulaGird(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "v_formula_name") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @PathVariable("type") String type, HttpServletRequest request, HttpServletResponse response) {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        final GridPage<RulesFxGridModel> rulesList = fxService.listFx(page, max, sidx, sord, searchField,
                searchOper, searchString, type);
        return rulesList;
    }

    //Delete Formula
    @RequestMapping(value = "/grid/edit", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFx(@RequestParam(value = "formulaId") String formulaId, HttpServletRequest request) {
        fxService.deleteFx(formulaId, request);
    }

    //Check availability of formula name
    @RequestMapping(value = "/check", method = GET)
    public @ResponseBody
    String rulesFormulaAvailability(@RequestParam("fxname") String fxname) {
        return fxService.checkFxName(fxname);
    }

    //Pass formula name to expression builder page
    @RequestMapping(value = "/create/build", method = GET)
    public String rulesFormulaCreateGet(@RequestParam("fxname") String fxname,
            Map<String, Object> map, HttpServletRequest request) {
        map.put("breadcrumbMessage", msgSrc.getMessage("breadcrumb.rfw.fx.create", null, "Formula", null));
        map.put("uri", "rules/formula/create");
        RulesFxGridModel expModel = new RulesFxGridModel();
        expModel.setFormulaName(fxname);
        map.put("expModel", expModel);
        return "rulesFramework/formulaCreate";
    }

    //Build the table tree from DB
    @RequestMapping(value = "/tree/{type}", method = GET)
    public @ResponseBody
    TreeModel<RulesFxTreeModel> rulesFormulaTree(@PathVariable("type") String type) {
        if (type.equals("tc")) {
            TreeModel<RulesFxTreeModel> tm = new TreeModel<RulesFxTreeModel>();
            tm.setNodes(fxService.listRulesTable());
            tm.setState("parent");
            return tm;
        } else if (type.equals("fx")) {
            TreeModel<RulesFxTreeModel> tm = new TreeModel<RulesFxTreeModel>();
            tm.setNodes(fxService.listFxType());
            tm.setState("parent");
            return tm;
        } else {
            TreeModel<RulesFxTreeModel> tm = new TreeModel<RulesFxTreeModel>();
            tm.setNodes(fxService.listOperatorType());
            tm.setState("parent");
            return tm;
        }
    }

    //Get column names of the clicked tree node
    @RequestMapping(value = "/tree/node/{type}", method = GET)
    public @ResponseBody
    TreeModel<RulesFxTreeModel> rulesFormulaTreeColumn(@PathVariable("type") String type,
            @RequestParam("node") String node) {
        if (type.equals("tc")) {
            TreeModel<RulesFxTreeModel> tc = new TreeModel<RulesFxTreeModel>();
            tc.setNodes(fxService.listRulesColumns(node));
            tc.setState("child");
            return tc;
        } else if (type.equals("fx")) {
            TreeModel<RulesFxTreeModel> tc = new TreeModel<RulesFxTreeModel>();
            tc.setNodes(fxService.listFxName(node));
            tc.setState("child");
            return tc;
        } else {
            TreeModel<RulesFxTreeModel> tc = new TreeModel<RulesFxTreeModel>();
            tc.setNodes(fxService.listOperatorName(node));
            tc.setState("child");
            return tc;
        }
    }

    //Warn before formula modification
    @RequestMapping(value = "/create/warning/{fxid}", method = GET)
    public @ResponseBody
    String rulesFormulaWarning(@PathVariable("fxid") String fxid, Map<String, Object> map) {
        return fxService.getFxWarning(fxid);
    }

    //Select existing formula to create formula
    @RequestMapping(value = "/create/duplicate/{fxid}", method = GET)
    public String rulesFormulaGetExisting(@PathVariable("fxid") String fxid, @RequestParam("fxname") String fxname,
            Map<String, Object> map, HttpServletRequest request) {        
        map.put("breadcrumbMessage", msgSrc.getMessage("breadcrumb.rfw.fx.create", null, "Formula", null));
        map.put("uri", "rules/formula/create");
        RulesFxGridModel expModel = fxService.getFormula(fxid);
        expModel.setFormulaName(fxname);
        map.put("expModel", expModel);
        return "rulesFramework/formulaCreate";
    }

    //Create a new formula
    @RequestMapping(value = "/create", method = POST)
    public @ResponseBody
    String rulesFormulaCreatePost(@ModelAttribute("expModel") RulesFxGridModel expModel,
            BindingResult result, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        fxService.insertFx(expModel, request);
        return "success";
    }

    //View formula in highslide
    @RequestMapping(value = "/view/{fxid}", method = GET)
    public String rulesFormulaView(@PathVariable("fxid") String fxid, Map<String, Object> map) {
        map.put("expModel", fxService.getFormula(fxid));
        return "rulesFramework/formulaView";
    }

    //Get formula to edit
    @RequestMapping(value = "/edit/{fxid}", method = GET)
    public String rulesFormulaEditGet(@PathVariable("fxid") String fxid,
            Map<String, Object> map, HttpServletRequest request) {
        map.put("breadcrumbMessage", msgSrc.getMessage("breadcrumb.rfw.fx.edit", null, "Formula", null));
        map.put("uri", "rules/formula/edit");
        map.put("expModel", fxService.getFormula(fxid));
        return "rulesFramework/formulaCreate";
    }

    //Edit formula
    @RequestMapping(value = "/edit", method = POST)
    public @ResponseBody
    String rulesFormulaEditPost(@ModelAttribute("expModel") RulesFxGridModel expModel,
            BindingResult result, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        fxService.updateFx(expModel, request);
        return "success";
    }
}