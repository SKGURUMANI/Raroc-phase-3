<%-- 
    Document   : raraocView
    Created on : Aug 21, 2014, 1:04:32 PM
    Author     : vinoy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<c:url var="path" value="resources/scripts/grid/raroc/rarocView.js"/>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/raroc/rarocNextFy.js"/>"></script>
<script type="text/javascript" src="${path}"></script>

<div class="col-lg-12 marginTop">
    <div class="panel panel-default">
        <div class="panel-heading">
            <a><em>Customer Details</em></a>
            <span class="pull-right clickable "><i class="glyphicon glyphicon-chevron-down"></i></span>
        </div>
        <div class="panel-body no-gutters">
            <div class="spacingBottom">
                <table class="table table-bordered" id="summary-table">
                    <tr>
                        <td class="text">Customer Name: <c:out value="${form.cname}"/></td>           
                        <td class="text">Segment: <c:out value="${form.bussunit}"/></td>
                        <td class="text" colspan="2">Industry: <c:out value="${form.ind}"/></td>
                    </tr>
                    <tr>
                        <td class="text">Customer ID: <c:out value="${form.cid}"/></td>
                        <td class="text">Rating Tool ID: <c:out value="${form.rid}"/></td>
                        <td class="text">Rating Tool: <c:out value="${form.rtool}"/></td>
                        <td class="text">Internal Rating: <c:out value="${form.intRat}"/></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="col-lg-12 marginTop">
    <div class="panel panel-default">
        <div class="panel-heading">
            <a><em>RAROC Calculation Sheet</em></a>
            <span class="pull-right clickable "><i class="glyphicon glyphicon-chevron-down"></i></span>
        </div>
        <div class="panel-body no-gutters">
            <input type="hidden" id="ref" value="<c:out value="${form.rarocref}"/>"/>
            <input type="hidden" id="cnt" value="<c:out value="${form.facility}"/>"/>
            <div id="juiTabs">
                <ul class="nav nav-tabs" id="mytabs"> 
                    <c:choose>
                        <c:when test="${sessionScope.unit == 1}">
                            <c:set var="unit" value=""/>                        
                        </c:when>
                        <c:when test="${sessionScope.unit == 100000}">
                            <c:set var="unit" value="lacs"/>                        
                        </c:when>
                        <c:when test="${sessionScope.unit == 1000000}">
                            <c:set var="unit" value="millions"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="unit" value="crores"/>
                        </c:otherwise>
                    </c:choose>
                    <li class="active"><a data-toggle="tab" href="#tabs-1">Output - Current FY</a></li>
                    <li><a data-toggle="tab" href="#tabs-2">Output - Next FY</a></li>
                    <li><a data-toggle="tab" href="#tabs-4">Sensitivity RW</a></li>
                    <li><a data-toggle="tab" href="#tabs-5">Sensitivity Utilization</a></li>
                </ul>
                <div id="tab-content">
                <div id="tabs-1" class="tab-pane fade in active">
                    <div id="jqgrid1">
                        <table id="grid1"></table>
                        <div id="pager1"></div>
                    </div>
                </div>
                <div id="jq2">
                    <div id="tabs-2" class="tab-pane fade">

                        <div id="jqgrid2">
                            <table id="grid2"></table>
                            <div id="pager2"></div>
                        </div>
                    </div>
                </div>
                <div id="tabs-4" class="tab-pane fade"></div>
                <div id="tabs-5" class="tab-pane fade"></div>
            </div>        
            </div>
        </div>
    </div>

</div>