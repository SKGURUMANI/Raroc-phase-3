/*
 * � 2013 Asymmetrix Solutions Private Limited. All rights reserved.
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
import atrix.st.dao.RulesFrameworkDao;
import atrix.st.dao.RulesMappingDao;
import atrix.st.model.RulesFrameworkModel;
import atrix.st.model.RulesOptionModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author vaio
 */
@Service("rulesService")
public class RulesFrameworkServiceImpl implements RulesFrameworkService {

    @Autowired
    private MessageSource msgSrc;
    @Autowired
    private RulesFrameworkDao rulesFrameworkDao;
    @Autowired
    private RulesMappingDao rulesMappingDao;
    @Autowired
    private SecurityDao securityDao;

    @Override
    public List<RulesOptionModel> listTables(String option) {
        return rulesFrameworkDao.listTables(option);
    }

    @Override
    public List<RulesOptionModel> listColumns(String table) {
        return rulesFrameworkDao.listColumns(table);
    }

    @Override
    public List<RulesOptionModel> listPkColumns(String table) {
        return rulesFrameworkDao.listPkColumns(table);
    }

    @Override
    public List<RulesOptionModel> listCondition(String tab, String col) {
        List<RulesOptionModel> bandConditionList = new ArrayList<RulesOptionModel>();

        if (tab != null && !tab.equals("") && col != null && !col.equals("")) {
            String dataType = rulesFrameworkDao.listConditions(tab, col);

            RulesOptionModel obj = new RulesOptionModel();
            if(tab.substring(0, 4).equals("RSLT")) {                
                obj.setCode("select");
                obj.setDesc("Select...");
            bandConditionList.add(obj);
            } else {
                obj.setCode("");
                obj.setDesc("");
                bandConditionList.add(obj);
            }                 
            
            obj = new RulesOptionModel();
            obj.setCode("eq");
            obj.setDesc("Equal To");
            bandConditionList.add(obj);

            obj = new RulesOptionModel();
            obj.setCode("neq");
            obj.setDesc("Not Equal To");
            bandConditionList.add(obj);

            if (dataType.equals("NUMBER")) {
                obj = new RulesOptionModel();
                obj.setCode("bw");
                obj.setDesc("Between");
                bandConditionList.add(obj);

                obj = new RulesOptionModel();
                obj.setCode("nbw");
                obj.setDesc("Not Between");
                bandConditionList.add(obj);

                obj = new RulesOptionModel();
                obj.setCode("gt");
                obj.setDesc("Greater Than");
                bandConditionList.add(obj);

                obj = new RulesOptionModel();
                obj.setCode("gte");
                obj.setDesc("Greater Than Equal To");
                bandConditionList.add(obj);

                obj = new RulesOptionModel();
                obj.setCode("lt");
                obj.setDesc("Less Than");
                bandConditionList.add(obj);

                obj = new RulesOptionModel();
                obj.setCode("lte");
                obj.setDesc("Less Than Equal To");
                bandConditionList.add(obj);
            } else {
                obj = new RulesOptionModel();
                obj.setCode("cn");
                obj.setDesc("Contains");
                bandConditionList.add(obj);

                obj = new RulesOptionModel();
                obj.setCode("ncn");
                obj.setDesc("Does Not Contain");
                bandConditionList.add(obj);
            }
        }
        return bandConditionList;
    }
    
    @Override
    public String checkRuleName(RulesFrameworkModel model) {
        String result = "success";
        if (model.getRuleName() == null || model.getRuleName().equals("")) {
            return msgSrc.getMessage("msg.rule.null", null, null, null);
        } else if (model.getRuleName().length() > 100) {
            return msgSrc.getMessage("msg.rule.100", null, null, null);
        } else if (rulesFrameworkDao.checkRuleName(model.getRuleName()) > 0) {
            return msgSrc.getMessage("msg.rule.duplicate", null, null, null);
        } else if (model.getRuleDesc().length() > 1000) {
            return msgSrc.getMessage("msg.ruleDesc.length", null, null, null);
        }
        return result;
    }

