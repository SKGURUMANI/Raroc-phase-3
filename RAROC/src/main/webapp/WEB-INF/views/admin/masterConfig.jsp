<%-- 
    Document   : masterConfig
    Created on : Nov 29, 2015, 2:27:25 PM
    Author     : Amol
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/config/jqgridConfig.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/MasterConfig.js"/>"></script>
<div class="container">
    <cf:csrfToken/>
    <div class="breadcrumb"><label><fmt:message key="breadcrumb.adminPrsLog"/></label></div>
    <div id="jqgrid" class="spacing">
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
    <div class="hidden">      
        <label id="caption">Master Table Configuration</label>
        <label id="col1">Table Name</label>
        <label id="col2">Table Description</label>
        <label id="col3">Edit Flag</label>
        <label id="col4">View Flag</label>
        <label id="col5">Active Flag</label>
    </div>
</div>