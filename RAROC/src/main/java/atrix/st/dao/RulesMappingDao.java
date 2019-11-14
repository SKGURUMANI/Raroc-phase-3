/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.util.DataTablesResponse;
import atrix.common.util.GridPage;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesMappingModel;
import atrix.st.model.RulesOptionModel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vaio
 */
public interface RulesMappingDao {

    public GridPage<RulesMappingModel> listMappings(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String ruleName);

    public void deleteRuleMapping(String rulename);

    public List<RulesOptionModel> getOptions(String tab, String col);

    public void deleteMapping(int id, String ruleName);

    public void addMapping(String ruleName, RulesMappingModel rulesModel);
    
    public void copyMapping(String newName, String ruleName, String audCd);

    public void editMapping(String ruleName, RulesMappingModel rulesModel);

    public Map<String, String> getColumns(String table, String ruleType);

    public DataTablesResponse<RulesFxGridModel> listFx(Integer sEcho, Integer start, Integer length,
            Integer sidx, String sord, String search);

    public RulesFxGridModel getFx(String fxid);

    public void updateFxInMapping(RulesFxGridModel expModel);

    public Map<String, String> getFxFromMapping(String id, String ruleName);
    
    public void updateMappingNull(String cColumn, String sColumn, String ruleName);
}