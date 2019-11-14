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
import atrix.st.dao.FormulaDao;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesFxTreeModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vaio
 */
@Service("fxService")
public class FormulaServiceImpl implements FormulaService {

    @Autowired
    private MessageSource msgSrc;
    @Autowired
    private FormulaDao formulaDao;
    @Autowired
    private SecurityDao securityDao;
    
    @Override
    public String checkFxName(String fxName) {
        Integer i = formulaDao.checkFxName(fxName);
        if (i == 0) {
            return "success";
        } else {
            return "failed";
        }
    }

    @Override
    public GridPage<RulesFxGridModel> listFx(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type) {
        return formulaDao.listFx(page, max, sidx, sord, searchField, searchOper, searchString, type);
    }

    @Override
    public List<RulesFxTreeModel> listRulesTable() {
        return formulaDao.listRulesTable();
    }

    @Override
    public List<RulesFxTreeModel> listRulesColumns(String key) {
        return formulaDao.listRulesColumns(key);
    }    
    
    @Override
    public List<RulesFxTreeModel> listFxType() {
        return formulaDao.listFxType();
    }

    @Override
    public List<RulesFxTreeModel> listFxName(String key) {
        return formulaDao.listFxName(key);
    }

    @Override
    public List<RulesFxTreeModel> listOperatorType() {
        return formulaDao.listOperatorType();
    }

    @Override
    public List<RulesFxTreeModel> listOperatorName(String key) {
        return formulaDao.listOperatorName(key);
    }

    @Override
    public String getFxWarning(String fxId) {
        String check = formulaDao.getFxWarning(fxId);
        if (check == null || check.equals("")) {
            return "no_mapping";
        } else {
            return msgSrc.getMessage("msg.fxWarning.exists", null, null, null) + "<br />" + check;
        }
    }

    @Override
    public void insertFx(RulesFxGridModel expModel, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        formulaDao.insertFx(expModel);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Add formula", (String) session.getAttribute("userid"),
                "success", "New formula '" + expModel.getFormulaName() + "' has been added");
    }

    @Override
    public RulesFxGridModel getFormula(String fxid) {
        return formulaDao.getFormula(fxid);
    }

    @Override
    public void deleteFx(String formulaId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        formulaDao.deleteFx(formulaId);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Delete formula", (String) session.getAttribute("userid"),
                "success", "Formula '" + formulaId + "' has been deleted");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateFx(RulesFxGridModel expModel, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        formulaDao.updateFx(expModel);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Modify formula", (String) session.getAttribute("userid"),
                "success", "Formula '" + expModel.getFormulaName() + "' has been modified");
    }
}