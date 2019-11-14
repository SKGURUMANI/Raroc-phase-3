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

import atrix.common.util.GridPage;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesFxTreeModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface FormulaService {

    public String checkFxName(String fxName);

    public GridPage<RulesFxGridModel> listFx(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type);

    public List<RulesFxTreeModel> listRulesTable();

    public List<RulesFxTreeModel> listRulesColumns(String key);

    public List<RulesFxTreeModel> listFxType();

    public List<RulesFxTreeModel> listFxName(String key);

    public List<RulesFxTreeModel> listOperatorType();

    public List<RulesFxTreeModel> listOperatorName(String key);

    public String getFxWarning(String fxId);

    public void insertFx(RulesFxGridModel expModel, HttpServletRequest request);

    public RulesFxGridModel getFormula(String fxid);

    public void deleteFx(String formulaId, HttpServletRequest request);

    public void updateFx(RulesFxGridModel expModel, HttpServletRequest request);
}
