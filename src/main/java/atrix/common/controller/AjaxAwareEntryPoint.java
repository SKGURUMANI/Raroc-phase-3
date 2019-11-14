/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 *
 * @author vaio
 */
public class AjaxAwareEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public AjaxAwareEntryPoint(String loginUrl) {
        super(loginUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        if (isAjaxRequest(request)) {
            response.setStatus(619);
            PrintWriter out = response.getWriter();
            out.println("SESSION_TIMED_OUT");
            out.flush();
        } else {
            request.setAttribute("targetUrl", request.getRequestURL());
            super.commence(request, response, authException);
        }
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}