<%-- 
    Document   : rarocExistingGrid
    Created on : Aug 20, 2014, 3:24:11 PM
    Author     : vinoy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="resources/scripts/grid/raroc/rarocExistingGrid.js"></script>
<div id="jqgrid" class="container spacingBottom">
    <table id="grid"></table>
    <div id="pager"></div>
</div>
<c:url var="curl" value="/raroc/existing"/>
<input type="hidden" value="${oldref}" id="oldref"/>
<form:form id="recInfo" method="post" commandName="formRarocMaster" action="${curl}">
    <form:hidden path="rarocref" id="refcode"/>
    <form:hidden path="facility" id="facility"/>
    <form:hidden path="bussunit"/>
    <form:hidden path="cname"/>
    <form:hidden path="cid"/>
    <form:hidden path="rtool"/>
    <form:hidden path="intRat"/>
    <form:hidden path="rid"/>
    <form:hidden path="ind"/>  
</form:form>