    @Override
    public String validateRuleData(RulesFrameworkModel model) {
        String result = "success";
        if (model.getRuleCat().equals("VM") && (model.getUpdateTable() == null || model.getUpdateTable().equals(""))) {
            return msgSrc.getMessage("msg.update.table", null, null, null);
        } else if ((model.getRuleCat() != null && model.getRuleCat().equals("VM"))
                && (model.getdColumn() == null || model.getdColumn().equals(""))) {
            return msgSrc.getMessage("msg.destination.table", null, null, null);
        } else if (model.getsColumn1().equals("") && model.getsColumn2().equals("") && model.getsColumn3().equals("")
                && model.getsColumn4().equals("") && model.getsColumn5().equals("")) {
            return msgSrc.getMessage("msg.source.selection", null, null, null);            
        } else if ((model.getBc1() != null && model.getBc1().equals("select")) || 
                   (model.getBc2() != null && model.getBc2().equals("select")) || 
                   (model.getBc3() != null && model.getBc3().equals("select")) ||
                   (model.getBc4() != null && model.getBc4().equals("select")) ||
                   (model.getBc5() != null && model.getBc5().equals("select"))) {
            return msgSrc.getMessage("msg.bc.null", null, null, null);            
        }
        return result;
    }        

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public Integer addRule(RulesFrameworkModel rulesModel, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Add rule", (String) session.getAttribute("userid"),
                "success", "New rule '" + rulesModel.getRuleName() + "' has been added");
        return rulesFrameworkDao.addRule(rulesModel, audseq);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public Integer modifyRule(RulesFrameworkModel rulesModel, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        RulesFrameworkModel oldModel = rulesFrameworkDao.getRule(rulesModel.getRuleName());
        
        if(rulesModel.getsColumn1() == null && oldModel.getsColumn1() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE1_CD", "C_SOURCE1", rulesModel.getRuleName());
        } else if(rulesModel.getsColumn1() != null && oldModel.getsColumn1() != null && 
                !rulesModel.getsColumn1().equals(oldModel.getsColumn1())) {
            rulesMappingDao.updateMappingNull("C_SOURCE1_CD", "C_SOURCE1", rulesModel.getRuleName());
        }
        
        if(rulesModel.getsColumn2() == null && oldModel.getsColumn2() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE2_CD", "C_SOURCE2", rulesModel.getRuleName());
        } else if(rulesModel.getsColumn2() != null && oldModel.getsColumn2() != null && 
                !rulesModel.getsColumn2().equals(oldModel.getsColumn2())) {
            rulesMappingDao.updateMappingNull("C_SOURCE2_CD", "C_SOURCE2", rulesModel.getRuleName());
        }
        
        if(rulesModel.getsColumn3() == null && oldModel.getsColumn3() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE3_CD", "C_SOURCE3", rulesModel.getRuleName());
        } else if(rulesModel.getsColumn3() != null && oldModel.getsColumn3() != null && 
                !rulesModel.getsColumn3().equals(oldModel.getsColumn3())) {
            rulesMappingDao.updateMappingNull("C_SOURCE3_CD", "C_SOURCE3", rulesModel.getRuleName());
        }
        
        if(rulesModel.getsColumn4() == null && oldModel.getsColumn4() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE4_CD", "C_SOURCE4", rulesModel.getRuleName());
        } else if(rulesModel.getsColumn4() != null && oldModel.getsColumn4() != null && 
                !rulesModel.getsColumn4().equals(oldModel.getsColumn4())) {
            rulesMappingDao.updateMappingNull("C_SOURCE4_CD", "C_SOURCE4", rulesModel.getRuleName());
        }
        
        if(rulesModel.getsColumn5() == null && oldModel.getsColumn5() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE5_CD", "C_SOURCE5", rulesModel.getRuleName());
        } else if(rulesModel.getsColumn5() != null && oldModel.getsColumn5() != null && 
                !rulesModel.getsColumn5().equals(oldModel.getsColumn5())) {
            rulesMappingDao.updateMappingNull("C_SOURCE5_CD", "C_SOURCE5", rulesModel.getRuleName());
        }
        
        if(rulesModel.getdColumn() == null && oldModel.getdColumn() != null) {
            rulesMappingDao.updateMappingNull("C_DESTINATION_CD", "C_DESTINATION", rulesModel.getRuleName());
        } else if(rulesModel.getdColumn() != null && oldModel.getdColumn() != null && 
                !rulesModel.getdColumn().equals(oldModel.getdColumn())) {
            rulesMappingDao.updateMappingNull("C_DESTINATION_CD", "C_DESTINATION", rulesModel.getRuleName());
        }
        
        if(rulesModel.getBc1() == null && oldModel.getBc1() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE1_CD", "C_SOURCE1", rulesModel.getRuleName());
        } else if(rulesModel.getBc1() != null && oldModel.getBc1() != null && 
                !rulesModel.getBc1().equals(oldModel.getBc1())) {
            rulesMappingDao.updateMappingNull("C_SOURCE1_CD", "C_SOURCE1", rulesModel.getRuleName());
        }
        
        if(rulesModel.getBc2() == null && oldModel.getBc2() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE2_CD", "C_SOURCE2", rulesModel.getRuleName());
        } else if(rulesModel.getBc2() != null && oldModel.getBc2() != null && 
                !rulesModel.getBc2().equals(oldModel.getBc2())) {
            rulesMappingDao.updateMappingNull("C_SOURCE2_CD", "C_SOURCE2", rulesModel.getRuleName());
        }
        
        if(rulesModel.getBc3() == null && oldModel.getBc3() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE3_CD", "C_SOURCE3", rulesModel.getRuleName());
        } else if(rulesModel.getBc3() != null && oldModel.getBc3() != null && 
                !rulesModel.getBc3().equals(oldModel.getBc3())) {
            rulesMappingDao.updateMappingNull("C_SOURCE3_CD", "C_SOURCE3", rulesModel.getRuleName());
        }
        
        if(rulesModel.getBc4() == null && oldModel.getBc4() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE4_CD", "C_SOURCE4", rulesModel.getRuleName());
        } else if(rulesModel.getBc4() != null && oldModel.getBc4() != null && 
                !rulesModel.getBc4().equals(oldModel.getBc4())) {
            rulesMappingDao.updateMappingNull("C_SOURCE4_CD", "C_SOURCE4", rulesModel.getRuleName());
        }
        
        if(rulesModel.getBc5() == null && oldModel.getBc5() != null) {
            rulesMappingDao.updateMappingNull("C_SOURCE5_CD", "C_SOURCE5", rulesModel.getRuleName());
        } else if(rulesModel.getBc5() != null && oldModel.getBc5() != null && 
                !rulesModel.getBc5().equals(oldModel.getBc5())) {
            rulesMappingDao.updateMappingNull("C_SOURCE5_CD", "C_SOURCE5", rulesModel.getRuleName());
        }
                
        securityDao.insertOperAudit(audseq, "Modify rule", (String) session.getAttribute("userid"),
                "success", "Rule '" + rulesModel.getRuleName() + "' has been modified");
        return rulesFrameworkDao.modifyRule(rulesModel, audseq);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public Integer copyRule(RulesFrameworkModel model, String oldName, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Add rule", (String) session.getAttribute("userid"),
                "success", "New rule '" + model.getRuleName() + "' has been added");
        rulesFrameworkDao.copyRuleMaster(model, oldName, audseq);
        rulesMappingDao.copyMapping(model.getRuleName(), oldName, audseq);
        return 1;
    }

