/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.model.*;
import atrix.common.util.GridPage;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface UserService {

    public String forceChangePass(ChangePassModel changePass, HttpServletRequest request);

    public String changePassword(ChangePassModel changePass, HttpServletRequest request);

    public List getHomePages();

    public List getLocales();

    public UserModel getCurrentSettings(HttpServletRequest request);

    public String saveSettings(UserModel user, HttpServletRequest request);

    public String CreateUser(UserModel usr, HttpServletRequest request);

    public String ModifyUser(UserModel usr, HttpServletRequest request);

    public GridPage<UserModel> listUsers(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);

    public UserModel getUserDetails(String userid);

    public List getUserRoles(String userid);

    public GridPage<UserModel> listSysLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate);

    public List<UserModel> docSysLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString);

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
}