/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author vaio
 */
public class HelperRegistry {
    
    public static <T> T getHelper(final ServletContext servletContext, ServletRequest servletRequest, 
            final Class<T> clazz, final String name) {
		final WebApplicationContext wc = RequestContextUtils.getWebApplicationContext(servletRequest, servletContext);
		T rv = null;
		if(wc != null) {
			if(StringUtils.isBlank(name))
				rv = wc.getBean(clazz);
			else
				rv = wc.getBean(name, clazz);
		}
		return rv;
	}
    
}