    @Override
    public Map<String, String> BandCondition(RulesFrameworkModel model) {
        Map<String, String> bandConditionList = new HashMap<String, String>();
        bandConditionList.put("bc1", bcType(model.getBc1()));
        bandConditionList.put("bc2", bcType(model.getBc2()));
        bandConditionList.put("bc3", bcType(model.getBc3()));
        bandConditionList.put("bc4", bcType(model.getBc4()));
        bandConditionList.put("bc5", bcType(model.getBc5()));
        return bandConditionList;
    }

    private String bcType(String bcCondition) {
        String value = "";
        if (bcCondition != null && bcCondition.equals("bw")) {
            value = "Between";
        } else if (bcCondition != null && bcCondition.equals("nbw")) {
            value = "Not Between";
        } else if (bcCondition != null && bcCondition.equals("cn")) {
            value = "Contains";
        } else if (bcCondition != null && bcCondition.equals("ncn")) {
            value = "Does Not Contain";
        } else if (bcCondition != null && bcCondition.equals("eq")) {
            value = "Equal To";
        } else if (bcCondition != null && bcCondition.equals("neq")) {
            value = "Not Equal To";
        } else if (bcCondition != null && bcCondition.equals("neq")) {
            value = "Not Equal To";
        } else if (bcCondition != null && bcCondition.equals("gt")) {
            value = "Greater Than";
        } else if (bcCondition != null && bcCondition.equals("gte")) {
            value = "Greater Than Equal To";
        } else if (bcCondition != null && bcCondition.equals("lt")) {
            value = "Less Than";
        } else if (bcCondition != null && bcCondition.equals("lte")) {
            value = "Less Than Equal To";
        }
        return value;
    }

    @Override
    public GridPage<RulesFrameworkModel> listRules(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type) {
        return rulesFrameworkDao.listRules(page, max, sidx, sord, searchField, searchOper, searchString, type);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteRule(String ruleName, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        rulesMappingDao.deleteRuleMapping(ruleName);
        rulesFrameworkDao.deleteRule(ruleName);
        String audseq = securityDao.getAuditSequence();
        securityDao.insertOperAudit(audseq, "Delete rule", (String) session.getAttribute("userid"),
                "success", "Rule '" + ruleName + "' has been deleted");
    }

    @Override
    public RulesFrameworkModel getRule(String ruleName) {
        return rulesFrameworkDao.getRule(ruleName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String commitRule(String ruleName, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String ret = rulesFrameworkDao.commitRule(ruleName);
        if (ret.equals("Success")) {
            String audseq = securityDao.getAuditSequence();
            securityDao.insertOperAudit(audseq, "Commit Rule", (String) session.getAttribute("userid"),
                    "success", "Rule '" + ruleName + "' modified");
            rulesFrameworkDao.updateCommitDate(ruleName, audseq);
            return msgSrc.getMessage("func.success", null, ret, null);
        } else {
            return msgSrc.getMessage("func.failure", null, ret, null);
        }
    }    
}