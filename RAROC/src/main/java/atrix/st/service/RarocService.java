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

import atrix.common.model.MessageModel;
import atrix.common.model.OptionsModel;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithFooterPage;
import atrix.st.exception.CustomException;
import atrix.st.model.RarocAuthorize;
import atrix.st.model.RarocGridModel;
import atrix.st.model.RarocInputModel;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface RarocService {

    public GridPage<RarocMasterModel> listRarocMaster(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, HttpServletRequest request) throws CustomException;

    public MessageModel checkAvailability(String rid, HttpServletRequest request);

    public String checkRarocExistence(String rid, HttpServletRequest request);
    
    public List<OptionsModel> listBondExtRat();

    public GridPage<RarocGridModel> listRarocInput(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String recref) throws CustomException;

    public RarocMasterModel gridRarocHeader(String ref);

    public RarocMasterModel editRarocHeader(String ref);
    
    public String getCMSFee(String refId);

    public List<OptionsModel> getFacilitylist(String ref);

    public List<RarocViewModel> listSensitivityRW(String ref, String facility);

    public List<RarocViewModel> listSensitivityUtil(String ref, String facility);

    public GridWithFooterPage<RarocViewModel> gridRarocView(String ref, Integer unit);

    public GridWithFooterPage<RarocViewModel> gridRwaCalc(String ref);

    public GridWithFooterPage<RarocViewModel> gridRarocCalc(String ref);

    public List<OptionsModel> listBu(HttpServletRequest request);

    public List<OptionsModel> listModel();

    public List<OptionsModel> listInternalRating(String model);

    public List<OptionsModel> listTemplateRating(String model);

    public List<OptionsModel> listIndustry();

    public List<OptionsModel> listNames(String value);

    public String getCurveDate();

    public String addRarocMaster(RarocMasterModel model, HttpServletRequest request);

    public String existingRarocMaster(String oldref, RarocMasterModel model, HttpServletRequest request);

    public void editRarocMaster(RarocMasterModel model, HttpServletRequest request);

    public void delRaroc(String recref, HttpServletRequest request);

    public List<OptionsModel> listFacility();

    public List<OptionsModel> listAsset(String rTool);

    public List<OptionsModel> listCurrency();

    public List<OptionsModel> listRestructured();

    public List<OptionsModel> listLongExt();

    public List<OptionsModel> listShortExt();

    public List<OptionsModel> listLongExt(String astType);

    public List<OptionsModel> listGuarType();

    public List<OptionsModel> listInternalRating();

    public List<OptionsModel> listMappedExternalRating(String model, String rating);

    public List<OptionsModel> listFinHaircut();

    public List<OptionsModel> listGuarIntRating(String model);

    public List<OptionsModel> listBenchmark();

    public void addRarocDetails(RarocInputModel model, HttpServletRequest request);

    public void editRarocDetails(RarocInputModel model, HttpServletRequest request);

    public void submitRaroc(String recref, HttpServletRequest request);

    public RarocInputModel getRarocInput(String recref, Integer unit);

    public RarocInputModel getExistingInput(String recref, String facnos, Integer unit);

    public GridPage<RarocMasterModel> listRaroc(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, HttpServletRequest request) throws CustomException;

    public RarocMasterModel adminEditRarocHeader(String ref);

    public void adminEditRarocMaster(RarocMasterModel model, HttpServletRequest request);

    public void adminRecordStatus(RarocAuthorize model, String status, String ref, HttpServletRequest request);

    public String getApproverComments(String refno);

    public String getBA_CDAB(String id);

    public String getTermDepo(String id);

    public String getBB_FEE(String id);

    public String getTresFee(String id);

    public String getOther(String id);

    public GridPage<RarocInputModel> listRarocInputs(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException;

    public void submitFacility(String recref, String derivate, HttpServletRequest request);

    public String getCostFundFlag();

    public List<OptionsModel> listPSL();

    public void delRarocFacility(String recref, String id, HttpServletRequest request);

    public String getMapCol(String id);
    
    public String getCname(String ref);
    
    public List<OptionsModel> listRatingIds();
    
    public RarocMasterModel getRatingData(String ratingId);

    public void addRarocDetails_nfb(RarocInputModel model, HttpServletRequest request);
    
    public void updateRarocDetails_fb(RarocInputModel model, HttpServletRequest request);
    
    public GridPage<RarocInputModel> listRarocInputs_nfb(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException;
    
    public GridPage<RarocInputModel> listRarocInputs_bonds(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException;
    
    public void updateRarocDetails_nfb(RarocInputModel model, HttpServletRequest request);
    
    public List<OptionsModel> listFacility_bonds();
    
    public List<OptionsModel> listFacility_nfb();
    
    public List<OptionsModel> listReFreq();
    
    public void addRarocDetails_bonds(RarocInputModel model, HttpServletRequest request);
    
    public void updateRarocDetails_bonds(RarocInputModel model, HttpServletRequest request);
    
    public String callCostOfFundFunc(String facType, String country, String intType, String curr, String repFreq, String mult, String oriDate, String OriMat);
    
    public void submitFacilityNext(String recref, String derivate, HttpServletRequest request);
    
    public GridWithFooterPage<RarocViewModel> gridRarocNewView(String ref, Integer unit);
    
    public GridWithFooterPage<RarocViewModel> gridRarocNewViewNext(String ref, Integer unit);
    
    public List<OptionsModel> listBondCet();
}
