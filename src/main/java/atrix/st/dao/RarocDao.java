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
package atrix.st.dao;

import atrix.common.model.OptionsModel;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithFooterPage;
import atrix.st.exception.CustomException;
import atrix.st.model.RarocAuthorize;
import atrix.st.model.RarocGridModel;
import atrix.st.model.RarocInputModel;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vaio
 */
public interface RarocDao {
 
    public GridPage<RarocMasterModel> listRarocMaster(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String userid) throws CustomException;
    
    public RarocMasterModel gridRarocHeader(String ref);
    
    public String getRecordStatus(String ref);
    
    public String checkAvailability1(String rid, String userid);
    
    public int checkAvailability2(String rid, String userid);
    
    public String checkRarocExistence(String rid, String userid);
    
    public GridPage<RarocGridModel> listRarocInput(int page, int max, String sidx, String sord, 
            String searchField, String searchOper, String searchString, String recref) throws CustomException;
    
    public List<OptionsModel> getFacilitylist(String ref);
    
    public List<RarocViewModel> listSensitivityRW(String ref, String facility);
    
    public List<RarocViewModel> listSensitivityUtil(String ref, String facility);
    
    public GridWithFooterPage<RarocViewModel> gridRarocView(String ref, Integer unit);
    
    public GridWithFooterPage<RarocViewModel> gridRwaCalc(String ref);
    
    public GridWithFooterPage<RarocViewModel> gridRarocCalc(String ref);
    
    public List<OptionsModel> listBu();
    
    public List<OptionsModel> listModel();
    
    public List<OptionsModel> listInternalRating(String model);
    
    public List<OptionsModel> listGuarIntRating(String model);
    
    public List<OptionsModel> listIndustry();
    
    public List<OptionsModel> listBenchmark();
    
    public List<OptionsModel> listCustName(String value);
    
    public String getSequence();
    
    public void addRarocMaster(RarocMasterModel model, String user);
    
    public void editRarocMaster(RarocMasterModel model, String user);    
    
    public int delRarocMaster(String recref, String userid);
    
    public void delRarocInput(String recref);
    
    public void delRarocResults(String recref);
    
    public List<OptionsModel> listFacility();
    
    public List<OptionsModel> listAsset();
    
    public List<OptionsModel> listCurrency();
    
    public List<OptionsModel> listRestructured();
    
    public List<OptionsModel> listLongExt();        
    
    public List<OptionsModel> listLongExt(String astType);
    
    public List<OptionsModel> listShortExt();    
    
    public List<OptionsModel> listGuarType();
    
    public List<OptionsModel> listInternalRating();  
    
    public List<OptionsModel> listMappedExternalRating(String model, String rating);
    
    public List<OptionsModel> listFinHaircut();
    
    public String getCurveDate();
    
    public void addRarocDetails(RarocInputModel model);
    
    public RarocInputModel getRarocInput(String recref, Integer unit);
    
    public RarocInputModel getExistingInput(String recref, String facnos, Integer unit);
    
    public void editRarocDetails(RarocInputModel model);
    
    public void rarocStatus(String recref, String user, String status, String pass);
    
    public GridPage<RarocMasterModel> listRaroc(int page, int max, String sidx, String sord, 
            String searchField, String searchOper, String searchString, String userid) throws CustomException;
        
    public void adminEditRarocMaster(RarocMasterModel model, String user);
    
    public void adminRecordStatus(RarocAuthorize model, String status, String user, String ref);
    
    public String getApproverComments(String refno);
    
    public String getBA_CDAB(String refId);
    
    public String getTermDepo(String refId);
    
    public String getBB_FEE(String refId);
    
    public String getTresFee(String refId);
    
    public String getOther(String refId);
    
    public GridPage<RarocInputModel> listRarocInputs(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException;
    
    public List<String> listFacNumber(String refRec);
    
    public void submitRarocDetails(String refRec, String facType);
    
    public String getCostFundFlag();
    
    public List<OptionsModel> listPSL();
    
    public int delRarocFacility(String recref, String id) ;
    
    public String getMapCol(String id);
    
    public String getCname(String ref);
    
    public RarocMasterModel getRatingData(String ratingId);
    
    public List<OptionsModel> listRatingIds();
    
    public void addRarocDetails_nfb(RarocInputModel model);
    
    public void updateRarocDetails_fb(RarocInputModel model);
    
    public void updateRarocDetails_nfb(RarocInputModel model);
    
    public GridPage<RarocInputModel> listRarocInputs_bonds(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException;
    
    public GridPage<RarocInputModel> listRarocInputs_nfb(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException;
            
    public List<OptionsModel> listFacility_nfb();
    
    public List<OptionsModel> listFacility_bonds();
    
    public List<OptionsModel> listReFreq();
    
    public void addRarocDetails_bonds(RarocInputModel model);
            
    public void updateRarocDetails_bonds(RarocInputModel model);
    
    public String getCMSFee(String refId);
            
    public String callCostOfFundFunc(String facType, String country, String intType, String curr, String repFreq, String mult, String oriDate, String OriMat);
    
    public void submitRarocDetailsNext(String refRec, String facType);
    
    public GridWithFooterPage<RarocViewModel> gridRarocNewView(String ref, Integer unit);
    
    public List<OptionsModel> listSubHeader(String id);
    
    public GridWithFooterPage<RarocViewModel> gridRarocNewViewNext(String ref, Integer unit);
    
    public List<OptionsModel> listBondExtRat();
    
    public List<OptionsModel> listBondCet();
}