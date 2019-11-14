<%-- 
    Document   : listPrsLog
    Created on : 30 Jan, 2014, 3:17:13 PM
    Author     : vaio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/AuditPrsLog.js"/>"></script>
<div class="col-lg-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            <fmt:message key="breadcrumb.adminPrsLog"/>
        </div>
    </div>
    <div id="jqgrid" class="spacing">
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
    <div class="hidden">      
        <label id="caption">Process Log</label>
        <label id="col1">Batch ID</label>
        <label id="col2">Task Name</label>
        <label id="col3">Start Time</label>
        <label id="col4">End Time</label>
        <label id="col5">Status</label>
        <label id="col6">Remarks</label>
        <label id="col7">Error</label>
    </div>
</div>