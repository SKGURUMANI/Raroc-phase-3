<%-- 
    Document   : colPage
    Created on : Nov 30, 2015, 12:00:27 PM
    Author     : Amol
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://asymmetrix/custom/csrf" prefix="cf"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/admin/masterColList.js" />"></script>
<div class="container">
    <cf:csrfToken/>
    <div class="breadcrumb"><label>Master Table >> Column Configuration</label></div>
    <input type="hidden" id="_tk" name="_tk"/>
    <input type="hidden" id="tableId" value="${table}"/>
    <div id="jqgrid" class="spacing"> 
        <table id="grid"></table>
        <div id="pager"></div>
    </div>
</div>
<style>

    /*    label, input { display:block; }*/
    input.text { margin-bottom:12px; width:95%; padding: .4em; }
    fieldset { padding:0; border:0; margin-top:25px; }
    h1 { font-size: 1.2em; margin: .6em 0; }
    div#users-contain { width: 350px; margin: 20px 0; }
    div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
    div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
    .ui-dialog .ui-state-error { padding: .3em; }
    .validateTips { border: 1px solid transparent; padding: 0.3em; }
</style>

<div id="dialog-form" title="Create Mannual List">
    <p class="validateTips">All form fields are required.</p>
    <form id="dForm">
        <fieldset>
            <label for="name">Table Name:</label>
            <input type="text" class="hidden" id="dType" />
            <input type="text" name="name" id="tab" value="${table}"  required="true" class="text ui-widget-content ui-corner-all">
            <label for="email">Column Name</label>
            <input type="text" name="email" id="hidd" readonly="true" required="true" class="text ui-widget-content ui-corner-all">
            <label for="password">List</label>
            <textarea class="text ui-widget-content ui-corner-all" required="true" id="lValue" style="width: 300px;" name="value"></textarea>
            <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
        </fieldset>
    </form>
</div>            