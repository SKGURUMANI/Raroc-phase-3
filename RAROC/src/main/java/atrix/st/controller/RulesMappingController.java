package atrix.st.controller;

import atrix.common.model.JQueryDataTableParamModel;
import atrix.common.service.CSRFTokenService;
import atrix.common.util.DataTablesParamUtility;
import atrix.common.util.DataTablesResponse;
import atrix.common.util.GridPage;
import atrix.st.dao.RulesMappingDao;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesMappingModel;
import java.net.URI;
import static java.util.Collections.singletonList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;

@Controller
@RequestMapping(value = "/ruleMapping")
public class RulesMappingController {

    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private RulesMappingDao rulesMappingDao;

    //List mappings of the specified rule
    @RequestMapping(method = GET)
    public @ResponseBody
    GridPage<RulesMappingModel> listMappings(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false, defaultValue = "c_source1") String sidx,
            @RequestParam(value = "sord", required = false, defaultValue = "asc") String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam("ruleName") String ruleName,
            HttpServletRequest request, HttpServletResponse response) {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        final GridPage<RulesMappingModel> mappingList = rulesMappingDao.listMappings(page, max, sidx, sord, searchField,
                searchOper, searchString, ruleName);
        return mappingList;
    }

    //Mapping options from model master
    @RequestMapping(value = "/dropDown/{tab}/{col}", method = GET)
    public String dropDown(Map<String, Object> map,
            @PathVariable("tab") String tab,
            @PathVariable("col") String col) {
        if (!tab.equals("") && !col.equals("")) {
            map.put("list", rulesMappingDao.getOptions(tab, col));
        } else {
            map.put("list", null);
        }
        return "rulesFramework/gridOptions";
    }

    //Create a new mapping row for the specified rule
    @RequestMapping(method = POST)
    public ResponseEntity<String> createMapping(HttpServletRequest request,
            @RequestBody RulesMappingModel rulesModel,
            @RequestParam("ruleName") String ruleName) {
        if (rulesModel.getOper().equals("edit")) {
            rulesMappingDao.editMapping(ruleName, rulesModel);
        } else {
            rulesMappingDao.addMapping(ruleName, rulesModel);
        }

        URI uri = new UriTemplate("{requestUrl}/{username}").expand(request.getRequestURL().toString(), rulesModel.getId());
        final HttpHeaders headers = new HttpHeaders();
        headers.put("Location", singletonList(uri.toASCIIString()));
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    //Edit mapping row of the specified rule
    @RequestMapping(method = PUT)
    public ResponseEntity<String> editMapping(HttpServletRequest request,
            @RequestBody RulesMappingModel rulesModel,
            @RequestParam("ruleName") String ruleName) {
        rulesMappingDao.editMapping(ruleName, rulesModel);

        URI uri = new UriTemplate("{requestUrl}/{username}").expand(request.getRequestURL().toString(), rulesModel.getId());
        final HttpHeaders headers = new HttpHeaders();
        headers.put("Location", singletonList(uri.toASCIIString()));
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    //Delete a mapping row of the specified rule
    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMapping(@RequestParam("ruleName") String ruleName, @RequestParam("id") int id) {
        rulesMappingDao.deleteMapping(id, ruleName);
    }

    //Formula search highslide page
    @RequestMapping(value = "/fx/page/{ruleType}", method = GET)
    public String rulesFormulaSearch(Map<String, Object> map, HttpServletRequest request,
            @PathVariable("ruleType") String ruleType, @RequestParam("ruleName") String ruleName,
            @RequestParam("id") String id, @RequestParam("table") String table) {
        RulesFxGridModel fx = new RulesFxGridModel();
        fx.setId(id);
        fx.setFormulaName(ruleName);
        map.put("mappings", rulesMappingDao.getFxFromMapping(id, ruleName));
        map.put("expModel", fx);
        return "rulesFramework/ruleFxList";
    }

    //Datatables Formula GRID
    @RequestMapping(value = "/fx/grid", method = GET)
    public @ResponseBody
    DataTablesResponse FilterGrid(HttpServletRequest request) {
        JQueryDataTableParamModel param = DataTablesParamUtility.getParam(request);
        return rulesMappingDao.listFx(param.sEcho, param.iDisplayStart, param.iDisplayLength, param.iSortColumnIndex,
                param.sSortDirection, param.sSearch);
    }

    //Get Formula For Display
    @RequestMapping(value = "/fx/grid/{fxid}", method = GET)
    public @ResponseBody
    RulesFxGridModel getFx(@PathVariable("fxid") String fxid) {
        return rulesMappingDao.getFx(fxid);
    }

    //Create new formula
    @RequestMapping(value = "/fx/grid", method = POST)
    public @ResponseBody
    String postFx(@ModelAttribute("expModel") RulesFxGridModel expModel,
            HttpServletRequest request) {
        csrfTokenService.removeTokenFromSession(request);
        rulesMappingDao.updateFxInMapping(expModel);
        return "success";
    }

    //View formula
    @RequestMapping(value = "/fx/view/page", method = GET)
    public String rulesFormulaView(Map<String, Object> map,
            @RequestParam("ruleName") String ruleName, @RequestParam("id") String id) {
        RulesFxGridModel fx = new RulesFxGridModel();
        map.put("expModel", fx);
        map.put("mappings", rulesMappingDao.getFxFromMapping(id, ruleName));
        return "rulesFramework/ruleFxView";
    }
}