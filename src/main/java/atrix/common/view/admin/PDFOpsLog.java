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

import atrix.common.model.TaskMonitorModel;
import atrix.common.service.FormatterService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.document.AbstractPdfView;

/**
 *
 * @author vaio
 */
public class PDFOpsLog extends AbstractPdfView {

    private static Font smallBold = FontFactory.getFont("Lucida Sans Unicode", BaseFont.CP1252, BaseFont.EMBEDDED, 9, Font.BOLD);
    private static Font small = FontFactory.getFont("Lucida Sans Unicode", BaseFont.CP1252, BaseFont.EMBEDDED, 9, Font.NORMAL);

    @Override
    protected Document newDocument() {
        return new Document(PageSize.A4.rotate(), 0, 0, 20, 20);
    }

    @Override
    protected void buildPdfDocument(Map model, Document document,
            PdfWriter writer, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        FormatterService fmt = new FormatterService();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"OpsLog.pdf\"");
        float[] colsWidth = {1.667f, 1.667f, 1.667f, 1.667f, 1.667f, 1.667f};

        HeaderFooter heading = new HeaderFooter(new Phrase(getMessageSourceAccessor().getMessage("caption.admin.opsLog")), false);
        heading.setAlignment(Element.ALIGN_CENTER);
        document.setHeader(heading);

        document.open();

        PdfPTable table = new PdfPTable(colsWidth);
        table.setHeaderRows(1);
        PdfPCell header;
        PdfPCell tdrow;

        header = new PdfPCell(new Phrase(getMessageSourceAccessor().getMessage("label.date"), smallBold));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase(getMessageSourceAccessor().getMessage("label.userid"), smallBold));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase(getMessageSourceAccessor().getMessage("label.userName"), smallBold));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);
        
        header = new PdfPCell(new Phrase(getMessageSourceAccessor().getMessage("label.action"), smallBold));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase(getMessageSourceAccessor().getMessage("label.actionDesc"), smallBold));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase(getMessageSourceAccessor().getMessage("label.status"), smallBold));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);
                
        ArrayList list = (ArrayList) model.get("list");
        Iterator itr1 = list.iterator();
        while (itr1.hasNext()) {
            TaskMonitorModel object = (TaskMonitorModel) itr1.next();

            tdrow = new PdfPCell(new Phrase(object.getStime(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(fmt.ToString(object.getUserid()), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(fmt.ToString(object.getUserName()), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(fmt.ToString(object.getTask()), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(fmt.ToString(object.getRemarks()), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(fmt.ToString(object.getStatus()), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);
            
        }
        document.add(table);
        document.close();
    }
}