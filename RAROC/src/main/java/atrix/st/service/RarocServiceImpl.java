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

import atrix.common.dao.SecurityDao;
import atrix.common.model.MessageModel;
import atrix.common.model.OptionsModel;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithFooterPage;
import atrix.st.dao.RarocDao;
import atrix.st.exception.CustomException;
import atrix.st.model.RarocAuthorize;
import atrix.st.model.RarocGridModel;
import atrix.st.model.RarocInputModel;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vaio
 */
@Service("rarocService")
public class RarocServiceImpl implements RarocService {

    @Autowired
    private RarocDao rarocDao;
    @Autowired
    private SecurityDao securityDao;
    @Autowired
    private MessageSource msgSrc;

    @Override
    public GridPage<RarocMasterModel> listRarocMaster(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, HttpServletRequest request) throws CustomException {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        return rarocDao.listRarocMaster(page, max, sidx, sord, searchField, searchOper, searchString, userid);
    }

    @Override
    public GridPage<RarocInputModel> listRarocInputs(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException {
        return rarocDao.listRarocInputs(page, max, sidx, sord, searchField, searchOper, searchString, refId);
    }

    @Override
    public GridPage<RarocInputModel> listRarocInputs_nfb(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException {
        return rarocDao.listRarocInputs_nfb(page, max, sidx, sord, searchField, searchOper, searchString, refId);
    }

    @Override
    public GridPage<RarocInputModel> listRarocInputs_bonds(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException {
        return rarocDao.listRarocInputs_bonds(page, max, sidx, sord, searchField, searchOper, searchString, refId);
    }

    @Override
    public MessageModel checkAvailability(String rid, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        MessageModel m = new MessageModel();
        String userid = (String) session.getAttribute("userid");
        String ret = rarocDao.checkAvailability1(rid, userid);
        if (ret == null) {
            if (rarocDao.checkAvailability2(rid, userid) == 0) {
                m.setMesgType("empty");
                m.setMesgValue("");
            } else {
                m.setMesgType("error");
                m.setMesgValue(msgSrc.getMessage("raroc.rid.submitted", new Object[]{ret}, null));
            }
        } else {
            m.setMesgType("error");
            m.setMesgValue(msgSrc.getMessage("raroc.rid.unavailable", new Object[]{ret}, null));
        }
        return m;
    }

    @Override
    public String checkRarocExistence(String rid, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        return rarocDao.checkRarocExistence(rid, userid);
    }

    @Override
    public GridPage<RarocGridModel> listRarocInput(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String recref) throws CustomException {
        return rarocDao.listRarocInput(page, max, sidx, sord, searchField, searchOper, searchString, recref);
    }

    @Override
    public RarocMasterModel gridRarocHeader(String ref) {
        return rarocDao.gridRarocHeader(ref);
    }

    @Override
    public RarocMasterModel editRarocHeader(String ref) {
        if (!(rarocDao.getRecordStatus(ref).equals("I") || rarocDao.getRecordStatus(ref).equals("R"))) {
            return null;
        } else {
            return rarocDao.gridRarocHeader(ref);
        }
    }

    @Override
    public List<OptionsModel> getFacilitylist(String ref) {
        return rarocDao.getFacilitylist(ref);
    }

    @Override
    public List<RarocViewModel> listSensitivityRW(String ref, String facility) {
        return rarocDao.listSensitivityRW(ref, facility);
    }

    @Override
    public List<RarocViewModel> listSensitivityUtil(String ref, String facility) {
        return rarocDao.listSensitivityUtil(ref, facility);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocView(String ref, Integer unit) {
        return rarocDao.gridRarocView(ref, unit);
    }
    
    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocNewView(String ref, Integer unit) {
        return rarocDao.gridRarocNewView(ref, unit);
    }
    
    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocNewViewNext(String ref, Integer unit) {
        return rarocDao.gridRarocNewViewNext(ref, unit);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRwaCalc(String ref) {
        return rarocDao.gridRwaCalc(ref);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocCalc(String ref) {
        return rarocDao.gridRarocCalc(ref);
    }

    @Override
    public List<OptionsModel> listBu(HttpServletRequest request) {
        List<OptionsModel> list = new ArrayList<OptionsModel>();
        if (request.isUserInRole("ROLE_TREASURY")) {
            OptionsModel obj = new OptionsModel();
            obj.setKey("Treasury");
            obj.setValue("Treasury");
            list.add(obj);
            return list;
        } else {
            return rarocDao.listBu();
        }
    }

    @Override
    public List<OptionsModel> listModel() {
        return rarocDao.listModel();
    }

    @Override
    public List<OptionsModel> listInternalRating(String model) {
        return rarocDao.listInternalRating(model);
    }

    @Override
    public List<OptionsModel> listGuarIntRating(String model) {
        return rarocDao.listGuarIntRating(model);
    }

    @Override
    public String getCMSFee(String refId){
        return rarocDao.getCMSFee(refId);
    }
    
    @Override
    public List<OptionsModel> listTemplateRating(String model) {
        List<OptionsModel> list = new ArrayList<OptionsModel>();
        if (!model.equals("SME Schematic Non Trade") || !model.equals("SME Schematic Trade")) {
            OptionsModel obj = new OptionsModel();
            obj.setKey("N/A");
            obj.setValue("Non-Templated");
            list.add(obj);
        }
        list.addAll(rarocDao.listInternalRating("Templated"));
        return list;
    }

    @Override
    public List<OptionsModel> listIndustry() {
        return rarocDao.listIndustry();
    }

    @Override
    public List<OptionsModel> listBenchmark() {
        return rarocDao.listBenchmark();
    }

    @Override
    public List<OptionsModel> listNames(String value) {
        return rarocDao.listCustName(value);
    }

    @Override
    public String getCurveDate() {
        return rarocDao.getCurveDate();
    }

    @Override
    public String getCname(String ref) {
        return rarocDao.getCname(ref);
    }

    @Override
    public String getBA_CDAB(String id) {
        return rarocDao.getBA_CDAB(id);
    }

    @Override
    public String getTermDepo(String id) {
        return rarocDao.getTermDepo(id);
    }

    @Override
    public String getBB_FEE(String id) {
        return rarocDao.getBB_FEE(id);
    }

    @Override
    public String getTresFee(String id) {
        return rarocDao.getTresFee(id);
    }

    @Override
    public String getOther(String id) {
        return rarocDao.getOther(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String addRarocMaster(RarocMasterModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        String refseq = "Ref-" + rarocDao.getSequence();
        securityDao.insertOperAudit(audseq, "Insert RAROC Master", userid,
                "success", "Ref number '" + refseq + "' inserted");
        model.setRarocref(refseq);
        rarocDao.addRarocMaster(model, userid);
        return refseq;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String existingRarocMaster(String oldref, RarocMasterModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String pass = (String) session.getAttribute("pass");
        String audseq = securityDao.getAuditSequence();
        String refseq = "Ref-" + rarocDao.getSequence();
        securityDao.insertOperAudit(audseq, "Insert RAROC Master", userid,
                "success", "Ref number '" + refseq + "' inserted");
        rarocDao.rarocStatus(oldref, userid, "H", pass);
        model.setRarocref(refseq);
        rarocDao.addRarocMaster(model, userid);
        return refseq;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void editRarocMaster(RarocMasterModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Edit RAROC Master", userid,
                "success", "Ref number '" + model.getRarocref() + "' modified");
        rarocDao.editRarocMaster(model, userid);
    }

    @Override
    public void delRaroc(String recref, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Delete RAROC", userid,
                "success", "Ref number '" + recref + "' deleted");
        int status = rarocDao.delRarocMaster(recref, userid);
        System.out.println("Staus: "+status);
        if (status == 0) {
            throw new EmptyResultDataAccessException(903);
        } else {
            rarocDao.delRarocInput(recref);
            rarocDao.delRarocResults(recref);
        }
    }

    @Override
    public List<OptionsModel> listFacility() {
        return rarocDao.listFacility();
    }

    @Override
    public List<OptionsModel> listAsset(String rTool) {
        if (rTool.equals("DSB")) {
            List<OptionsModel> lObj = new ArrayList<OptionsModel>();
            OptionsModel obj = new OptionsModel();
            obj.setKey("Sch Bank(CAR > 9%)");
            obj.setValue("Sch Bank(CAR > 9%)");
            lObj.add(obj);
            obj = new OptionsModel();
            obj.setKey("Sch Bank(CAR:6% - 9%)");
            obj.setValue("Sch Bank(CAR:6% - 9%)");
            lObj.add(obj);
            return lObj;
        } else if (rTool.equals("FBER") || rTool.equals("FBIR")) {
            List<OptionsModel> lObj = new ArrayList<OptionsModel>();
            OptionsModel obj = new OptionsModel();
            obj.setKey("Foreign Bank");
            obj.setValue("Foreign Bank");
            lObj.add(obj);
            return lObj;
        } else {
            return rarocDao.listAsset();
        }
    }

    @Override
    public List<OptionsModel> listCurrency() {
        return rarocDao.listCurrency();
    }

    @Override
    public List<OptionsModel> listRestructured() {
        return rarocDao.listRestructured();
    }

    @Override
    public List<OptionsModel> listLongExt() {
        return rarocDao.listLongExt();
    }

    @Override
    public List<OptionsModel> listShortExt() {
        return rarocDao.listShortExt();
    }

    @Override
    public List<OptionsModel> listLongExt(String astType) {
        return rarocDao.listLongExt(astType);
    }

    @Override
    public List<OptionsModel> listGuarType() {
        return rarocDao.listGuarType();
    }

    @Override
    public List<OptionsModel> listInternalRating() {
        return rarocDao.listInternalRating();
    }

    @Override
    public List<OptionsModel> listMappedExternalRating(String model, String rating) {
        return rarocDao.listMappedExternalRating(model, rating);
    }

    @Override
    public List<OptionsModel> listFinHaircut() {
        return rarocDao.listFinHaircut();
    }

    @Override
    public void addRarocDetails(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Facility Added", userid,
                "success", "RAROC Facility Added For FB '" + model.getRefrec());
        rarocDao.addRarocDetails(model);
    }

    @Override
    public void editRarocDetails(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Calculate RAROC", userid,
                "success", "RAROC calculated for '" + model.getRefrec());
        rarocDao.editRarocDetails(model);
    }

    @Override
    public void submitFacility(String recref, String derivate, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Submit RAROC", userid,
                "success", "Current FY RAROC submitted for reference id '" + recref);
        System.out.println("I ahere oo");
        rarocDao.submitRarocDetails(recref, derivate);
    }
    
    @Override
    public void submitFacilityNext(String recref, String derivate, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Submit RAROC", userid,
                "success", "Next FY RAROC submitted for reference id '" + recref);
        System.out.println("I ahere oo");
        rarocDao.submitRarocDetailsNext(recref, derivate);
    }
    
    @Override
    public List<OptionsModel> listBondExtRat(){
        return rarocDao.listBondExtRat();
    }
    
    @Override
    public List<OptionsModel> listBondCet(){
        return rarocDao.listBondCet();
    }

    @Override
    public void submitRaroc(String recref, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String pass = (String) session.getAttribute("pass");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Submit RAROC", userid,
                "success", "RAROC submitted for reference id '" + recref);
        rarocDao.rarocStatus(recref, userid, "S", pass);
    }

    @Override
    public RarocInputModel getRarocInput(String recref, Integer unit) {
        return rarocDao.getRarocInput(recref, unit);
    }

    @Override
    public RarocInputModel getExistingInput(String recref, String facnos, Integer unit) {
        return rarocDao.getExistingInput(recref, facnos, unit);
    }

    @Override
    public GridPage<RarocMasterModel> listRaroc(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, HttpServletRequest request) throws CustomException {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        return rarocDao.listRaroc(page, max, sidx, sord, searchField, searchOper, searchString, userid);
    }

    @Override
    public RarocMasterModel adminEditRarocHeader(String ref) {
        if (!rarocDao.getRecordStatus(ref).equals("I")) {
            return rarocDao.gridRarocHeader(ref);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void adminEditRarocMaster(RarocMasterModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Edit RAROC Master", userid,
                "success", "Ref number '" + model.getRarocref() + "' modified");
        rarocDao.adminEditRarocMaster(model, userid);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void adminRecordStatus(RarocAuthorize model, String status, String ref,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Update record status", userid,
                "success", "Ref number '" + ref + " " + status);
        rarocDao.adminRecordStatus(model, status, userid, ref);
    }

    @Override
    public String getApproverComments(String refno) {
        return rarocDao.getApproverComments(refno);
    }

    @Override
    public String getCostFundFlag() {
        return rarocDao.getCostFundFlag();
    }

    @Override
    public String getMapCol(String id) {
        return rarocDao.getMapCol(id);
    }

    @Override
    public List<OptionsModel> listPSL() {
        return rarocDao.listPSL();
    }

    @Override
    public void delRarocFacility(String recref, String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Delete Facility", userid,
                "success", "Facility id '" + id + "' deleted for Ref id" + recref);
        int status = rarocDao.delRarocFacility(recref, id);
        if (status == 0) {
            throw new EmptyResultDataAccessException(903);
        }
    }

    @Override
    public List<OptionsModel> listRatingIds() {
        return rarocDao.listRatingIds();
    }

    @Override
    public RarocMasterModel getRatingData(String ratingId) {
        return rarocDao.getRatingData(ratingId);
    }

    @Override
    public void addRarocDetails_nfb(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Facility Added", userid,
                "success", "RAROC Facility Added For NFB '" + model.getRefrec());
        rarocDao.addRarocDetails_nfb(model);
    }

    @Override
    public void updateRarocDetails_fb(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Facility Updated", userid,
                "success", "RAROC Facility Updated For FB '" + model.getRefrec() + "And Facility No is " + model.getFacNo());
        rarocDao.updateRarocDetails_fb(model);
    }

    @Override
    public void updateRarocDetails_nfb(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Facility Updated", userid,
                "success", "RAROC Facility Updated For NFB '" + model.getRefrec() + "And Facility No is " + model.getFacNo());
        rarocDao.updateRarocDetails_nfb(model);
    }

    @Override
    public List<OptionsModel> listFacility_nfb() {
        return rarocDao.listFacility_nfb();
    }

    @Override
    public List<OptionsModel> listFacility_bonds() {
        return rarocDao.listFacility_bonds();
    }

    @Override
    public List<OptionsModel> listReFreq() {
        return rarocDao.listReFreq();
    }

    @Override
    public void addRarocDetails_bonds(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Facility Added", userid,
                "success", "RAROC Facility Added For BONDS '" + model.getRefrec());
        rarocDao.addRarocDetails_bonds(model);
    }

    @Override
    public void updateRarocDetails_bonds(RarocInputModel model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Facility Updated", userid,
                "success", "RAROC Facility Updated For BONDS '" + model.getRefrec() + "And Facility No is " + model.getFacNo());
        rarocDao.updateRarocDetails_bonds(model);
    }

    @Override
    public String callCostOfFundFunc(String facType, String country, String intType, String curr, String repFreq, String mult,String oriDate, String OriMat) {
        return rarocDao.callCostOfFundFunc(facType, country, intType, curr, repFreq, mult, oriDate, OriMat);
    }
}
