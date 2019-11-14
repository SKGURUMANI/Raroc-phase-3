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

import atrix.common.util.GridPage;
import atrix.st.model.RulesFrameworkModel;
import atrix.st.model.RulesOptionModel;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface RulesFrameworkService {

    public List<RulesOptionModel> listTables(String option);

    public List<RulesOptionModel> listColumns(String table);

    public List<RulesOptionModel> listPkColumns(String table);
    
    public List<RulesOptionModel> listCondition(String tab, String col);

    public String validateRuleData(RulesFrameworkModel model);
    
    public String checkRuleName(RulesFrameworkModel model);

    public Integer addRule(RulesFrameworkModel rulesModel, HttpServletRequest request);
    
    public Integer modifyRule(RulesFrameworkModel rulesModel, HttpServletRequest request);
    
    public Integer copyRule(RulesFrameworkModel model, String oldName, HttpServletRequest request);

    public Map<String, String> BandCondition(RulesFrameworkModel model);

    public GridPage<RulesFrameworkModel> listRules(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type);

    public void deleteRule(String ruleName, HttpServletRequest request);

    public RulesFrameworkModel getRule(String ruleName);

    public String commitRule(String ruleName, HttpServletRequest request);    
}