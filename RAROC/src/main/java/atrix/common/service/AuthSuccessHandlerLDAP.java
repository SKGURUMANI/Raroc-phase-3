/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.dao.SecurityDao;
import atrix.common.model.SecurityModel;
import atrix.st.dao.MastersDao;
import atrix.st.model.MastersModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.servlet.LocaleResolver;

/**
 *
 * @author vaio
 */
public class AuthSuccessHandlerLDAP extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private SecurityDao securityDao;
    @Autowired
    private MastersDao mastersDao;

    @Resource
    private LocaleResolver localeResolver;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        HttpSession session = request.getSession(false);
        String userid = (String) authentication.getName();  
        String password = (String) authentication.getCredentials();
        String sessionid = ((WebAuthenticationDetails) authentication.getDetails()).getSessionId();
        String userip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        session.setAttribute("userid", userid);
        session.setAttribute("usersi", sessionid);
        session.setAttribute("userip", userip);
        session.setAttribute("pass", password);
        SecurityModel model = securityDao.getPreferences(userid);   
        String roles= model.getRole();
        String role=null;
        List<String> roleslist = Arrays.asList(roles.split(","));
        if (roleslist.contains("ROLE_ADMIN") && roleslist.contains("ROLE_RAROC_AUTH")) {
        	role="AdminAuth";
        }if (roleslist.contains("ROLE_RAROC_AUTH") && !roleslist.contains("ROLE_ADMIN")) {
        	role="Auth";
        }if (roleslist.contains("ROLE_ADMIN") && !roleslist.contains("ROLE_RAROC_AUTH")) {
        	role="Administrator";
        }if (roleslist.contains("ROLE_ADMIN") && roleslist.contains("ROLE_RAROC_CORP")) {
        	role="Corp";
        }if (roleslist.contains("ROLE_RAROC_AUTH") && roleslist.contains("ROLE_RAROC_CORP")) {
        	role="CorpAuth";
        }if (roleslist.contains("ROLE_ADMIN") && roleslist.contains("ROLE_RAROC_AUTH")&&roleslist.contains("ROLE_RAROC_CORP")) {
        	role="AdminCorp";
        }
        session.setAttribute("username", model.getUsername());
        session.setAttribute("homepage", model.getHomepage());           
        session.setAttribute("lastLogin", model.getLastLogin());
        session.setAttribute("role",role);
        session.setAttribute("unit", model.getUnit());
        securityDao.insertSysAudit("Login", userid, sessionid, userip, "Success");                        
        session.setMaxInactiveInterval(model.getSessionTime());
        localeResolver.setLocale(request, response, new Locale(model.getLocale()));
        MastersModel cModel = mastersDao.getCurrLocalization();        
        session.setAttribute("prefix", cModel.getCol2());
        securityDao.reportLoginSuccess(userid);                    
        response.sendRedirect("welcome");
    }
}