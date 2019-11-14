/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vaio
 */
public class PoiStyles {

    public CellStyle doubleStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("##,##,##0.00"));
        return style;
    }

    public CellStyle intStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("##,##,##0"));
        return style;
    }

    public CellStyle percentageStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("0.00%"));
        return style;
    }

    public CellStyle boldStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }

    public CellStyle headRightStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        return style;
    }
    
    public CellStyle headCenterStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }

    public CellStyle dataCenterStyle(Workbook wb){
        Font font = wb.createFont();
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }
    
    public CellStyle rightStyle(Workbook wb) {
        Font font = wb.createFont();
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        return style;
    }

    public CellStyle color(XSSFWorkbook wb, String color, CellStyle format) {
        XSSFCellStyle style = wb.createCellStyle();
        style.cloneStyleFrom(format);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.index);
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.index);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.index);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.index);
        if (color.equals("green")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 176, 80)));
        } else if (color.equals("orange")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 192, 0)));
        } else if (color.equals("red")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
        } else if (color.equals("grey")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(128, 128, 128)));
        }
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }
    
    public CellStyle color(XSSFWorkbook wb, String color) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.index);
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.index);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.index);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.index);
        if (color.equals("green")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 176, 80)));
        } else if (color.equals("orange")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 192, 0)));
        } else if (color.equals("red")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
        } else if (color.equals("grey")) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(128, 128, 128)));
        }
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }
    
    public CellStyle doubleCenterStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("##,##,##0.00"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }

    public CellStyle intCenterStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("##,##,##0"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }

    public CellStyle percentageCenterStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("0.00%"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }
}