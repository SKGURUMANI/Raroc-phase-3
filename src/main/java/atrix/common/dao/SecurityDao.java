/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.dao;

import atrix.common.model.*;
import atrix.common.util.GridPage;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author vaio
 */
public interface SecurityDao {

    public SecurityModel getUserByUsername(String userid);

    public List<GrantedAuthority> getAuthorityByUsernameJDBC(String userid);

    public List<GrantedAuthority> getAuthorityByUsernameLDAP(String userid);

    public void reportFailedLogin(String username);

    public SecurityModel getPreferences(String username);

    public void reportLoginSuccess(String username);

    public void insertSysAudit(String action, String userid, String sessionid, String ip, String remarks);

    public GridPage<UserModel> listSysLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate);

    public List<UserModel> docSysLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString);

    public String getAuditSequence();
    
    public void insertOperAudit(String audcd, String type, String userid, String status, String desc);
    
    public GridPage<TaskMonitorModel> listOpsLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate);
    
    public List<TaskMonitorModel> docOpsLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString);
    
    public GridPage<TaskMonitorModel> listPrsLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);

    public GridPage<DefragModel> listDefragTables(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);

    public List<OptionsModel> listSchemaNames();

    public List<OptionsModel> listTableNames(String schema);

    public void postDefrag(DefragModel model);

    public void deleteDefrag(String id);

    public void callDefragProc();
    
    public void updateAppVersion();

}