<%-- 
    Document   : accessDenied
    Created on : 31 Mar, 2013, 2:07:56 AM
    Author     : vaio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <title><fmt:message key="title"/></title>
    <body>
        <h1>HTTP Status 403 - Access is denied</h1>
        <h3>Message :<font color="Blue"><c:out value="${msg}"/></font></h3> 
    </body>
</html>
