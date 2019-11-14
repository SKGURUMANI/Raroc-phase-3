<%-- 
    Document   : defragTables
    Created on : 6 Feb, 2014, 3:33:16 AM
    Author     : vaio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/Defrag.js"/>"></script>
<div class="col-lg-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            Administration >> Defrag Tables
        </div>
    </div>
    <div id="jqgrid" class="spacing">
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
</div>