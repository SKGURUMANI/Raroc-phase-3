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
package atrix.common.util;

import atrix.common.model.DataTypeModel;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vaio
 */
@XmlRootElement
public class GridWithColType<T> {

    private List<T> rows;
    private DataTypeModel type;
    private int page;
    private int max;
    private int total;

    public GridWithColType() {
    }

    public GridWithColType(List<T> rows, DataTypeModel type, int page, int max, int total) {
        this.rows = rows;
        this.type = type;
        this.page = page;
        this.max = max;
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public DataTypeModel getType() {
        return type;
    }

    public void setType(DataTypeModel type) {
        this.type = type;
    }
    
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}