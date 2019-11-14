/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.dao.SecurityDao;
import atrix.common.model.SecurityModel;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 *
 * @author vaio
 */
public class UserDetailsServiceLDAP implements UserDetailsContextMapper, Serializable {

    private static final long serialVersionUID = 3962976258168853954L;
    private static final Logger logger = Logger.getLogger(UserDetailsServiceLDAP.class);
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SecurityDao securityDao;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String userid,
            Collection<? extends GrantedAuthority> authority) throws UsernameNotFoundException {

        SecurityModel sec = securityDao.getUserByUsername(userid);
        if (sec != null) {
            if (sec.isEnabled() == false) {
                throw new UsernameNotFoundException("Account is diabled");
            }
            if (sec.isActive() == false) {
                throw new UsernameNotFoundException("Account is marked as inactive");
            }
            List<GrantedAuthority> AUTHORITIES = securityDao.getAuthorityByUsernameLDAP(userid);
            if (AUTHORITIES == null) {
                AUTHORITIES = AuthorityUtils.NO_AUTHORITIES;
            }
            /*
             * User(String username, String password, boolean enabled, boolean
             * accountNonExpired, boolean credentialsNonExpired, boolean
             * accountNonLocked, Collection<? extends GrantedAuthority>
             * authorities)
             */
            User user = new User(sec.getUsername(), "", sec.isEnabled(), true, true, sec.isActive(), AUTHORITIES);
            return user;
        } else {
            logger.error("User Not Found");
            throw new UsernameNotFoundException(messageSource.getMessage("LdapAuthenticationProvider.noAppAccess",
                    new Object[]{userid}, "No access rights", null));
        }
    }

    @Override
    public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
    }
}