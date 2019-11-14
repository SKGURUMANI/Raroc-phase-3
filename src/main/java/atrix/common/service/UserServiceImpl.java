/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.dao.SecurityDao;
import atrix.common.dao.UserDao;
import atrix.common.model.*;
import atrix.common.util.GridPage;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vaio
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private SecurityDao securityDao;

    @Override
    public String forceChangePass(ChangePassModel changePass, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String rOpassword = changePass.getOpassword();
        String rNpassword = changePass.getNpassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        changePass = userDao.getPasswords(userid);

        if (userid.equals("rNpassword")) {
            return "password.n.username";
        } else if (!passwordEncoder.matches(rOpassword, changePass.getOpassword1())) {
            return "password.o.incorrect";
        } else if (passwordEncoder.matches(rNpassword, changePass.getOpassword1())
                || passwordEncoder.matches(rNpassword, changePass.getOpassword2())
                || passwordEncoder.matches(rNpassword, changePass.getOpassword3())) {
            return "password.o.last3";
        } else {
            String eNpassword = passwordEncoder.encode(rNpassword);
            int result = userDao.changePassword(userid, eNpassword);
            if (result == 1) {
                if (session != null) {
                    session.invalidate();
                }
                SecurityContextHolder.clearContext();
                return "success";
            } else {
                return "internalError";
            }
        }
    }

    @Override
    public String changePassword(ChangePassModel changePass, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        String rOpassword = changePass.getOpassword();
        String rNpassword = changePass.getNpassword();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        changePass = userDao.getPasswords(userid);

        if (userid.equals("rNpassword")) {
            return "password.n.username";
        } else if (!passwordEncoder.matches(rOpassword, changePass.getOpassword1())) {
            return "password.o.incorrect";
        } else if (passwordEncoder.matches(rNpassword, changePass.getOpassword1())
                || passwordEncoder.matches(rNpassword, changePass.getOpassword2())
                || passwordEncoder.matches(rNpassword, changePass.getOpassword3())) {
            return "password.o.last3";
        } else {
            String eNpassword = passwordEncoder.encode(rNpassword);
            int result = userDao.changePassword(userid, eNpassword);
            if (result == 1) {
                return "success";
            } else {
                return "internalError";
            }
        }
    }

    @Override
    public List getHomePages() {
        return userDao.getHomePages();
    }

    @Override
    public List getLocales() {
        return userDao.getLocales();
    }

    @Override
    public UserModel getCurrentSettings(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return userDao.getCurrentSettings((String) session.getAttribute("userid"));
    }

    @Override
    public String saveSettings(UserModel user, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        int ret = userDao.SaveSettings(user, (String) session.getAttribute("userid"));
        if (ret == 1) {
            return "success";
        } else {
            return "internalError";
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public String CreateUser(UserModel usr, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usr.setPassword(passwordEncoder.encode(usr.getPassword()));
        int i, check = userDao.checkUsrIDAvailability(usr.getUserId());
        if (check == 0) {
            userDao.CreateUser(usr);
            String[] roles = usr.getRoles().split(",");
            for (i = 0; i < roles.length; i++) {
                userDao.CreateUserRole(usr.getUserId(), roles[i]);
            }
            /*
            String[] managers = usr.getManager().split(",");
            for (i = 0; i < managers.length; i++) {
                userDao.CreateUserManager(usr.getUserId(), managers[i]);
            }*/
            securityDao.insertSysAudit("Create User " + usr.getUserId(), (String) session.getAttribute("userid"),
                    (String) session.getAttribute("usersi"), (String) session.getAttribute("userip"), "Success");
            return "success";
        } else {
            return "duplicate";
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public String ModifyUser(UserModel usr, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        userDao.ModifyUser(usr);                
        String[] roles = usr.getRoles().split(",");
        List<String> formRoles = Arrays.asList(roles);
        List<String> dbRoles = userDao.getUserRoles(usr.getUserId());
        for (String role : formRoles) {
           if(!dbRoles.contains(role)) {
             userDao.CreateUserRole(usr.getUserId(), role);  
           }            
        }             
        for (String role : dbRoles) {
            if(!formRoles.contains(role)) {
                userDao.DeleteUserRole(usr.getUserId(), role);
            }
        }      
        /*
        String[] managers = usr.getManager().split(",");
        int i;
        for (i = 0; i < managers.length; i++) {
            userDao.DeleteUserManager(usr.getUserId(), managers[i]);
            userDao.CreateUserManager(usr.getUserId(), managers[i]);
        }
        */
        securityDao.insertSysAudit("Modified User " + usr.getUserId(), (String) session.getAttribute("userid"),
                    (String) session.getAttribute("usersi"), (String) session.getAttribute("userip"), "Success");
        return "success";        
    }

    @Override
    public GridPage<UserModel> listUsers(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        return userDao.listUsers(page, max, sidx, sord, searchField, searchOper, searchString);
    }

    @Override
    public UserModel getUserDetails(String userid) {
        return userDao.getUserDetails(userid);
    }
    
    @Override
    public List getUserRoles(String userid) {
        return userDao.getUserRoles(userid);
    }
    
    @Override
    public GridPage<UserModel> listSysLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate) {
        return securityDao.listSysLog(page, max, sidx, sord, searchField, searchOper, searchString, 
                fromDate, toDate);
    }
    
    @Override
    public List<UserModel> docSysLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString) {
        return securityDao.docSysLog(fromDate, toDate, searchField, searchOper, searchString);
    }

    @Override
    public GridPage<TaskMonitorModel> listOpsLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate) {
        return securityDao.listOpsLog(page, max, sidx, sord, searchField, searchOper, searchString, fromDate, toDate);
    }
    
    @Override
    public List<TaskMonitorModel> docOpsLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString) {
        return securityDao.docOpsLog(fromDate, toDate, searchField, searchOper, searchString);
    }
    
    @Override
    public GridPage<TaskMonitorModel> listPrsLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        return securityDao.listPrsLog(page, max, sidx, sord, searchField, searchOper, searchString);
    }
    
    @Override
    public GridPage<DefragModel> listDefragTables(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        return securityDao.listDefragTables(page, max, sidx, sord, searchField, searchOper, searchString);
    }
        
    @Override
    public List<OptionsModel> listSchemaNames() {
        return securityDao.listSchemaNames();
    }
        
    @Override
    public List<OptionsModel> listTableNames(String schema) {
        return securityDao.listTableNames(schema);
    }

    @Override
    public void postDefrag(DefragModel model) {
        securityDao.postDefrag(model);
    }

    @Override
    public void deleteDefrag(String id) {
        securityDao.deleteDefrag(id);
    }
    
    @Async
    @Override
    public void callDefragProc() {
        securityDao.callDefragProc();
    }
}