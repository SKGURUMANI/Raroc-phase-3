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

import atrix.common.model.MessageModel;
import atrix.common.model.OptionsModel;
import atrix.common.service.CSRFTokenService;
import atrix.common.service.DecryptPasswordBeforeAuthentication;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithFooterPage;
import atrix.st.exception.CustomException;
import atrix.st.model.RarocGridModel;
import atrix.st.model.RarocInputModel;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import atrix.st.service.RarocService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vaio
 */
@Controller
@RequestMapping(value = "/raroc")
public class RarocController {

    @Autowired
    private RarocService rarocService;
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private MessageSource msgSrc;
    private static final Logger logger = Logger.getLogger(RarocController.class);

    //Main RAROC Page
    @RequestMapping(method = RequestMethod.GET)
    public String createPage(Map<String, Object> model) {
        return "raroc/rarocMain";
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
            HttpServletRequest request, HttpServletResponse response) throws CustomException {
        csrfTokenService.removeTokenFromSession(request);
        response.setHeader("_tk", csrfTokenService.getTokenFromSession(request));
        GridPage<RarocMasterModel> gridList = rarocService.listRarocMaster(page, max, sidx, sord, searchField, searchOper, searchString, request);
        return gridList;
    }

    //Delete incomplete entries
//    @RequestMapping(value = "/master/grid/del", method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteRaroc(@RequestParam(value = "recref") String recref,
//            HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            rarocService.delRaroc(recref, request);
//        } catch (EmptyResultDataAccessException ex) {
//            logger.info(ex);
//            response.sendError(903, msgSrc.getMessage("delete.prohibited", null, "Prohibited", null));
//        } catch (Exception ex) {
//            logger.error(ex);
//            response.sendError(900);
//        }
//    }
    //View RAROC page
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewPage(Map<String, Object> model, @RequestParam(value = "ref") String ref,
            @RequestParam(value = "viewType", required = false) String viewType, HttpServletRequest request) {
        model.put("form", rarocService.gridRarocHeader(ref));
        if (viewType == null) {
            return "raroc/rarocView";
        } else if (viewType.equals("XLS")) {
            HttpSession session = request.getSession(false);
            Integer unit = (Integer) session.getAttribute("unit");
            //model.put("grid", rarocService.gridRarocView(ref, unit));
            model.put("grid", rarocService.gridRarocNewView(ref, unit));
            return "XlsRaroc";
        } else {
            HttpSession session = request.getSession(false);
            Integer unit = (Integer) session.getAttribute("unit");
            //model.put("grid", rarocService.gridRarocView(ref, unit));
            model.put("grid", rarocService.gridRarocNewView(ref, unit));
            return "PdfRaroc";
        }
    }

    //View RAROC page
    @RequestMapping(value = "/view/next", method = RequestMethod.GET)
    public String viewPageNext(Map<String, Object> model, @RequestParam(value = "ref") String ref,
            @RequestParam(value = "viewType", required = false) String viewType, HttpServletRequest request) {
        model.put("form", rarocService.gridRarocHeader(ref));
        if (viewType == null) {
            return "raroc/rarocView";
        } else if (viewType.equals("XLS")) {
            HttpSession session = request.getSession(false);
            Integer unit = (Integer) session.getAttribute("unit");
            //model.put("grid", rarocService.gridRarocView(ref, unit));
            model.put("grid", rarocService.gridRarocNewViewNext(ref, unit));
            return "XlsRaroc";
        } else {
            HttpSession session = request.getSession(false);
            Integer unit = (Integer) session.getAttribute("unit");
            //model.put("grid", rarocService.gridRarocView(ref, unit));
            model.put("grid", rarocService.gridRarocNewViewNext(ref, unit));
            return "PdfRaroc";
        }
    }

    //View Approver Comments
    @RequestMapping(value = "/view/comments", method = RequestMethod.GET)
    public @ResponseBody
    String viewApproverComments(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        return rarocService.getApproverComments(ref);
    }

    //RAROC View Tab1
    @RequestMapping(value = "/view/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridWithFooterPage<RarocViewModel> gridRarocView(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Integer unit = (Integer) session.getAttribute("unit");
        return rarocService.gridRarocNewView(ref, unit);
    }

