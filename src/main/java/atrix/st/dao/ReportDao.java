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
package atrix.st.dao;

import atrix.common.util.DataTablesResponse;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithColType;
import atrix.st.model.MstExecutionModel;
import atrix.st.model.ReportModel;

/**
 *
 * @author vaio
 */
public interface ReportDao {
    
    public DataTablesResponse<MstExecutionModel> listMstExecution(Integer sEcho, Integer start, Integer length,
            Integer sidx, String sord, String search);

    public String getExecutionName(String execId);
    
    public GridPage<ReportModel> listTests(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String id);
    
    public GridWithColType<ReportModel> listDetails(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String id);
}
