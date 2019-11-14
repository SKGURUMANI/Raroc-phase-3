/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.controller;

import atrix.common.service.CSRFTokenService;
import atrix.common.util.GridPage;
import atrix.st.model.RulesFrameworkModel;
import atrix.st.model.RulesOptionModel;
import atrix.st.service.RulesFrameworkService;
import java.util.List;
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
@RequestMapping(value = "/rules")
public class RulesFrameworkController {

    @Autowired
    private MessageSource msgSrc;
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private RulesFrameworkService rulesService;

    //Rule main page
    @RequestMapping(method = GET)
    public String mainPage() {
        return "rulesFramework/rulesMain";
    }

    //Create rule form page
    @RequestMapping(value = "/create", method = GET)
    public String rulesCreate(Map<String, Object> map) {        
        RulesFrameworkModel rm = new RulesFrameworkModel();
        rm.setRuleCat("VM");
        map.put("rulesModel", rm);
        map.put("resultTables", rulesService.listTables("result"));
        map.put("masterTables", rulesService.listTables("master"));
        map.put("rsltMastTables", rulesService.listTables("both"));
        return "rulesFramework/ruleCreate";
    }

    //Get list of existing rules to copy
    @RequestMapping(value = "/copy/listPage", method = GET)
    public String ruleCopyPage() {
        return "rulesFramework/ruleCreateList";
    }
    
    //Get table names for rule creation from model master
    @RequestMapping(value = "/selectTable/{tabType}", method = GET)
    public @ResponseBody
    List<RulesOptionModel> selectTable(@PathVariable("tabType") String tabType) {
        return rulesService.listTables(tabType);
    }

    //Get source columns from model master
    @RequestMapping(value = "/selectColumn/{tabName}", method = GET)
    public @ResponseBody
    List<RulesOptionModel> selectColumn(@PathVariable("tabName") String table) {
        return rulesService.listColumns(table);
    }

    //Get destination columns from model master
    @RequestMapping(value = "/selectColumn/destination/{tabName}", method = GET)
    public @ResponseBody
    List<RulesOptionModel> selectColumnDestination(@PathVariable("tabName") String table) {
        return rulesService.listPkColumns(table);
    }    

    //Get band condition according to the column type
    @RequestMapping(value = "/selectCondition/{tab}/{col}", method = GET)
    public @ResponseBody
    List<RulesOptionModel> selectConditions(@PathVariable("tab") String tab,
            @PathVariable("col") String col) {
        return rulesService.listCondition(tab, col);
    }

    //Check rule name availability and validate post data
    @RequestMapping(value = "/check/new", method = POST)
    public @ResponseBody
    String check(@ModelAttribute("rulesModel") RulesFrameworkModel rulesModel,
            BindingResult result) {
        String ret = rulesService.checkRuleName(rulesModel);
        if (ret.equals("success")) {
            return rulesService.validateRuleData(rulesModel);
        } else {
            return ret;
        }
    }

    //Check rule name availability and validate post data
    @RequestMapping(value = "/check/copy", method = POST)
    public @ResponseBody
    String checkRuleName(@ModelAttribute("rulesModel") RulesFrameworkModel rulesModel,
            BindingResult result) {
        return rulesService.checkRuleName(rulesModel);
    }

    //Create new rule
    @RequestMapping(value = "create/new", method = POST)
    public String addRule(@ModelAttribute("rulesModel") RulesFrameworkModel rulesModel,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        Integer val = rulesService.addRule(rulesModel, request);
        String js;
        if (val == 1) {
            if (rulesModel.getRuleCat().equals("BR")) {
                js = "businessRuleEdit";
            } else {
                js = "reclassRuleEdit";
            }
            map.put("ruleDetails", rulesService.getRule(rulesModel.getRuleName()));
            map.put("bcCondition", rulesService.BandCondition(rulesModel));
            map.put("js", js);
            return "rulesFramework/ruleMapping";
        } else {
            return "common/error";
        }
    }

    //Create new rule using existing rule
    @RequestMapping(value = "create/copy", method = POST)
    public String copyRule(@RequestParam("oldName") String oldName,
            @ModelAttribute("rulesModel") RulesFrameworkModel rulesModel,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        Integer val = rulesService.copyRule(rulesModel, oldName, request);
        String js;
        if (val == 1) {
            RulesFrameworkModel rm = rulesService.getRule(rulesModel.getRuleName());
            if (rm.getRuleCat().equals("BR")) {
                js = "businessRuleEdit";
            } else {
                js = "reclassRuleEdit";
            }
            map.put("ruleDetails", rulesService.getRule(rm.getRuleName()));
            map.put("bcCondition", rulesService.BandCondition(rm));
            map.put("js", js);
            return "rulesFramework/ruleMapping";
        } else {
            return "common/error";
        }
    }

