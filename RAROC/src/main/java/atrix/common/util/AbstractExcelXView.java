/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

/**
 *
 * @author vaio
 */
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractExcelXView extends AbstractView {

    /**
     * The content type for an Excel(xlsx) response
     */
    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /**
     * The extension to look for existing templates
     */
    private static final String EXTENSION = ".xlsx";
    private String url;

    /**
     * * Default Constructor. * Sets the content type of the view to
     * openxmlformats.
     */
    public AbstractExcelXView() {
        setContentType(CONTENT_TYPE);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    /**
     * Renders the Excel view, given the specified model.
     */
    @Override
    protected final void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        XSSFWorkbook workbook;
        ByteArrayOutputStream baos = createTemporaryOutputStream();
        if (this.url != null) {
            workbook = getTemplateSource(this.url, request, response);
        } else {
            workbook = new XSSFWorkbook();
            logger.debug("Created Excel Workbook from scratch");
        }
        buildExcelDocument(model, workbook, request, response);
        workbook.write(baos);
        writeToResponse(response, baos);        
    }

    protected XSSFWorkbook getTemplateSource(String url, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
        Locale userLocale = RequestContextUtils.getLocale(request);
        Resource inputFile = helper.findLocalizedResource(url, EXTENSION, userLocale);
        // Create the Excel document from the source.
        if (logger.isDebugEnabled()) {
            logger.debug("Loading Excel workbook from " + inputFile);
        }
        response.setContentType(getContentType());        
        Workbook wb = WorkbookFactory.create(inputFile.getInputStream());
        return (XSSFWorkbook) wb;
    }

    protected abstract void buildExcelDocument(
            Map<String, Object> model, XSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    protected Cell getCell(Sheet sheet, int row, int col) {
        Row sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        Cell cell = sheetRow.getCell(col);
        if (cell == null) {
            cell = sheetRow.createCell(col);
        }
        return cell;
    }

    protected void setText(Cell cell, String text) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(text);
    }
}