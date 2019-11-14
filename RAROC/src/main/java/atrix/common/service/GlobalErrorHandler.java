/*
 * © 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 * 
 * Asymmetrix Solutions Private Limited
 * 115, Bldg 2, Sector 3, Millennium Business Park,
 * Navi Mumbai, India, 410701
 */
package atrix.common.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 *
 * @author vaio
 */
public class GlobalErrorHandler extends SimpleMappingExceptionResolver {

    private static final Logger log = Logger.getLogger(GlobalErrorHandler.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        log.error(ex);
        System.out.println("I M HERE");
        response.setStatus(500);        
        return super.doResolveException(request, response, handler, ex);        
    }
}