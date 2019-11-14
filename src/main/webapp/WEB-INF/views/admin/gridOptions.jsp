<%-- 
    Document   : gridOptions
    Created on : Jan 18, 2016, 1:49:09 PM
    Author     : Amol
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<select>
    <c:if test="${not empty list}">
        <option value="">Select...</option>
        <c:forEach var="object" items="${list}">
            <option value="${object.code}"><c:out value="${object.desc}" /></option>
        </c:forEach>
    </c:if>    
</select>
