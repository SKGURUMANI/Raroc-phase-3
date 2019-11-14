/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

/**
 *
 * @author vaio
 */
import atrix.common.service.CSRFTokenService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.lang3.StringUtils;

/**
 * Creates a hidden input field with the CSRF Token
 *
 * @author michael.simons, 2011-09-20
 */
public class CSRFTokenTag extends TagSupport {

    private static final long serialVersionUID = 745177955805541350L;
    private boolean plainToken = false;

    @Override
    public int doStartTag() throws JspException {
        final CSRFTokenService csrfTokenService = HelperRegistry.getHelper(super.pageContext.getServletContext(), super.pageContext.getRequest(), CSRFTokenService.class, "csrfTokenService");
        final String token = csrfTokenService.getTokenFromSession((HttpServletRequest) super.pageContext.getRequest());
        if (!StringUtils.isBlank(token)) {
            try {
                if (plainToken) {
                    pageContext.getOut().write(token);
                } else {
                    pageContext.getOut().write(String.format("<input type=\"hidden\" name=\"%1$s\" id=\"%1$s\" value=\"%2$s\" />", CSRFTokenService.TOKEN_PARAMETER_NAME, token));
                }
            } catch (IOException e) {
            }
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public boolean isPlainToken() {
        return plainToken;
    }

    public void setPlainToken(boolean plainToken) {
        this.plainToken = plainToken;
    }

    public static String getTokenParameterName() {
        return CSRFTokenService.TOKEN_PARAMETER_NAME;
    }
}