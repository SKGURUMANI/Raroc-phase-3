/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.controller;

import atrix.common.dao.SecurityDao;
import atrix.common.service.SecurityContextAccessor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author vaio
 */
@Controller
public class LoginController {
    
    @Autowired
    SecurityContextAccessor securityContextAccessor;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SecurityDao securityDao;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {        
        if (securityContextAccessor.isCurrentAuthenticationAnonymous()) {
            return "common/login";
        } else {
            return "common/redirect";
        }        
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(Map<String, Object> map, HttpServletRequest request) {                
        if (securityContextAccessor.isCurrentAuthenticationAnonymous()) {
            map.put("logout", messageSource.getMessage("logout", null, "Logged out", null));
            return "common/login";
        } else {
            return "common/redirect";
        }        
    }

    @RequestMapping(value = "/login/sessionExpired", method = RequestMethod.GET)
    public String failedLogin(Map<String, Object> map) {
        map.put("error", messageSource.getMessage("login.sessionExpired", null, "Session Expired", null));
        return "common/login";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome() {
        return "common/redirect";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "common/index";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Map<String, Object> map, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        securityDao.insertSysAudit("Forbidden", (String) session.getAttribute("userid"), (String) session.getAttribute("usersi"),
                (String) session.getAttribute("userip"), "User tried to access a protected page");
        map.put("msg", messageSource.getMessage("accessDenied", null, "Access Denied", null));
        return "common/accessDenied";
    }
}