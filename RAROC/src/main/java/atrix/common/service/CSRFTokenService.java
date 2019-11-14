/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface CSRFTokenService {

    public final static String TOKEN_PARAMETER_NAME = "_tk";
    public final static String TOKEN_ATTRIBUTE_NAME = "CSRFToken";
    public final static List<String> METHODS_TO_CHECK = Collections.unmodifiableList(Arrays.asList("POST", "PUT", "DELETE"));    

    /**
     * Generates a new CSRF Protection token
     */
    public String generateToken();    
    
    /**
     * Obtains the token from the session. If there is no token, a new one will
     * be generated
     */
    public String getTokenFromSession(final HttpServletRequest request);

    /**
     * This method tests, if a token is acceptable when a user is logged in
     */
    public boolean acceptsTokenIn(HttpServletRequest request);    
    
    /**
     * This method removes the token from the session.
     */
    public void removeTokenFromSession(HttpServletRequest request);        
}