/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.dao;

import atrix.common.model.ChangePassModel;
import atrix.common.model.UserModel;
import atrix.common.util.GridPage;
import java.util.List;

/**
 *
 * @author vaio
 */
public interface UserDao {
    
    public ChangePassModel getPasswords(String userid);
    
    public int changePassword(String userid, String newPassword);
    
    public List getHomePages();    
    
    public List getLocales();
    
    public UserModel getCurrentSettings(String user);
    
    public int SaveSettings(UserModel usr, String userid);
    
    public int checkUsrIDAvailability(String userid);
    
    public void CreateUser(UserModel usr);
    
    public void ModifyUser(UserModel usr);
    
    public List<String> getUserRoles(String userid);    
    
    public void CreateUserRole(String userid, String role);
    
    public void DeleteUserRole(String userid, String role);
    
    public void CreateUserManager(String userid, String manager);
    
    public void DeleteUserManager(String userid, String manager);
    
    public GridPage<UserModel> listUsers(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public UserModel getUserDetails(String userid);        
}
