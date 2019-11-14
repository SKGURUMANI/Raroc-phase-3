/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.controller;

import atrix.common.service.CSRFTokenService;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithFooterPage;
import atrix.st.exception.CustomException;
import atrix.st.model.RarocAuthorize;
import atrix.st.model.RarocInputModel;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import atrix.st.service.RarocService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author vinoy
 */
@Controller
@RequestMapping(value = "/rarocAuth")
public class RarocAuthController {

    @Autowired
    private RarocService rarocService;
    @Autowired
    private CSRFTokenService csrfTokenService;
    private static final Logger logger = Logger.getLogger(RarocController.class);

    //Main RAROC Page
    @RequestMapping(method = RequestMethod.GET)
    public String createPage(Map<String, Object> model) {
        return "raroc/rarocAuthMain";
    }

    //RAROC grid for users
    @RequestMapping(value = "/master/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<RarocMasterModel> listRarocMaster(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            HttpServletRequest request, HttpServletResponse response)  throws CustomException {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        return rarocService.listRaroc(page, max, sidx, sord, searchField, searchOper, searchString, request);
    }

    //Edit RAROC Master
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editMaster(Map<String, Object> model, @RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        RarocMasterModel master = rarocService.adminEditRarocHeader(ref);
        if (master == null) {
            return "common/error";
        } else {
            model.put("formRarocMaster", master);
            model.put("bu", rarocService.listBu(request));
            model.put("rtool", rarocService.listModel());
            model.put("rcode", rarocService.listInternalRating(master.getRtool()));
            model.put("ind", rarocService.listIndustry());
            return "raroc/rarocAdminMasterEdit";
        }
    }

    //Edit RAROC Master submit
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editMaster(@ModelAttribute("formRarocMaster") RarocMasterModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.adminEditRarocMaster(model, request);
        HttpSession session = request.getSession(false);
        Integer unit = (Integer)session.getAttribute("unit");
        map.put("formRarocFacility", rarocService.getRarocInput(model.getRarocref(), unit));
        map.put("corpName", model.getCname());
        map.put("facCount", model.getFacility());
        map.put("facType", rarocService.listFacility());
        map.put("assetType", rarocService.listAsset(model.getRtool()));
        map.put("tRating", rarocService.listTemplateRating("Templated"));
        map.put("currency", rarocService.listCurrency());
        map.put("restStatus", rarocService.listRestructured());
        map.put("ltRating", rarocService.listLongExt());
        map.put("stRating", rarocService.listShortExt());
        map.put("guarType", rarocService.listGuarType());
        map.put("guarInt", rarocService.listInternalRating());
        map.put("guarExt", rarocService.listLongExt());
        return "raroc/rarocAdminFacilityEdit";
    }

    //Edit RAROC facility
    @RequestMapping(value = "/edit/facility", method = RequestMethod.POST)
    public String editFaciltities(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.editRarocDetails(model, request);
        map.put("form", rarocService.gridRarocHeader(model.getRefrec()));
        return "raroc/rarocViewAuth";
    }

    //View RAROC page
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewPage(Map<String, Object> model, @RequestParam(value = "ref") String ref,
            @RequestParam(value = "viewType", required = false) String viewType, HttpServletRequest request) {
        model.put("form", rarocService.gridRarocHeader(ref));
        if (viewType == null) {
            model.put("rarocAuthorize", new RarocAuthorize());
            return "raroc/rarocViewAuth";
        } else if (viewType.equals("XLS")) {
            HttpSession session = request.getSession(false);
            Integer unit = (Integer)session.getAttribute("unit");
            model.put("grid", rarocService.gridRarocView(ref, unit));
            return "XlsRaroc";
        } else {
            HttpSession session = request.getSession(false);
            Integer unit = (Integer)session.getAttribute("unit");
            model.put("grid", rarocService.gridRarocView(ref, unit));
            return "PdfRaroc";
        }
    }        
    
    //View Approver Comments
    @RequestMapping(value = "/view/comments", method = RequestMethod.GET)
    public @ResponseBody String viewApproverComments(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        return rarocService.getApproverComments(ref);
    }

    //RAROC View Tab1
    @RequestMapping(value = "/view/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridWithFooterPage<RarocViewModel> gridRarocView(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Integer unit = (Integer)session.getAttribute("unit");
        return rarocService.gridRarocView(ref, unit);
    }

    //RAROC View Tab2 page
    @RequestMapping(value = "/view/rwaTab", method = RequestMethod.GET)
    public String viewRwaTab() {
        return "raroc/rarocViewRwa";
    }

    //RAROC View Tab2 grid
    @RequestMapping(value = "/view/rwaTab/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridWithFooterPage<RarocViewModel> gridRwaTab(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        return rarocService.gridRwaCalc(ref);
    }

    //RAROC View Tab3 page
    @RequestMapping(value = "/view/rarocTab", method = RequestMethod.GET)
    public String viewRarocTab() {
        return "raroc/rarocViewRaroc";
    }

    //RAROC View Tab3 grid
    @RequestMapping(value = "/view/rarocTab/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridWithFooterPage<RarocViewModel> gridRarocTab(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        return rarocService.gridRarocCalc(ref);
    }

    //RAROC View Tab4 page
    @RequestMapping(value = "/view/sensRwTab", method = RequestMethod.GET)
    public String viewSensRwPage(Map<String, Object> model, @RequestParam(value = "ref") String ref) {
        model.put("list", rarocService.getFacilitylist(ref));
        return "raroc/rarocViewSensRw";
    }

    //RAROC View Tab5 page
    @RequestMapping(value = "/view/sensUtilTab", method = RequestMethod.GET)
    public String viewSensUtilPage(Map<String, Object> model, @RequestParam(value = "ref") String ref) {
        model.put("list", rarocService.getFacilitylist(ref));
        return "raroc/rarocViewSensUtil";
    }

    //RAROC View Charts
    @RequestMapping(value = "/view/chart/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<RarocViewModel> chartSensitivity(@PathVariable("type") String type,
            @RequestParam(value = "ref") String ref,
            @RequestParam(value = "fac") String fac) {
        if (type.equals("rw")) {
            return rarocService.listSensitivityRW(ref, fac);
        } else {
            return rarocService.listSensitivityUtil(ref, fac);
        }
    }
    
    //RAROC Status
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String rarocStatus(@RequestParam(value = "ref") String ref,
            @RequestParam(value = "action") String action,
            @ModelAttribute("rarocAuthorize") RarocAuthorize model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.adminRecordStatus(model, action, ref, request);
        return "raroc/rarocAuthMain";
    }

}