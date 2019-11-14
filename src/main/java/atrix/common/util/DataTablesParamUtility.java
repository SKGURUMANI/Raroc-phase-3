/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import atrix.common.model.JQueryDataTableParamModel;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public class DataTablesParamUtility {

    public static JQueryDataTableParamModel getParam(HttpServletRequest request) {
        if (request.getParameter("sEcho") != null && !request.getParameter("sEcho").equals("")) {
            JQueryDataTableParamModel param = new JQueryDataTableParamModel();
            param.sEcho = Integer.parseInt(request.getParameter("sEcho"));
            param.sSearch = request.getParameter("sSearch");
            param.sColumns = request.getParameter("sColumns");
            param.iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
            param.iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
            param.iColumns = Integer.parseInt(request.getParameter("iColumns"));
            param.iSortingCols = Integer.parseInt(request.getParameter("iSortingCols"));
            param.iSortColumnIndex = Integer.parseInt(request.getParameter("iSortCol_0"));
            param.sSortDirection = request.getParameter("sSortDir_0");
            return param;
        } else {
            return null;
        }
    }
}