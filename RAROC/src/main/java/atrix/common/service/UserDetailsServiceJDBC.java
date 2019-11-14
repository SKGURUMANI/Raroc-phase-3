/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.dao.SecurityDao;
import atrix.common.model.SecurityModel;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author vaio
 */
public class UserDetailsServiceJDBC implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserDetailsServiceJDBC.class);
    @Autowired
    private SecurityDao securityDao;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        SecurityModel sec = securityDao.getUserByUsername(userid);        
        if (sec != null) {            
            if (sec.isEnabled() == false) {
                logger.error("User not enabled");
            }
            if (sec.isActive() == false) {
                logger.error("User is Locked");
            }
            if (sec.isPassExpired() == false) {
                logger.error("Password Expired");
            }
            List<GrantedAuthority> AUTHORITIES = securityDao.getAuthorityByUsernameJDBC(userid);
            if (AUTHORITIES == null) {
                AUTHORITIES = AuthorityUtils.NO_AUTHORITIES;
            }
            /* User(String username, String password, boolean enabled, boolean accountNonExpired, 
             * boolean credentialsNonExpired, boolean accountNonLocked, 
             * Collection<? extends GrantedAuthority> authorities)            
             */
            User user = new User(sec.getUsername(), sec.getPassword(), sec.isEnabled(), true, sec.isPassExpired(),
                        sec.isActive(), AUTHORITIES);
            return user;
        } else {
            logger.error("User Not Found");
            throw new UsernameNotFoundException("User " + userid + " has no granted authority");
        }                        
    }

}