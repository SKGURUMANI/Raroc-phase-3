<%-- 
    Document   : redirect
    Created on : 29 Mar, 2013, 11:40:37 AM
    Author     : vaio
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="redirect" value="${sessionScope.homepage}" />
<c:redirect url="${redirect}"/>