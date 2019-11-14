<%-- 
    Document   : repColPage
    Created on : Jun 7, 2016, 11:03:15 AM
    Author     : Amolraj
--%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://asymmetrix/custom/csrf" prefix="cf"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/reportColList.js" />"></script>
<div class="container">
    <cf:csrfToken/>
    <div class="breadcrumb"><label>Report >> Column Configuration</label></div>
    <input type="hidden" id="_tk" name="_tk"/>
    <input type="hidden" id="rId" value="${rep}"/>
    <input type="hidden" id="rName" value="${repName}"/>
    <div id="jqgrid" class="spacing"> 
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
</div>

  