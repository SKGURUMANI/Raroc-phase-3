/* © 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 *
 * Asymmetrix Solutions Private Limited
 * 101, Bldg 2, Sector 3,
 * Millennium Business Park,
 * Navi Mumbai, India, 410701
 */
package atrix.common.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.springframework.stereotype.Service;

/**
 * Formatter used to type cast JAVA objects
 *
 * @author vaio
 */
@Service("fmt")
public class FormatterService {

    public String ToString(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    public Integer ToInteger(Object obj) {
        return (obj == null) ? 0 : ((BigDecimal) obj).intValueExact();
    }

    public Double ToDouble(Object obj) {
        return (obj == null) ? 0.00 : ((BigDecimal) obj).doubleValue();
    }       

    public String ToPercent(Object obj) {
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMaximumFractionDigits(2);
        return (obj == null) ? null : pf.format(((BigDecimal) obj).doubleValue());
    }

    public String ToStringInteger(Object obj) {
        DecimalFormat df = new DecimalFormat("##,##,##0");
        return (obj == null) ? "" : df.format(obj);        
    }
    
    public String ToStringDouble(Object obj) {
        DecimalFormat df = new DecimalFormat("##,##,##0.00");
        return (obj == null) ? "" : df.format(obj);
    }
    
    public String ToStringFour(Object obj) {
        DecimalFormat df = new DecimalFormat("##,##,##0.0000");
        return (obj == null) ? "" : df.format(obj);
    }
    
    public String ToStringPercent(Object obj) {
        DecimalFormat df = new DecimalFormat("##,##,##0.00");
        return (obj == null) ? "" : df.format(obj)+"%";
    }
}