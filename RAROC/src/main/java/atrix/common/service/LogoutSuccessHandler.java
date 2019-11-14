/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.dao.SecurityDao;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 *
 * @author vaio
 */
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Autowired
    private SecurityDao securityDao;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        try {
            String userid = (String) authentication.getName();
            String sessionid = ((WebAuthenticationDetails) authentication.getDetails()).getSessionId();
            String userip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
            securityDao.insertSysAudit("Logout", userid, sessionid, userip, "Success");
            response.sendRedirect("logout");            
        } catch (Exception ex) {            
            Logger.getLogger(LogoutSuccessHandler.class.getName()).log(Level.SEVERE, null, ex);            
            response.sendRedirect("logout");
        }
    }
}