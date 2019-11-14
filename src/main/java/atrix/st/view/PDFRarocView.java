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

import atrix.common.util.GridWithFooterPage;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.document.AbstractPdfView;

/**
 *
 * @author vaio
 */
public class PDFRarocView extends AbstractPdfView {

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

        RarocMasterModel head = (RarocMasterModel) model.get("form");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"RAROC_" + head.getCid() + ".pdf\"");

        Chunk headerText = new Chunk(" \t\tRAROC - " + head.getCname()
                + "\n \t\tCalculator version - v4.0 "
                + "\n \t\tLatest version release date - " + head.getVersion()
                + "\n \t\tComputation Timestamp - " + head.getCreated()
                + "\n \t\tRef ID - " + head.getRarocref()
                + "\n \t\tUser Id - " + head.getUsername()
                + "\n \t\tCIF ID - " + head.getCif()
                + "\n \t\tRating Tool Id - " + head.getRid()
                + "\n \t\tPAN - " + head.getPan(), smallBold);
        HeaderFooter heading;

        heading = new HeaderFooter(new Phrase(headerText), false);
        heading.setAlignment(Element.ALIGN_LEFT);
        document.setHeader(heading);

        document.open();

        PdfPTable table = new PdfPTable(head.getFacility() + 8);
        table.setHeaderRows(1);
        PdfPCell header;
        PdfPCell tdrow;

        header = new PdfPCell(new Phrase("Parameter"));
        header.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        for (int colno = 1; colno <= head.getFacility(); colno++) {
            header = new PdfPCell(new Phrase("Facility" + colno));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.setBackgroundColor(Color.lightGray);
            table.addCell(header);
        }

        header = new PdfPCell(new Phrase("CA"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase("SA"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase("TD"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase("CMS"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase("FX"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase("Other"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        header = new PdfPCell(new Phrase("Total"));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(Color.lightGray);
        table.addCell(header);

        GridWithFooterPage<RarocViewModel> grid = (GridWithFooterPage<RarocViewModel>) model.get("grid");
        java.util.List<RarocViewModel> list = grid.getRows();
        for (RarocViewModel object : list) {

            tdrow = new PdfPCell(new Phrase(object.getOutputName(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getFacility1(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            if (head.getFacility() > 1) {
                tdrow = new PdfPCell(new Phrase(object.getFacility2(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 2) {
                tdrow = new PdfPCell(new Phrase(object.getFacility3(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 3) {
                tdrow = new PdfPCell(new Phrase(object.getFacility4(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 4) {
                tdrow = new PdfPCell(new Phrase(object.getFacility5(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 5) {
                tdrow = new PdfPCell(new Phrase(object.getFacility6(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 6) {
                tdrow = new PdfPCell(new Phrase(object.getFacility7(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 7) {
                tdrow = new PdfPCell(new Phrase(object.getFacility8(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 8) {
                tdrow = new PdfPCell(new Phrase(object.getFacility9(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }
            if (head.getFacility() > 9) {
                tdrow = new PdfPCell(new Phrase(object.getFacility10(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }
            if (head.getFacility() > 10) {
                tdrow = new PdfPCell(new Phrase(object.getFacility11(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }
            if (head.getFacility() > 11) {
                tdrow = new PdfPCell(new Phrase(object.getFacility12(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 12) {
                tdrow = new PdfPCell(new Phrase(object.getFacility13(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 13) {
                tdrow = new PdfPCell(new Phrase(object.getFacility14(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 14) {
                tdrow = new PdfPCell(new Phrase(object.getFacility15(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 15) {
                tdrow = new PdfPCell(new Phrase(object.getFacility16(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 16) {
                tdrow = new PdfPCell(new Phrase(object.getFacility17(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 17) {
                tdrow = new PdfPCell(new Phrase(object.getFacility18(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 18) {
                tdrow = new PdfPCell(new Phrase(object.getFacility19(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 19) {
                tdrow = new PdfPCell(new Phrase(object.getFacility20(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 20) {
                tdrow = new PdfPCell(new Phrase(object.getFacility21(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            if (head.getFacility() > 21) {
                tdrow = new PdfPCell(new Phrase(object.getFacility22(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }
            if (head.getFacility() > 22) {
                tdrow = new PdfPCell(new Phrase(object.getFacility23(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }
            if (head.getFacility() > 23) {
                tdrow = new PdfPCell(new Phrase(object.getFacility24(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }
            if (head.getFacility() > 24) {
                tdrow = new PdfPCell(new Phrase(object.getFacility25(), small));
                tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
                tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tdrow);
            }

            tdrow = new PdfPCell(new Phrase(object.getCa(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getSa(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getTd(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getCms(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getFx(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getOther(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

            tdrow = new PdfPCell(new Phrase(object.getTotal(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(tdrow);

        }

        RarocViewModel footer = (RarocViewModel) grid.getUserdata();

        tdrow = new PdfPCell(new Phrase(footer.getOutputName(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_LEFT);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getFacility1(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        if (head.getFacility() > 1) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility2(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 2) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility3(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 3) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility4(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 4) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility5(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 5) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility6(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);

        }

        if (head.getFacility() > 6) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility7(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 7) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility8(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 8) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility9(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 9) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility10(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 10) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility11(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 11) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility12(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 12) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility13(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 13) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility14(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 14) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility15(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 15) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility16(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 16) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility17(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 17) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility18(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 18) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility19(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 19) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility20(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 20) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility21(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 21) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility22(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 22) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility23(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 23) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility24(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        if (head.getFacility() > 24) {
            tdrow = new PdfPCell(new Phrase(footer.getFacility25(), small));
            tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tdrow.setBackgroundColor(Color.lightGray);
            table.addCell(tdrow);
        }

        tdrow = new PdfPCell(new Phrase(footer.getCa(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getSa(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getTd(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getCms(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getFx(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getOther(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        tdrow = new PdfPCell(new Phrase(footer.getTotal(), small));
        tdrow.setHorizontalAlignment(Element.ALIGN_CENTER);
        tdrow.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tdrow.setBackgroundColor(Color.lightGray);
        table.addCell(tdrow);

        document.add(table);
        document.close();
    }
}
