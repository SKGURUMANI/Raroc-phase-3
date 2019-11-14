<%-- 
    Document   : options
    Created on : 5 Nov, 2013, 8:01:09 PM
    Author     : vaio
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<select>
    <c:if test="${not empty list}">
        <c:forEach var="object" items="${list}">
            <option value="${object.key}"><c:out value="${object.value}" /></option>
        </c:forEach>
    </c:if>    
</select>
