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
package atrix.st.view;

import atrix.common.util.AbstractExcelXView;
import atrix.common.util.GridWithFooterPage;
import atrix.common.util.PoiStyles;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vaio
 */
public class XLSRarocView extends AbstractExcelXView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, XSSFWorkbook wb, 
        HttpServletRequest request, HttpServletResponse response) throws Exception {
        CellStyle bStyle, hcStyle, dcStyle, pStyle, dStyle, rowStyle;
        PoiStyles pois = new PoiStyles();
        bStyle = pois.boldStyle(wb);
        hcStyle = pois.headCenterStyle(wb);
        dcStyle = pois.dataCenterStyle(wb);
        pStyle = pois.percentageCenterStyle(wb);
        dStyle = pois.doubleCenterStyle(wb);
        int rowno = 6, colno;

        RarocMasterModel header = (RarocMasterModel) model.get("form");

        response.setHeader("Content-Disposition", "attachment;filename=RAROC_" + header.getCid() + ".xlsx");
        Sheet sheet = wb.createSheet("RAROC");
        Row titleRow;
        Row hrow;
        Row data;
        Cell hcell, dcell, titleCell;

        titleRow = sheet.createRow(0);
        
        titleCell = titleRow.createCell(0);
        titleCell.setCellValue(new XSSFRichTextString("Calculator version - v4.0"));
        titleCell.setCellStyle(bStyle);        
        
        titleCell = titleRow.createCell(1);
        titleCell.setCellValue(new XSSFRichTextString("Latest version release date - "+header.getVersion()));
        titleCell.setCellStyle(bStyle);
        
        titleRow = sheet.createRow(1);
        
        titleCell = titleRow.createCell(0);
        titleCell.setCellValue(new XSSFRichTextString("Computation Timestamp - "+header.getCreated()));
        titleCell.setCellStyle(bStyle);        
        
        titleCell = titleRow.createCell(1);
        titleCell.setCellValue(new XSSFRichTextString("Ref ID - "+header.getRarocref()));
        titleCell.setCellStyle(bStyle);
        
        titleRow = sheet.createRow(2);
        
        titleCell = titleRow.createCell(0);
        titleCell.setCellValue(new XSSFRichTextString("User Id - "+header.getUsername()));
        titleCell.setCellStyle(bStyle);        
        
        titleCell = titleRow.createCell(1);
        titleCell.setCellValue(new XSSFRichTextString("CIF ID - "+header.getCif()));
        titleCell.setCellStyle(bStyle);
        
        titleRow = sheet.createRow(3);
        
        titleCell = titleRow.createCell(0);
        titleCell.setCellValue(new XSSFRichTextString("Rating Tool Id - "+header.getRid()));
        titleCell.setCellStyle(bStyle);        
        
        titleCell = titleRow.createCell(1);
        titleCell.setCellValue(new XSSFRichTextString("PAN - "+header.getPan()));
        titleCell.setCellStyle(bStyle);
        
        titleRow = sheet.createRow(4);
        
        titleCell = titleRow.createCell(0);
        titleCell.setCellValue(new XSSFRichTextString(""));
        titleCell.setCellStyle(bStyle);        
        
        
        hrow = sheet.createRow(5);
        
        hcell = hrow.createCell(0);
        hcell.setCellValue(new XSSFRichTextString("Parameter"));
        hcell.setCellStyle(bStyle);

        for (colno = 1; colno <= header.getFacility(); colno++) {
            hcell = hrow.createCell(colno);
            hcell.setCellValue(new XSSFRichTextString("Facility " + colno));
            hcell.setCellStyle(hcStyle);
        }        
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("CA"));
        hcell.setCellStyle(hcStyle);
        
        colno++;
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("SA"));
        hcell.setCellStyle(hcStyle);

        colno++;
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("TD"));
        hcell.setCellStyle(hcStyle);
        
        colno++;
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("CMS"));
        hcell.setCellStyle(hcStyle);
        
        colno++;
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("FX"));
        hcell.setCellStyle(hcStyle);
        
        colno++;
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("Others"));
        hcell.setCellStyle(hcStyle);
        
        colno++;
        
        hcell = hrow.createCell(colno);
        hcell.setCellValue(new XSSFRichTextString("Total"));
        hcell.setCellStyle(hcStyle);
        
        GridWithFooterPage<RarocViewModel> grid = (GridWithFooterPage<RarocViewModel>) model.get("grid");
        List<RarocViewModel> list = grid.getRows();
        for (RarocViewModel object : list) {
            data = sheet.createRow(rowno);
            dcell = data.createCell(0);
            dcell.setCellValue(new XSSFRichTextString(object.getOutputName()));
            dcell = data.createCell(1);
            if(object.getId() == 11 || object.getId() == 13 || object.getId() == 14 || object.getId() == 17 || 
               object.getId() == 21 || object.getId() == 25 || object.getId() == 26 || object.getId() == 27 || 
               object.getId() == 28 || object.getId() == 29 || object.getId() == 30 || object.getId() == 33 || 
               object.getId() == 43) {
                rowStyle = pStyle;
            } else if(object.getId() == 8 || object.getId() == 15 || object.getId() == 16 || object.getId() == 31 || 
               object.getId() == 32 || object.getId() == 34 || object.getId() == 35 || object.getId() == 37 || 
               object.getId() == 38 || object.getId() == 39 || object.getId() == 40 || object.getId() == 41 || 
               object.getId() == 42) {
                rowStyle = dStyle;
            } else {
                rowStyle = dcStyle;
            }
            dcell.setCellValue(object.getFacility1());
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(2);
            dcell.setCellValue(object.getFacility2());
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(3);
            dcell.setCellValue(object.getFacility3());
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(4);
            dcell.setCellValue(object.getFacility4());
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(5);
            dcell.setCellValue(object.getFacility5());
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(6);
            dcell.setCellValue(object.getFacility6());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(7);
            dcell.setCellValue(object.getFacility7());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(8);
            dcell.setCellValue(object.getFacility8());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(9);
            dcell.setCellValue(object.getFacility9());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(10);
            dcell.setCellValue(object.getFacility10());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(11);
            dcell.setCellValue(object.getFacility11());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(12);
            dcell.setCellValue(object.getFacility12());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(13);
            dcell.setCellValue(object.getFacility13());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(14);
            dcell.setCellValue(object.getFacility14());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(15);
            dcell.setCellValue(object.getFacility15());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(16);
            dcell.setCellValue(object.getFacility16());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(17);
            dcell.setCellValue(object.getFacility17());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(18);
            dcell.setCellValue(object.getFacility18());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(19);
            dcell.setCellValue(object.getFacility19());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(20);
            dcell.setCellValue(object.getFacility20());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(21);
            dcell.setCellValue(object.getFacility21());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(22);
            dcell.setCellValue(object.getFacility22());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(23);
            dcell.setCellValue(object.getFacility23());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(24);
            dcell.setCellValue(object.getFacility24());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(25);
            dcell.setCellValue(object.getFacility25());
            
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(colno-6);
            dcell.setCellValue(object.getCa());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(colno-5);
            dcell.setCellValue(object.getSa());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(colno-4);
            dcell.setCellValue(object.getTd());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(colno-3);
            dcell.setCellValue(object.getCms());
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(colno-2);
            dcell.setCellValue(object.getFx());
            dcell.setCellStyle(rowStyle);
            
            dcell.setCellStyle(rowStyle);
            dcell = data.createCell(colno-1);
            dcell.setCellValue(object.getOther());
            
            dcell = data.createCell(colno);
            dcell.setCellValue(object.getTotal());
            dcell.setCellStyle(rowStyle);            
            rowno++;
        }

        RarocViewModel footer = (RarocViewModel) grid.getUserdata();

        data = sheet.createRow(rowno);
        dcell = data.createCell(0);
        dcell.setCellValue(new XSSFRichTextString(footer.getOutputName()));

        dcell = data.createCell(1);
        dcell.setCellValue(footer.getFacility1());
        dcell.setCellStyle(pStyle);

        dcell = data.createCell(2);
        dcell.setCellValue(footer.getFacility2());
        dcell.setCellStyle(pStyle);

        dcell = data.createCell(3);
        dcell.setCellValue(footer.getFacility3());
        dcell.setCellStyle(pStyle);

        dcell = data.createCell(4);
        dcell.setCellValue(footer.getFacility4());
        dcell.setCellStyle(pStyle);

        dcell = data.createCell(5);
        dcell.setCellValue(footer.getFacility5());
        dcell.setCellStyle(pStyle);

        dcell = data.createCell(6);
        dcell.setCellValue(footer.getFacility6());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(7);
        dcell.setCellValue(footer.getFacility7());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(8);
        dcell.setCellValue(footer.getFacility8());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(9);
        dcell.setCellValue(footer.getFacility9());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(10);
        dcell.setCellValue(footer.getFacility10());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(11);
        dcell.setCellValue(footer.getFacility11());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(12);
        dcell.setCellValue(footer.getFacility12());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(13);
        dcell.setCellValue(footer.getFacility13());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(14);
        dcell.setCellValue(footer.getFacility14());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(15);
        dcell.setCellValue(footer.getFacility15());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(16);
        dcell.setCellValue(footer.getFacility16());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(17);
        dcell.setCellValue(footer.getFacility17());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(18);
        dcell.setCellValue(footer.getFacility18());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(19);
        dcell.setCellValue(footer.getFacility19());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(20);
        dcell.setCellValue(footer.getFacility20());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(21);
        dcell.setCellValue(footer.getFacility21());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(22);
        dcell.setCellValue(footer.getFacility22());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(23);
        dcell.setCellValue(footer.getFacility23());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(24);
        dcell.setCellValue(footer.getFacility24());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(25);
        dcell.setCellValue(footer.getFacility25());
        dcell.setCellStyle(pStyle);

        dcell = data.createCell(colno-6);
        dcell.setCellValue(footer.getCa());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(colno-5);
        dcell.setCellValue(footer.getSa());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(colno-4);
        dcell.setCellValue(footer.getTd());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(colno-3);
        dcell.setCellValue(footer.getCms());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(colno-2);
        dcell.setCellValue(footer.getFx());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(colno-1);
        dcell.setCellValue(footer.getOther());
        dcell.setCellStyle(pStyle);
        
        dcell = data.createCell(colno);
        dcell.setCellValue(footer.getTotal());
        dcell.setCellStyle(pStyle);
        
        for (short col = 0; col <= colno; col++) {
            sheet.autoSizeColumn(col);
        }
    }    
}