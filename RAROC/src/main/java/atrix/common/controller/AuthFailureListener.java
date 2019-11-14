/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.controller;

import atrix.common.dao.SecurityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 *
 * @author vaio
 */
@Component
public class AuthFailureListener
        implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    @Autowired
    private SecurityDao securityDao;

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent ev) {
        String userid = ev.getAuthentication().getName();
        String userip = ((WebAuthenticationDetails) ev.getAuthentication().getDetails()).getRemoteAddress();
        securityDao.reportFailedLogin(userid);
        securityDao.insertSysAudit("Login", userid, null, userip, ev.getException().getMessage());
    }
}