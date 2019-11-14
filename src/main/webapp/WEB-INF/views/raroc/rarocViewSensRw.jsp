<%-- 
    Document   : rarocViewSensRw
    Created on : 1 Aug, 2014, 2:58:57 PM
    Author     : vaio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:url var="path" value="resources/scripts/charts/sensitivityRW.js"/>
<script type="text/javascript" src="${path}"></script>
<div id="rwSen">
    <div style="width:100%; margin:0px auto; text-align:center">
        <select id="fac1">
            <c:forEach items="${list}" var="opt">                                    
                <option value="${fn:escapeXml(opt.key)}"><c:out value="${opt.value}"/></option>
            </c:forEach>
        </select>
        <input type="button" class="btn btn-default" id="renderChart1" value="Go"/>
    </div>
    <div id="chart1"></div>
</div>