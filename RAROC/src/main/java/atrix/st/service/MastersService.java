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
package atrix.st.service;

import atrix.common.util.GridPage;
import atrix.st.model.MastersModel;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public interface MastersService {    

    public GridPage<MastersModel> masterGrid(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String table);

    public void putMaster(MastersModel model, String table, HttpServletRequest request);

    public void postMaster(MastersModel model, String table, HttpServletRequest request);

    public void deleteMaster(String pk, String table, HttpServletRequest request);    
}