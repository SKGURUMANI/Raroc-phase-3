<%-- 
    Document   : sackUser
    Created on : 22 Aug, 2013, 10:49:35 AM
    Author     : vaio
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/ajax/admin.js"/>"></script>
<div class="col-lg-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            <fmt:message key="breadcrumb.sackUser"/>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-body no-gutters">
            <c:url var="curl" value="/admin/sack"/>
            <form:form method="post" commandName="userModel" action="${curl}" autocomplete="off" id="formAdmin" htmlEscape="true">
                <div class="form-group">
                    <label for="userid" class="col-lg-1 control-label"><fmt:message key="label.userid"/></label>
                    <div class="col-lg-4">
                        <form:input cssClass="validate[required,maxSize[20]] form-control" path="username"/>
                    </div>
                </div>
                <div class="col-lg-3">
                    <button type="submit" class="btn btn-default"><fmt:message key="label.submit"/></button>
                </div>
            </form:form>
        </div>
    </div>
</div>