    //RAROC View Tab1
    @RequestMapping(value = "/view/next/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridWithFooterPage<RarocViewModel> gridRarocViewNext(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Integer unit = (Integer) session.getAttribute("unit");
        return rarocService.gridRarocNewViewNext(ref, unit);
    }

    //RAROC View Tab2 page
    @RequestMapping(value = "/view/sensRwTab", method = RequestMethod.GET)
    public String viewSensRwPage(Map<String, Object> model, @RequestParam(value = "ref") String ref) {
        model.put("list", rarocService.getFacilitylist(ref));
        return "raroc/rarocViewSensRw";
    }

    //RAROC View Tab3 page
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

    //New RAROC master record
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String createNew(Map<String, Object> model, HttpServletRequest request) {
        model.put("formRarocMaster", new RarocMasterModel());
        model.put("rid", rarocService.listRatingIds());
        model.put("bu", rarocService.listBu(request));
        model.put("rtool", rarocService.listModel());
        model.put("ind", rarocService.listIndustry());
        return "raroc/rarocMaster";
    }

    //Options for internal rating model
    @RequestMapping(value = "/intrating/options", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> intRatingList(@RequestParam(value = "model") String model) {
        return rarocService.listInternalRating(model);
    }

    //Options for external long term rating model
    @RequestMapping(value = "/extrating/lt", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> extLtRatingList(@RequestParam(value = "model") String model) {
        return rarocService.listLongExt(model);
    }

    //Options for external long term rating model
    @RequestMapping(value = "/bond/rating", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> bondsRatingList() {
        return rarocService.listBondExtRat();
    }

    //Options for external long term rating model
    @RequestMapping(value = "/bond/cet", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> bondCETList() {
        return rarocService.listBondCet();
    }

    //Options for internal rating of guarantor
    @RequestMapping(value = "/intrating/guar", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> guarIntRating(@RequestParam(value = "model") String model) {
        return rarocService.listGuarIntRating(model);
    }

    //Customer name auto suggestion
    @RequestMapping(value = "/cname/options", method = RequestMethod.GET)
    public @ResponseBody
    List<OptionsModel> namesList(@RequestParam(value = "query") String term) {
        return rarocService.listNames(term);
    }

    //Check Availability
    @RequestMapping(value = "/availability", method = RequestMethod.GET)
    public @ResponseBody
    MessageModel availability(@RequestParam(value = "rid") String rid,
            HttpServletRequest request) {
        return rarocService.checkAvailability(rid, request);
    }

    protected String obtainPan(HttpServletRequest request, String data) {
        try {
            String encrypted = data;
            HttpSession session = request.getSession(false);
            String password = session.getId();
            String salt = request.getParameter("salt");
            String iv = request.getParameter("iv");
            byte[] saltBytes = hexStringToByteArray(salt);
            byte[] ivBytes = hexStringToByteArray(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec sKey = (SecretKeySpec) generateKeyFromPassword(password, saltBytes);
            try {
                return decrypt(encrypted, sKey, ivParameterSpec);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (GeneralSecurityException ex) {
            java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static SecretKey generateKeyFromPassword(String password, byte[] saltBytes)
            throws GeneralSecurityException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), saltBytes, 100, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String decrypt(String encryptedData, SecretKeySpec sKey,
            IvParameterSpec ivParameterSpec) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, sKey, ivParameterSpec);
        byte[] decordedValue;
        decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    //New RAROC entry submit
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String createNewPost(@ModelAttribute("formRarocMaster") RarocMasterModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, CustomException {

        Pattern pattern;
        Matcher matcher;
        String regEx = "^[0-9a-zA-Z _.,&()-]+$";
        pattern = Pattern.compile(regEx);
        matcher = pattern.matcher(model.getCname());
        if (!matcher.find()) {
            throw new CustomException("502", "Path");
        }
        System.out.println("PAN Card = " + model.getPanEnc());
        System.out.println("Value = " + obtainPan(request, model.getPanEnc()));
        model.setPan(obtainPan(request, model.getPanEnc()));
        String existence = rarocService.checkRarocExistence(model.getRid(), request);
        if (existence.equals("empty")) {
            String refrec = rarocService.addRarocMaster(model, request);
            RarocInputModel rf = new RarocInputModel();
            rf.setRefrec(refrec);
            HttpSession session = request.getSession(false);
            rf.setUnit((Integer) session.getAttribute("unit"));
            map.put("formRarocFacility", rf);
            map.put("corpName", model.getCname());
            map.put("facCount", model.getFacility());
            map.put("expBanks", model.getExpBanks());
            map.put("maturity", rarocService.listBenchmark());
            map.put("facType", rarocService.listFacility());
            map.put("facType_nfb", rarocService.listFacility_nfb());
            map.put("reFreq", rarocService.listReFreq());
            map.put("facType_bonds", rarocService.listFacility_bonds());
            map.put("assetType", rarocService.listAsset(model.getRtool()));
            map.put("tRating", rarocService.listTemplateRating(model.getRtool()));
            map.put("currency", rarocService.listCurrency());
            map.put("restStatus", rarocService.listRestructured());
            map.put("finSecurity", rarocService.listFinHaircut());
            map.put("bondRat", rarocService.listBondExtRat());
            map.put("psl", rarocService.listPSL());
            if (model.getRtool().equals("FBER")) {
                map.put("ltRating", rarocService.listMappedExternalRating("FBER", model.getIntRat()));
            } else {
                map.put("ltRating", rarocService.listLongExt());
            }
            if (model.getRtool().equals("DSB") || model.getRtool().equals("FBER") || model.getRtool().equals("FBIR")) {
                map.put("stVisibility", "N");
            } else {
                map.put("stVisibility", "Y");
                map.put("stRating", rarocService.listShortExt());
            }
            map.put("guarType", rarocService.listGuarType());
            map.put("guarInt", rarocService.listInternalRating());
            map.put("guarExt", rarocService.listLongExt());
            map.put("benchmark", rarocService.listBenchmark());

            map.put("curveDate", rarocService.getCurveDate());
            map.put("bbcdab", model.getBbcdab());
            map.put("tdfee", model.getTdfee());
            map.put("forex", model.getForex());
            map.put("cafee", model.getCafee());
            map.put("cms", model.getCms());
            map.put("other", model.getOther());

//            if (model.getBussunit().equals("FIG")) {
//                map.put("utilization", "100");
//                map.put("guarantee", "0");
//            }
            if (request.isUserInRole("ROLE_TREASURY")) {
                return "raroc/rarocFacilityTrsry";
            } else {
                return "raroc/rarocFacilityNew";
            }
        } else {
            String refrec = rarocService.existingRarocMaster(existence, model, request);
            map.put("oldref", existence);
            map.put("formRarocMaster", rarocService.gridRarocHeader(refrec));
            return "raroc/rarocExistingGrid";
        }
    }

    //New RAROC facility add to grid for FB
    @RequestMapping(value = "/new/facility", method = RequestMethod.POST)
    public @ResponseBody
    String createFaciltities_FB(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.addRarocDetails(model, request);
        map.put("form", rarocService.gridRarocHeader(model.getRefrec()));
        //return "raroc/rarocSubmit";
        return "success";
    }

    //Edit RAROC facility to grid for FB
    @RequestMapping(value = "/update/facility", method = RequestMethod.POST)
    public @ResponseBody
    String updateFaciltities(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        //rarocService.editRarocDetails(model, request);
        rarocService.updateRarocDetails_fb(model, request);
        //map.put("form", rarocService.gridRarocHeader(model.getRefrec()));
        return "success";
    }

    //New RAROC facility add to grid for NFB
    @RequestMapping(value = "/new/facility_nfb", method = RequestMethod.POST)
    public @ResponseBody
    String createFaciltities_NFB(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.addRarocDetails_nfb(model, request);
        return "success";
    }

    //New RAROC facility add to grid for BONDS
    @RequestMapping(value = "/new/facility_bonds", method = RequestMethod.POST)
    public @ResponseBody
    String createFaciltities_BONDS(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.addRarocDetails_bonds(model, request);
        return "success";
    }

    //Edit RAROC facility to grid for BONDS
    @RequestMapping(value = "/update/facility_bonds", method = RequestMethod.POST)
    public @ResponseBody
    String updateFaciltities_Bonds(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.updateRarocDetails_bonds(model, request);
        return "success";
    }

    //Edit RAROC facility to grid for NFB
    @RequestMapping(value = "/update/facility_nfb", method = RequestMethod.POST)
    public @ResponseBody
    String updateFaciltities_NFBs(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.updateRarocDetails_nfb(model, request);
        return "success";
    }

    //New RAROC facility submit for final calculation
    @RequestMapping(value = "/final/submit", method = RequestMethod.GET)
    public String submitFaciltities(@RequestParam("refId") String id, @RequestParam("facTy") String facType, Map<String, Object> map, HttpServletRequest request) {
        rarocService.submitFacility(id, facType, request);
        //rarocService.submitFacilityNext(id, facType, request);
        map.put("form", rarocService.gridRarocHeader(id));
        return "raroc/rarocSubmit";
    }

    //Grid for existing RAROC   
    @RequestMapping(value = "/existing/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<RarocGridModel> listExistingGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "recref") String recref,
            HttpServletRequest request, HttpServletResponse response) throws CustomException {
        return rarocService.listRarocInput(page, max, sidx, sord, searchField, searchOper,
                searchString, recref);
    }

    //Existing RAROC grid submit
    @RequestMapping(value = "/existing", method = RequestMethod.POST)
    public String createExistingPost(@ModelAttribute("formRarocMaster") RarocMasterModel model,
            BindingResult result, Map<String, Object> map,
            HttpServletRequest request,
            @RequestParam(value = "facs") String facs,
            @RequestParam(value = "oldref") String oldref) {
        HttpSession session = request.getSession(false);
        Integer unit = (Integer) session.getAttribute("unit");
        RarocInputModel im = rarocService.getExistingInput(oldref, facs, unit);
        im.setRefrec(model.getRarocref());
        im.setUnit(unit);
        map.put("formRarocFacility", im);
        map.put("corpName", model.getCname());
        map.put("facCount", model.getFacility());
        map.put("facType", rarocService.listFacility());
        map.put("assetType", rarocService.listAsset(model.getRtool()));
        map.put("tRating", rarocService.listTemplateRating(model.getRtool()));
        map.put("currency", rarocService.listCurrency());
        map.put("restStatus", rarocService.listRestructured());
        map.put("finSecurity", rarocService.listFinHaircut());
        map.put("bondRat", rarocService.listBondExtRat());
        if (model.getRtool().equals("FBER")) {
            map.put("ltRating", rarocService.listMappedExternalRating("FBER", model.getIntRat()));
        } else {
            map.put("ltRating", rarocService.listLongExt());
        }
        if (model.getRtool().equals("DSB") || model.getRtool().equals("FBER") || model.getRtool().equals("FBIR")) {
            map.put("stVisibility", "N");
        } else {
            map.put("stVisibility", "Y");
            map.put("stRating", rarocService.listShortExt());
        }
        map.put("guarType", rarocService.listGuarType());
        map.put("guarInt", rarocService.listInternalRating());
        map.put("guarExt", rarocService.listLongExt());
        map.put("benchmark", rarocService.listBenchmark());
        map.put("curveDate", rarocService.getCurveDate());
        if (request.isUserInRole("ROLE_TREASURY")) {
            return "raroc/rarocFacilityTrsry";
        } else {
            return "raroc/rarocFacility";
        }
    }

    //Edit RAROC Master
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editMaster(Map<String, Object> model, @RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        RarocMasterModel master = rarocService.editRarocHeader(ref);
        if (master != null) {
            model.put("formRarocMaster", master);
            model.put("ratingIds", master.getRid());
            model.put("radio", master.getCustType());
            System.out.println("getCustType  " + master.getCustType());
            model.put("rid", rarocService.listRatingIds());
            model.put("bu", rarocService.listBu(request));
            model.put("rtool", rarocService.listModel());
            model.put("rcode", rarocService.listInternalRating(master.getRtool()));
            model.put("ind", rarocService.listIndustry());
            if (master.getCustType().equals("NTB")) {
                return "raroc/rarocMasterNtbEdit";
            } else {
                return "raroc/rarocMasterEdit";
            }

        } else {
            return "common/error";
        }
    }

    //Edit RAROC Master submit
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editMaster(@ModelAttribute("formRarocMaster") RarocMasterModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) throws CustomException {
        Pattern pattern;
        Matcher matcher;
        String regEx = "^[0-9a-zA-Z _.,&()-]+$";
        pattern = Pattern.compile(regEx);
        matcher = pattern.matcher(model.getCname());
        if (!matcher.find()) {
            throw new CustomException("502", "Path");
        }

        HttpSession session = request.getSession(false);
        System.out.println("PAN Card = " + model.getPanEnc());
        System.out.println("Value = " + obtainPan(request, model.getPanEnc()));
        model.setPan(obtainPan(request, model.getPanEnc()));
        Integer unit = 100000;
        model.setUnit(unit);
        rarocService.editRarocMaster(model, request);
        String custName = rarocService.getCname(model.getRarocref());
        RarocInputModel im = rarocService.getRarocInput(model.getRarocref(), unit);
        im.setUnit(unit);
        im.setRefrec(model.getRarocref());
        if (!model.getCname().equals(custName)) {
            throw new CustomException("502", "Path");
        }
        map.put("formRarocFacility", im);
        map.put("refrec", model.getRarocref());
        map.put("expBanks", model.getExpBanks());
        map.put("corpName", custName);
        map.put("facCount", model.getFacility());
        map.put("facType", rarocService.listFacility());
        map.put("maturity", rarocService.listBenchmark());
        map.put("facType_nfb", rarocService.listFacility_nfb());
        map.put("facType_bonds", rarocService.listFacility_bonds());
        map.put("reFreq", rarocService.listReFreq());
        map.put("assetType", rarocService.listAsset(model.getRtool()));
        map.put("tRating", rarocService.listTemplateRating(model.getRtool()));
        map.put("currency", rarocService.listCurrency());
        map.put("restStatus", rarocService.listRestructured());
        map.put("finSecurity", rarocService.listFinHaircut());
        map.put("bondRat", rarocService.listBondExtRat());
        map.put("psl", rarocService.listPSL());
        if (model.getRtool().equals("FBER")) {
            map.put("ltRating", rarocService.listMappedExternalRating("FBER", model.getIntRat()));
        } else {
            map.put("ltRating", rarocService.listLongExt());
        }
        if (model.getRtool().equals("DSB") || model.getRtool().equals("FBER") || model.getRtool().equals("FBIR")) {
            map.put("stVisibility", "N");
        } else {
            map.put("stVisibility", "Y");
            map.put("stRating", rarocService.listShortExt());
        }
        map.put("guarType", rarocService.listGuarType());
        map.put("guarInt", rarocService.listInternalRating());
        map.put("guarExt", rarocService.listLongExt());
        map.put("benchmark", rarocService.listBenchmark());
        map.put("curveDate", rarocService.getCurveDate());
        map.put("bbcdab", model.getBbcdab());
        map.put("tdfee", model.getTdfee());
        map.put("forex", model.getForex());
        map.put("cafee", model.getCafee());
        map.put("cms", model.getCms());
        map.put("other", model.getOther());
        map.put("cfFlag", rarocService.getCostFundFlag());

//        if (model.getBussunit().equals("FIG")) {
//            map.put("utilization", "100");
//            map.put("guarantee", "0");
//        }
        if (request.isUserInRole("ROLE_TREASURY")) {
            return "raroc/rarocFacilityTrsryEdit";
        } else {
            return "raroc/rarocFacilityNew";
        }
    }

    //Edit RAROC Facility Back
    @RequestMapping(value = "/edit/back", method = RequestMethod.GET)
    public String editFacilityBack(@RequestParam(value = "ref") String ref, Map<String, Object> map,
            HttpServletRequest request) {
        RarocMasterModel model = rarocService.gridRarocHeader(ref);
//        System.out.println("PAN Card = " + model.getPanEnc());
//        System.out.println("Value = " + obtainPan(request, model.getPanEnc()));
//        model.setPan(obtainPan(request, model.getPanEnc()));
        HttpSession session = request.getSession(false);
        Integer unit = (Integer) session.getAttribute("unit");
        RarocInputModel im = rarocService.getRarocInput(model.getRarocref(), unit);
        im.setUnit(unit);
        map.put("formRarocFacility", im);
        map.put("refrec", model.getRarocref());
        map.put("expBanks", model.getExpBanks());
        map.put("corpName", model.getCname());
        map.put("facCount", model.getFacility());
        map.put("facType", rarocService.listFacility());
        map.put("maturity", rarocService.listBenchmark());
        map.put("facType_nfb", rarocService.listFacility_nfb());
        map.put("facType_bonds", rarocService.listFacility_bonds());
        map.put("reFreq", rarocService.listReFreq());
        map.put("assetType", rarocService.listAsset(model.getRtool()));
        map.put("tRating", rarocService.listTemplateRating(model.getRtool()));
        map.put("currency", rarocService.listCurrency());
        map.put("restStatus", rarocService.listRestructured());
        map.put("finSecurity", rarocService.listFinHaircut());
        map.put("bondRat", rarocService.listBondExtRat());
        map.put("psl", rarocService.listPSL());
        if (model.getRtool().equals("FBER")) {
            map.put("ltRating", rarocService.listMappedExternalRating("FBER", model.getIntRat()));
        } else {
            map.put("ltRating", rarocService.listLongExt());
        }
        if (model.getRtool().equals("DSB") || model.getRtool().equals("FBER") || model.getRtool().equals("FBIR")) {
            map.put("stVisibility", "N");
        } else {
            map.put("stVisibility", "Y");
            map.put("stRating", rarocService.listShortExt());
        }
        map.put("guarType", rarocService.listGuarType());
        map.put("guarInt", rarocService.listInternalRating());
        map.put("guarExt", rarocService.listLongExt());
        map.put("benchmark", rarocService.listBenchmark());
        map.put("curveDate", rarocService.getCurveDate());
        map.put("bbcdab", rarocService.getBA_CDAB(model.getRarocref()));
        map.put("tdfee", rarocService.getTermDepo(model.getRarocref()));
        map.put("forex", rarocService.getBB_FEE(model.getRarocref()));
        map.put("cafee", rarocService.getTresFee(model.getRarocref()));
        map.put("cms", rarocService.getCMSFee(model.getRarocref()));
        map.put("other", rarocService.getOther(model.getRarocref()));
        map.put("cfFlag", rarocService.getCostFundFlag());
        if (model.getBussunit().equals("FIG")) {
            map.put("utilization", "100");
            map.put("guarantee", "0");
        }
        if (request.isUserInRole("ROLE_TREASURY")) {
            return "raroc/rarocFacilityTrsryEdit";
        } else {
            return "raroc/rarocFacilityNew";
        }
    }

    //Edit RAROC facility
    @RequestMapping(value = "/edit/facility", method = RequestMethod.POST)
    public String editFaciltities(@ModelAttribute("formRarocFacility") RarocInputModel model,
            BindingResult result, Map<String, Object> map, HttpServletRequest request) {
        rarocService.editRarocDetails(model, request);
        map.put("form", rarocService.gridRarocHeader(model.getRefrec()));
        return "raroc/rarocSubmit";
    }

    //Submit RAROC
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submitRaroc(@RequestParam(value = "ref") String ref,
            HttpServletRequest request) {
        rarocService.submitRaroc(ref, request);
        return "redirect:/raroc";
    }

    //Grid for facilty information
    @RequestMapping(value = "/facility/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<RarocInputModel> listRarocInptGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "recref") String recref,
            HttpServletRequest request, HttpServletResponse response) throws CustomException {
        return rarocService.listRarocInputs(page, max, sidx, sord, searchField, searchOper,
                searchString, recref);
    }

    //Grid for facilty information
    @RequestMapping(value = "/facility/nfb/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<RarocInputModel> listRarocInptGrid_nfb(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "recref") String recref,
            HttpServletRequest request, HttpServletResponse response) throws CustomException {
        return rarocService.listRarocInputs_nfb(page, max, sidx, sord, searchField, searchOper,
                searchString, recref);
    }

    //Grid for facilty information
    @RequestMapping(value = "/facility/bonds/grid", method = RequestMethod.GET)
    public @ResponseBody
    GridPage<RarocInputModel> listRarocInptGrid_bonds(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "recref") String recref,
            HttpServletRequest request, HttpServletResponse response) throws CustomException {
        return rarocService.listRarocInputs_bonds(page, max, sidx, sord, searchField, searchOper,
                searchString, recref);
    }

    //Delete facilities entries
//    @RequestMapping(value = "/facility/grid/fb", method = RequestMethod.POST)
//    public void deleteRaroc(@RequestParam(value = "recref") String recref,
//            @RequestParam(value = "id") String id,
//            HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            rarocService.delRarocFacility(recref, id, request);
//        } catch (EmptyResultDataAccessException ex) {
//            logger.info(ex);
//            ex.printStackTrace();
//            response.sendError(903, msgSrc.getMessage("delete.prohibited", null, "Prohibited", null));
//        } catch (Exception ex) {
//            logger.error(ex);
//            response.sendError(900);
//        }
//    }
    //Get Mapping column from MST_GUARANTOR
    @RequestMapping(value = "/get/mapCol", method = RequestMethod.GET)
    public @ResponseBody
    String getMapCols(@RequestParam(value = "id") String id, HttpServletRequest request) {
        return rarocService.getMapCol(id);
    }

    //Get the Data on rating id from RAM Database
    @RequestMapping(value = "/data/ratingId", method = RequestMethod.GET)
    public @ResponseBody
    RarocMasterModel getRatingData(@RequestParam(value = "rid") String rid) {
        return rarocService.getRatingData(rid);
    }

    //Calculate Cost pf Fund
    @RequestMapping(value = "/get/cf", method = RequestMethod.GET)
    public @ResponseBody
    String getCF(@RequestParam(value = "facT") String facT,
            @RequestParam(value = "region") String region,
            @RequestParam(value = "intType") String intType,
            @RequestParam(value = "curr") String curr,
            @RequestParam(value = "reFrqe") String reFrqe,
            @RequestParam(value = "mult") String mult,
            @RequestParam(value = "date") String date,
            @RequestParam(value = "Mat") String Mat) {
        return rarocService.callCostOfFundFunc(facT, region, intType, curr, reFrqe, mult, date, Mat);
    }

    @RequestMapping(value = "/del/grid", method = RequestMethod.GET)
    public @ResponseBody
    String delMasRaroc(@RequestParam(value = "refNo") String refNo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = null;
        try {
            rarocService.delRaroc(refNo, request);
            System.out.println("1");
            message = "Success";
        } catch (EmptyResultDataAccessException ex) {
            logger.info(ex);
            System.out.println("2");
            //response.sendError(903, msgSrc.getMessage("delete.prohibited", null, "Prohibited", null));
            message = msgSrc.getMessage("delete.prohibited", null, "Prohibited", null);
        } catch (Exception ex) {
            logger.error(ex);
            System.out.println("3");
            //response.sendError(900);
            message = "Please Check with IT Team";
        }
        return message;
    }

    //Delete facilities entries
    @RequestMapping(value = "/facility/grid/fb/get", method = RequestMethod.GET)
    public @ResponseBody
    String Facilitydel(@RequestParam(value = "recref") String recref,
            @RequestParam(value = "id") String id,
            HttpServletRequest request, HttpServletResponse response) {
        String message = null;
        try {
            rarocService.delRarocFacility(recref, id, request);
            message = "Success";
        } catch (EmptyResultDataAccessException ex) {
            logger.info(ex);
            ex.printStackTrace();
            message = msgSrc.getMessage("delete.prohibited", null, "Prohibited", null);
        } catch (Exception ex) {
            logger.error(ex);
            //response.sendError(900);
            message = "Please Check with IT Team";
        }
        return message;
    }
}
