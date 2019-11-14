/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.controller;

import atrix.common.model.ChangePassModel;
import atrix.common.model.UserModel;
import atrix.common.service.CSRFTokenService;
import atrix.common.service.UserService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author vaio
 */
@Controller
public class UserController {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private CSRFTokenService csrfTokenService;
    private static final Logger logger = Logger.getLogger(UserController.class);
    @Value("${auth.type}")
    private String authType;
    @Autowired
    private ServletContext servletContext;

    @RequestMapping(value = "/forceChangePass", method = RequestMethod.GET)
    public String forceChangePass(Model model) {
        model.addAttribute("changePassModel", new ChangePassModel());
        return "preferences/forceChangePass";
    }

    @RequestMapping(value = "/forceChangePass", method = RequestMethod.POST)
    public String forceChangePassPost(@Valid ChangePassModel changePassModel, BindingResult result,
            Map<String, Object> map, HttpServletRequest request) {
        if (result.hasErrors()) {
            map.put("error", null);
            return "preferences/forceChangePass";
        } else {
            String status = userService.forceChangePass(changePassModel, request);
            String ret;
            if (status.equals("success")) {
                map.put("logout", messageSource.getMessage("password.changed", null, "Success", null));
                ret = "common/login";
            } else {
                map.put("error", messageSource.getMessage(status, null, "Bad Attempt", null));
                ret = "preferences/forceChangePass";
            }
            csrfTokenService.removeTokenFromSession(request);
            logger.info("Token removed from session");
            return ret;
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String preferencePage(Map<String, String> map) {
        map.put("authType", authType);
        return "preferences/preferencesMain";
    }

    @RequestMapping(value = "/user/help", method = RequestMethod.GET)
    public void helpPage(HttpServletRequest request, HttpServletResponse response) {
        File file = new File(servletContext.getRealPath("/WEB-INF/views/common/help.pdf"));
        FileInputStream iStream = null;
        OutputStream oStream = null;
        try {
            response.setHeader("Content-disposition", "inline;filename=Help.pdf");
            response.setContentType("application/pdf");
            iStream = new FileInputStream(file);
            oStream = response.getOutputStream();
            IOUtils.copy(iStream, oStream);
        } catch (IOException ex) {
            logger.fatal(ex);
        } finally {
            IOUtils.closeQuietly(iStream);
            IOUtils.closeQuietly(oStream);
        }
    }

    @RequestMapping(value = "/user/general", method = RequestMethod.GET)
    public String generalSettingsPage(Model model, Map<String, List> map, HttpServletRequest request) {
        UserModel um = userService.getCurrentSettings(request);
        model.addAttribute("general", um);
        map.put("homePage", userService.getHomePages());
        map.put("locale", userService.getLocales());
        return "preferences/generalSettings";
    }

    @RequestMapping(value = "/user/general", method = RequestMethod.POST)
    public @ResponseBody
    String generalSettingsPost(@ModelAttribute UserModel general, BindingResult result,
            Map<String, Object> map, HttpServletRequest request) {
        String ret = userService.saveSettings(general, request);
        if (ret.equals("success")) {
            csrfTokenService.removeTokenFromSession(request);
            ret = messageSource.getMessage("settings.changed", null, "Success", null);
        } else {
            ret = messageSource.getMessage(ret, null, "Failed", null);
        }
        return ret;
    }

    @RequestMapping(value = "/user/changePassword", method = RequestMethod.GET)
    public String changePasswordPage(Model model, Map<String, List> map, HttpServletRequest request) {
        model.addAttribute("changePassModel", new ChangePassModel());
        return "preferences/changePassword";
    }

    @RequestMapping(value = "/user/changePassword", method = RequestMethod.POST)
    public @ResponseBody
    String changePasswordPost(@Valid ChangePassModel changePassModel, BindingResult result,
            Map<String, Object> map, HttpServletRequest request) {
        if (result.hasErrors()) {
            return messageSource.getMessage("form.error", null, "Failed", null);
        } else {
            String status = userService.changePassword(changePassModel, request);
            String ret;
            if (status.equals("success")) {
                ret = messageSource.getMessage("password.ajax.change", null, "success", null);
            } else {
                ret = messageSource.getMessage(status, null, "Bad Attempt", null);
            }
            csrfTokenService.removeTokenFromSession(request);
            logger.info("Token removed from session");
            return ret;
        }
    }
}