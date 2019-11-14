/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.util.GridPage;
import atrix.st.model.RulesFrameworkModel;
import atrix.st.model.RulesOptionModel;
import java.util.List;

/**
 *
 * @author vaio
 */
public interface RulesFrameworkDao {

    public List<RulesOptionModel> listTables(String option);

    public List<RulesOptionModel> listColumns(String table);

    public List<RulesOptionModel> listPkColumns(String table);
    
    public String listConditions(String tab, String col);

    public Integer checkRuleName(String ruleName);

    public Integer addRule(RulesFrameworkModel rulesModel, String audCd);
    
    public Integer modifyRule(RulesFrameworkModel rulesModel, String audCd);
    
    public void copyRuleMaster(RulesFrameworkModel model, String ruleName, String audCd);

    public GridPage<RulesFrameworkModel> listRules(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String type);

    public RulesFrameworkModel getRule(String ruleName);

    public void deleteRule(String rulename);

    public String commitRule(String rulename);

    public void updateCommitDate(String rulename, String audseq);    
}