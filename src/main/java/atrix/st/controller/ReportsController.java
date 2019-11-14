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
package atrix.st.controller;

import atrix.common.model.JQueryDataTableParamModel;
import atrix.common.util.DataTablesParamUtility;
import atrix.common.util.DataTablesResponse;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithColType;
import atrix.st.dao.ReportDao;
import atrix.st.model.MstExecutionModel;
import atrix.st.model.ReportModel;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vaio
 */
@Controller
@RequestMapping(value = "/reports", method = RequestMethod.GET)
public class ReportsController {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ReportDao reportDao;

    @RequestMapping
    public String RptMainPage() {
        return "reports/reportsMain";
    }

    // Execution ID Search Page
    @RequestMapping(value = "/filter")
    public String FilterPage() {
        return "reports/findReportId";
    }

    @RequestMapping(value = "/filter/grid")
    public @ResponseBody
    DataTablesResponse FilterGrid(HttpServletResponse response, HttpServletRequest request) {
        JQueryDataTableParamModel param = DataTablesParamUtility.getParam(request);
        DataTablesResponse<MstExecutionModel> json = reportDao.listMstExecution(param.sEcho, param.iDisplayStart,
                param.iDisplayLength, param.iSortColumnIndex, param.sSortDirection, param.sSearch);
        return json;
    }

    // Stress Testing >> Tabular Reports
    @RequestMapping(value = "/stress/{report}")
    public String RptStressTest(Map<String, Object> map, @PathVariable("report") String report) {
        String uri = "stress/page/" + report + "List";
        map.put("breadcrumbMessage", messageSource.getMessage("breadcrumb." + report, null, "Breadcrumb", null));
        map.put("uri", uri);
        return "reports/stressTesting";
    }

    // Stress Testing >> Tabular Reports >> Grid Page
    @RequestMapping(value = "/stress/page/{report}")
    public String GridPage(Map<String, Object> map, @PathVariable("report") String report,
            @RequestParam(value = "id", required = true) String id) {
        map.put("js", report);
        map.put("execId", id);
        map.put("execName", reportDao.getExecutionName(id));
        return "reports/grid"; 
    }

    // Stress Testing >> Test Grid
    @RequestMapping(value = "/stress/detailsList/grid")
    public @ResponseBody
    GridWithColType<ReportModel> TestGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam("id") String id) {
        final GridWithColType<ReportModel> list = reportDao.listDetails(page, max, sidx, sord, searchField, searchOper, searchString, id);
        return list;
    }
    
    /* Stress Testing >> Test XLS
    @RequestMapping(value = "/stress/testList/xls")
    public String TestXLS(@RequestParam("id") String id) {
        final GridPage<ReportModel> list = reportDao.listTests(page, max, sidx, sord, searchField, searchOper, searchString, id);
        return "XlsStressTest";
    }*/

    // Stress Testing >> Details Grid
    @RequestMapping(value = "/stress/testList/grid")
    public @ResponseBody
    GridPage<ReportModel> DetailsGrid(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "20") int max,
            @RequestParam(value = "sidx", required = false) String sidx,
            @RequestParam(value = "sord", required = false) String sord,
            @RequestParam(value = "searchField", required = false) String searchField,
            @RequestParam(value = "searchOper", required = false) String searchOper,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam("id") String id) {
        final GridPage<ReportModel> list = reportDao.listTests(page, max, sidx, sord, searchField, searchOper, searchString, id);
        return list;
    }
}