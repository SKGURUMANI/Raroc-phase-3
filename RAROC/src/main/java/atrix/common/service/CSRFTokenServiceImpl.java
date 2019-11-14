/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

/**
 *
 * @author vaio
 */
import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service("csrfTokenService")
public class CSRFTokenServiceImpl implements CSRFTokenService {

    private final SecureRandom random = new SecureRandom();        

    @Override
    public String generateToken() {
        final byte[] bytes = new byte[32];
        random.nextBytes(bytes);        
        //return Base64.encodeBase64URLSafeString(bytes);
        return null;
    }

    @Override
    public String getTokenFromSession(final HttpServletRequest request) {
        return request.getUserPrincipal() == null ? null : this.getTokenFromSessionImpl(request.getSession(false));
    }    

    private String getTokenFromSessionImpl(final HttpSession session) {
        String token = null;        
        if (session != null) {
            token = (String) session.getAttribute(TOKEN_ATTRIBUTE_NAME);
            if (StringUtils.isBlank(token)) {
                session.setAttribute(TOKEN_ATTRIBUTE_NAME, (token = generateToken()));
            }
        }
        return token;
    }

    @Override
    public boolean acceptsTokenIn(HttpServletRequest request) {
        boolean rv;
        String token;        
        if(request.getContentType() != null && request.getContentType().contains("application/json")) {
            token = request.getHeader(TOKEN_PARAMETER_NAME);            
        } else {
            token = request.getParameter(TOKEN_PARAMETER_NAME);
        }        
                
        // URI exportChart to be used when using ExportChartController
        // String charturl = request.getContextPath()+"/exportChart";        
        // if (request.getUserPrincipal() == null || request.getRequestURI().equals(charturl)) {
        if (request.getUserPrincipal() == null) {
            rv = true;
        } else {
            final HttpSession session = request.getSession(false);
            rv = session != null && this.getTokenFromSessionImpl(session).equals(token);
        }
        return rv;
    }    
    
    @Override
    public void removeTokenFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);        
        if (session != null) {
            String token = (String) session.getAttribute(TOKEN_ATTRIBUTE_NAME);
            if (StringUtils.isNotBlank(token)) {
                session.removeAttribute(TOKEN_ATTRIBUTE_NAME);
            }
        }
    }
    
}