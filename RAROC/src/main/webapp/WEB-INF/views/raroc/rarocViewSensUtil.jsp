<%-- 
    Document   : rarocViewSensUtil
    Created on : 1 Aug, 2014, 2:59:11 PM
    Author     : vaio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:url var="path" value="resources/scripts/charts/sensitivityUtil.js"/>
<script type="text/javascript" src="${path}"></script>
<div id="rwUtil">
    <div style="width:100%; margin:0px auto; text-align:center">
        <select id="fac2">
            <c:forEach items="${list}" var="opt">                                    
                <option value="${fn:escapeXml(opt.key)}"><c:out value="${opt.value}"/></option>
            </c:forEach>
        </select>
        <input type="button" class="btn btn-default" id="renderChart2" value="Go"/>
    </div>
    <div id="chart2"></div>
</div>