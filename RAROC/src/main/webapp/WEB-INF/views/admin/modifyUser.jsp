<%-- 
    Document   : listUser
    Created on : 27 Aug, 2013, 5:42:09 PM
    Author     : vaio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/listUsers.js"/>"></script>
<input id="userid" type="hidden" type="text" value="<fmt:message key='label.userid'/>" /> 
<input id="userName" type="hidden" type="text" value="<fmt:message key='label.userName'/>" /> 
<input id="email" type="hidden" type="text" value="<fmt:message key='label.email'/>" /> 
<input id="phone" type="hidden" type="text" value="<fmt:message key='label.phone'/>" /> 
<input id="active" type="hidden" type="text" value="<fmt:message key='label.active'/>" />
<input id="caption" type="hidden" type="text" value="<fmt:message key='caption.admin.userlist'/>" /> 
<input id="vbutn" type="hidden" type="text" value="<fmt:message key='jqgrid.button.view'/>" />
<input id="iheader" type="hidden" type="text" value="<fmt:message key='jqgrid.header.info'/>" />
<input id="nalert" type="hidden" type="text" value="<fmt:message key='jqgrid.alert.noselect'/>" />
<div class="col-lg-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            <fmt:message key="breadcrumb.modifyUser"/>
        </div>
    </div>
    <div id="jqgrid" class="spacing">
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
</div>