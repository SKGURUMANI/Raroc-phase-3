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
import atrix.common.util.GridPage;
import atrix.st.dao.MastersDao;
import atrix.st.model.MastersModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vaio
 */
@Service("masterService")
public class MastersServiceImpl implements MastersService {

    @Autowired
    private MastersDao mastersDao;
    @Autowired
    private SecurityDao securityDao;    
    
    @Override
    public GridPage<MastersModel> masterGrid(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String table) {
        if (table.equals("MST_OTH_INCOME")) {
            return mastersDao.listOtherIncome(page, max, sidx, sord, searchField, searchOper, searchString);
        } else if (table.equals("MST_CCF")) {
            return mastersDao.listCCF(page, max, sidx, sord, searchField, searchOper, searchString);
        } else if (table.equals("MST_RAROC")) {
            return mastersDao.listRaroc(page, max, sidx, sord, searchField, searchOper, searchString);
        } else if (table.equals("MST_GUARANTOR")) {
            return mastersDao.listGuarRw(page, max, sidx, sord, searchField, searchOper, searchString);
        } else if (table.equals("MST_RESTRUCTURED_RW")) {
            return mastersDao.listRestRw(page, max, sidx, sord, searchField, searchOper, searchString);
        } else if (table.equals("MST_OPERATING_EXPENSE")) {
            return mastersDao.listOpex(page, max, sidx, sord, searchField, searchOper, searchString);
        } else if (table.equals("MST_SENSITIVITY_ITERATIONS")) {
            return mastersDao.listSensitivity(page, max, sidx, sord, searchField, searchOper, searchString);
        } else {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void putMaster(MastersModel model, String table, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Master Table Edit", (String)session.getAttribute("userid"), 
                "success", "Master Table "+table+ " modified");
        securityDao.updateAppVersion();
        if (table.equals("MST_OTH_INCOME")) {
            mastersDao.updateOtherIncome(model);            
        } else if (table.equals("MST_CCF")) {
            mastersDao.updateCCF(model);
        } else if (table.equals("MST_RAROC")) {
            mastersDao.updateRaroc(model);
        } else if (table.equals("MST_GUARANTOR")) {
            mastersDao.updateGuarRw(model);
        } else if (table.equals("MST_RESTRUCTURED_RW")) {
            mastersDao.updateRestRw(model);
        } else if (table.equals("MST_OPERATING_EXPENSE")) {
            mastersDao.updateOpex(model);
        } else if (table.equals("MST_SENSITIVITY_ITERATIONS")) {
            mastersDao.updateSensitivity(model);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void postMaster(MastersModel model, String table, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Master Table Insert", (String)session.getAttribute("userid"), 
                "success", "Master Table "+table+ " modified");
        securityDao.updateAppVersion();
        if (table.equals("MST_OPERATING_EXPENSE")) {
            mastersDao.insertOpex(model);
        } else if (table.equals("MST_SENSITIVITY_ITERATIONS")) {
            mastersDao.insertSensitivity(model);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteMaster(String pk, String table, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Master Table Delete", (String)session.getAttribute("userid"), 
                "success", "Row "+ pk +" deleted from master table "+table);
        securityDao.updateAppVersion();
        if (table.equals("MST_OPERATING_EXPENSE")) {
            mastersDao.deleteOpex(pk);     
        } else if (table.equals("MST_SENSITIVITY_ITERATIONS")) {
            mastersDao.deleteSensitivity(pk);
        }
    }
}