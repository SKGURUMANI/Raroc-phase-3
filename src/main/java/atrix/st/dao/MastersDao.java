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

import atrix.common.util.GridPage;
import atrix.st.model.MastersModel;
import java.util.Map;

/**
 *
 * @author vaio
 */
public interface MastersDao {

    public MastersModel getCurrLocalization();

    public void updateCurrLocalization(MastersModel model);
    
    public Map<String, String> getMasterTables(String view);
    
    public GridPage<MastersModel> listOtherIncome(int page, int max, String sidx, String sord, 
            String searchField, String searchOper, String searchString);
    
    public void updateOtherIncome(MastersModel model);
    
    public GridPage<MastersModel> listCCF(int page, int max, String sidx, String sord, 
            String searchField, String searchOper, String searchString);
    
    public void updateCCF(MastersModel model);
    
    public GridPage<MastersModel> listRaroc(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public void updateRaroc(MastersModel model);
    
    public GridPage<MastersModel> listGuarRw(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public void updateGuarRw(MastersModel model);
    
    public GridPage<MastersModel> listRestRw(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public void updateRestRw(MastersModel model);
    
    public GridPage<MastersModel> listOpex(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public void updateOpex(MastersModel model);
    
    public void insertOpex(MastersModel model);
    
    public void deleteOpex(String pk);
    
    public GridPage<MastersModel> listSensitivity(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public void updateSensitivity(MastersModel model);
    
    public void insertSensitivity(MastersModel model);
    
    public void deleteSensitivity(String pk);
}