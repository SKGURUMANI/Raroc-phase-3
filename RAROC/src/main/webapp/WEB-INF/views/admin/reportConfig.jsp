<%-- 
    Document   : reportConfig
    Created on : Jun 6, 2016, 10:49:08 AM
    Author     : Amolraj
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://asymmetrix/custom/csrf" prefix="cf"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/config/jqgridConfig.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/ReportConfig.js"/>"></script>
<div class="container">
    <cf:csrfToken/>
    <div class="breadcrumb"><label><fmt:message key="breadcrumb.adminPrsLog"/></label></div>
    <div id="jqgrid" class="spacing">
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
    <div class="hidden">      
        <label id="caption">Report Configuration</label>
        <label id="col1">Report Id</label>
        <label id="col2">Report Name</label>
        <label id="col3">Report Group</label>
        <label id="col4">View Name</label>
        <label id="col5">Active Flag</label>
    </div>
</div>