    //Get list of rules to edit
    @RequestMapping(value = "/edit", method = GET)
    public String rulesEdit() {
        return "rulesFramework/ruleEditList";
    }
    
    //Validate rule data before modify
    @RequestMapping(value = "/check/modify", method = POST)
    public @ResponseBody
    String validateRuleModify(@ModelAttribute("rulesModel") RulesFrameworkModel rulesModel,
            BindingResult result) {
        return rulesService.validateRuleData(rulesModel);
    }
    
    //Modify rule form page
    @RequestMapping(value = "/modify", method = GET)
    public String rulesModify(@RequestParam(value = "ruleName") String ruleName,
        Map<String, Object> map) {
        RulesFrameworkModel rm = rulesService.getRule(ruleName);
        map.put("rulesModel", rm);
        map.put("resultTables", rulesService.listTables("result"));        
        map.put("rsltMastTables", rulesService.listTables("both"));
        if(rm.getRuleCat().equals("VM")) {
            map.put("destinationTabs", rulesService.listTables("master"));
        } else {
            map.put("destinationTabs", rulesService.listTables("result"));
        }
        map.put("column1", rulesService.listColumns(rm.getsTable1()));
        map.put("column2", rulesService.listColumns(rm.getsTable2()));
        map.put("column3", rulesService.listColumns(rm.getsTable3()));
        map.put("column4", rulesService.listColumns(rm.getsTable4()));
        map.put("column5", rulesService.listColumns(rm.getsTable5()));
        map.put("column6", rulesService.listColumns(rm.getdTable()));
        map.put("bc1", rulesService.listCondition(rm.getsTable1(), rm.getsColumn1()));
        map.put("bc2", rulesService.listCondition(rm.getsTable2(), rm.getsColumn2()));
        map.put("bc3", rulesService.listCondition(rm.getsTable3(), rm.getsColumn3()));
        map.put("bc4", rulesService.listCondition(rm.getsTable4(), rm.getsColumn4()));
        map.put("bc5", rulesService.listCondition(rm.getsTable5(), rm.getsColumn5()));
        return "rulesFramework/ruleModify";
    }

    //Modify rule post
    @RequestMapping(value = "modify", method = POST)
    public String modifyRule(@ModelAttribute("rulesModel") RulesFrameworkModel rulesModel,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        Integer val = rulesService.modifyRule(rulesModel, request);
        String js;
        if (val == 1) {
            if (rulesModel.getRuleCat().equals("BR")) {
                js = "businessRuleEdit";
            } else {
                js = "reclassRuleEdit";
            }
            map.put("ruleDetails", rulesService.getRule(rulesModel.getRuleName()));
            map.put("bcCondition", rulesService.BandCondition(rulesModel));
            map.put("js", js);
            return "rulesFramework/ruleMapping";
        } else {
            return "common/error";
        }
    }
    
    //Get list of rules to view
    @RequestMapping(value = "/view", method = GET)
    public String rulesView() {
        return "rulesFramework/ruleViewList";
    }

    //Rule grid
    @RequestMapping(value = "/ruleMaster/{type}", method = GET)
    public @ResponseBody
    GridPage<RulesFrameworkModel> rulesMasterView(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "v_rule_name") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @PathVariable("type") String type, HttpServletRequest request, HttpServletResponse response) {
        if(type.equals("edit")) {
            csrfTokenService.removeTokenFromSession(request);
            response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        }     
        final GridPage<RulesFrameworkModel> rulesList = rulesService.listRules(page, max, sidx, sord, searchField,
                searchOper, searchString, type);
        return rulesList;
    }

    //Delete rule
    @RequestMapping(value = "/ruleMaster/edit", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@RequestParam(value = "ruleName") String ruleName, HttpServletRequest request) {        
        rulesService.deleteRule(ruleName, request);
    }

    //Rule mapping page
    @RequestMapping(value = "/mapping/view", method = GET)
    public String rulesMappingView(@RequestParam("ruleName") String ruleName,
            Map<String, Object> map, HttpServletRequest request) {
        String js = "";
        RulesFrameworkModel rulesModel = rulesService.getRule(ruleName);
        map.put("ruleDetails", rulesModel);
        map.put("bcCondition", rulesService.BandCondition(rulesModel));
        if (rulesModel.getRuleCat().equals("BR")) {
            js = "businessRuleView";
        } else if (rulesModel.getRuleCat().equals("VM")) {
            js = "reclassRuleView";
        }
        map.put("js", js);
        return "rulesFramework/ruleMapping";
    }
    
    //Save or Commit Rule (Executes the function)
    @RequestMapping(value = "/commit", method = GET)
    public @ResponseBody
    String rulesMappingView(@RequestParam("ruleName") String ruleName, HttpServletRequest request) {
        return rulesService.commitRule(ruleName, request);
    }
}