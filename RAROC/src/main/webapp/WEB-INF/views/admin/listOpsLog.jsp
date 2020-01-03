<%-- 
    Document   : listOpsLog
    Created on : 30 Jan, 2014, 3:17:04 PM
    Author     : vaio
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/AuditOpsLog.js"/>"></script>
<div class="col-lg-12 marginTop">
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group">
                <div class="col-lg-2 col-md-2">
                    <label style="margin-top: 8px;" class="control-label"><fmt:message key="label.dateBetween"/>:</label>
                </div>
                <div class="col-lg-2 col-md-2">
                    <input class="form-control" type="text" id="from"/>
                </div>
                <div class="col-lg-2 col-md-2">
                    <input class="form-control" type="text" id="to"/>
                </div>
                <div class="col-lg-1 col-md-1">
                    <input class="btn btn-default" type="button" id="filterlog" value="Go"/>
                </div>                
            </div>                                        
        </div>
    </div>    
    <div id="jqgrid" class="spacing">
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
    <div class="hidden">      
        <label id="caption"><fmt:message key="caption.admin.opsLog"/></label>
        <label id="col1"><fmt:message key="label.date"/></label>
        <label id="col2"><fmt:message key="label.userid"/></label>
        <label id="col3"><fmt:message key="label.userName"/></label>
        <label id="col4"><fmt:message key="label.action"/></label>
        <label id="col5"><fmt:message key="label.actionDesc"/></label>
        <label id="col6"><fmt:message key="label.status"/></label>
    </div>            
</div>