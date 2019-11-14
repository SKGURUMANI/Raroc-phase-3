/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.controller;

import atrix.common.service.CSRFTokenService;
import atrix.common.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Amolraj
 */
@RequestMapping(value = "/SSO")
@Controller
public class SsoController {

    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private UserService userService;
    @Autowired
    private CSRFTokenService csrfTokenService;
    @Autowired
    private MessageSource messageSource;
    private static final Logger logger = Logger.getLogger(AdminController.class);
    

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage() {
        return "common/index";
    }
}
