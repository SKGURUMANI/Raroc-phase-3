/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import org.springframework.security.core.AuthenticationException;

/**
 * <p>
 * Thrown as a translation of an {@link javax.naming.AuthenticationException}
 * when attempting to authenticate against Active Directory using
 * {@link ActiveDirectoryLdapAuthenticationProvider}. Typically this error is
 * wrapped by an {@link AuthenticationException} since it does not provide a
 * user friendly message. When wrapped, the original Exception can be caught and
 * {@link ActiveDirectoryAuthenticationException} can be accessed using
 * {@link AuthenticationException#getCause()} for custom error handling.
 * </p>
 * <p>
 * The {@link #getDataCode()} will return the error code associated with the
 * data portion of the error message. For example, the following error message
 * would return 773 for {@link #getDataCode()}.
 * </p>
 *
 * <pre>
 * javax.naming.AuthenticationException: [LDAP: error code 49 - 80090308: LdapErr: DSID-0C090334, comment: AcceptSecurityContext error, data 775, vece ]
 * </pre>
 *
 * @author Rob Winch
 */
@SuppressWarnings("serial")
public final class ADAuthenticationException extends AuthenticationException {

    private final String dataCode;

    ADAuthenticationException(String dataCode, String message,
            Throwable cause) {
        super(message, cause);
        this.dataCode = dataCode;
    }

    public String getDataCode() {
        return dataCode;
    }
}
