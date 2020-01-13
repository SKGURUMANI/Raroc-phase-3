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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.servlet.LocaleResolver;

/**
 *
 * @author vaio
 */
public class AuthSuccessHandlerJDBC extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private SecurityDao securityDao;
    @Autowired
    private MastersDao mastersDao;

    @Resource
    private LocaleResolver localeResolver;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        System.out.println("I am here in success handler");
        HttpSession session = request.getSession(false);
        String userid = (String) authentication.getName();
        String password = (String) authentication.getCredentials();
        String sessionid = ((WebAuthenticationDetails) authentication.getDetails()).getSessionId();
        String userip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        session.setAttribute("userid", userid);
        session.setAttribute("usersi", sessionid);
        session.setAttribute("userip", userip);
        session.setAttribute("pass", password);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean firstLogin = authorities.contains(new SimpleGrantedAuthority("ROLE_PASSWORD_CHANGE"));
        SecurityModel model = securityDao.getPreferences(userid);
        String roles= model.getRole();
        List<String> roleslist = Arrays.asList(roles.split(","));
        String role=securityDao.rolerights(roleslist);
        if (firstLogin) {
            session.setAttribute("homepage", "forceChangePass");
            session.setAttribute("userroles", model.getRole());
            session.setAttribute("role",role);
            securityDao.insertSysAudit("Login", userid, sessionid, userip, "First login by the user");
        } else {
            session.setAttribute("username", model.getUsername());
            session.setAttribute("homepage", model.getHomepage());
            session.setAttribute("lastLogin", model.getLastLogin());
            session.setAttribute("unit", model.getUnit());
            session.setAttribute("userroles", model.getRole());
            session.setAttribute("role",role);
            securityDao.insertSysAudit("Login", userid, sessionid, userip, "Success");
        }
        session.setMaxInactiveInterval(model.getSessionTime());
        localeResolver.setLocale(request, response, new Locale(model.getLocale()));
        MastersModel cModel = mastersDao.getCurrLocalization();
        session.setAttribute("prefix", cModel.getCol2());
        securityDao.reportLoginSuccess(userid);
        response.sendRedirect("welcome");
    }
}
