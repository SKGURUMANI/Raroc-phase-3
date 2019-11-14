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
package atrix.common.view.admin;

import atrix.common.model.UserModel;
import atrix.common.util.AbstractExcelXView;
import atrix.common.util.PoiStyles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vaio
 */
public class XLSSysLog extends AbstractExcelXView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, XSSFWorkbook wb,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        CellStyle bStyle;
        PoiStyles pois = new PoiStyles();
        bStyle = pois.boldStyle(wb);

        response.setHeader("Content-Disposition", "attachment; filename=\"SysLog.xlsx\"");
        Sheet sheet = wb.createSheet("LOG");
        org.apache.poi.ss.usermodel.Row hrow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Row data;
        org.apache.poi.ss.usermodel.Cell hcell, dcell;

        hcell = hrow.createCell(0);
        hcell.setCellValue(getMessageSourceAccessor().getMessage("label.userid"));
        hcell.setCellStyle(bStyle);

        hcell = hrow.createCell(1);
        hcell.setCellValue(getMessageSourceAccessor().getMessage("label.userName"));
        hcell.setCellStyle(bStyle);

        hcell = hrow.createCell(2);
        hcell.setCellValue(getMessageSourceAccessor().getMessage("label.action"));
        hcell.setCellStyle(bStyle);

        hcell = hrow.createCell(3);
        hcell.setCellValue(getMessageSourceAccessor().getMessage("label.result"));
        hcell.setCellStyle(bStyle);

        hcell = hrow.createCell(4);
        hcell.setCellValue(getMessageSourceAccessor().getMessage("label.ipAddress"));
        hcell.setCellStyle(bStyle);

        hcell = hrow.createCell(5);
        hcell.setCellValue(getMessageSourceAccessor().getMessage("label.time"));
        hcell.setCellStyle(bStyle);

        ArrayList list = (ArrayList) model.get("list");
        Iterator itr1 = list.iterator();
        Integer rowno = 1;
        while (itr1.hasNext()) {
            UserModel object = (UserModel) itr1.next();
            data = sheet.createRow(rowno);

            dcell = data.createCell(0);
            dcell.setCellValue(object.getUserId());

            dcell = data.createCell(1);
            dcell.setCellValue((object.getUserName()));

            dcell = data.createCell(2);
            dcell.setCellValue((object.getRoles()));

            dcell = data.createCell(3);
            dcell.setCellValue((object.getAddress()));

            dcell = data.createCell(4);
            dcell.setCellValue(object.getActiveStr());

            dcell = data.createCell(5);
            dcell.setCellValue((object.getSessionTime()));
            
            rowno++;
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);        
    }
}