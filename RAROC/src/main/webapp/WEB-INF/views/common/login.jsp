<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<html>
    <head>

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><spring:message code="headerTitle"/></title>

        <link href="<c:url value="/resources/css/images/bct.ico"/>" rel="icon" type="image/x-icon">
        <link href="<c:url value="/resources/css/images/bct.ico"/>" rel="shortcut icon" type="image/x-icon">
        <link href="<c:url value="/resources/css/atrix-ui.css"/>" rel="stylesheet">

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
            <script src="<c:url value="/resources/scripts/html5shiv.js"/>"></script>
            <script src="<c:url value="/resources/scripts/respond.min.js"/>"></script>
        <![endif]-->

        <!-- jQuery -->
        <script type="text/javascript" src="<c:url value="/resources/js/jquery.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/bootstrap.min.js"/>"></script> 
        <script type="text/javascript" src="<c:url value="/resources/js/crypto.js"/>"></script>        
        <script type="text/javascript" src="<c:url value="/resources/scripts/config/appSec.js"/>"></script>
    </head>
    <body class="login">
        <div class="container">
            <div class="row">                
                <div class="col-md-4 col-md-offset-4">
                    <div class="login-panel panel panel-default">
                        <div class="panel-heading">
                            <img class="bct-logo center-block" src="<c:url value="/resources/css/images/axis-logo.png"/>"/>
                            <h2>
                                <img src="<c:url value="/resources/css/images/logo.png"/>"/>
                                RAROC Calculator
                            </h2>
                        </div>
                        <div class="panel-body">
                            <input type="hidden" id="phrase" value="<c:out value="${pageContext.session.id}"/>"/>
                            <form role="form" name='f' action="<c:url value='/login' />" method='POST'>
                                <br/>
                                <div class="form-group">
                                    <input class="form-control" placeholder="Username" name="username" type="text" autofocus autocomplete="off">
                                </div>                      
                                <br/>
                                <div class="form-group">
                                    <input class="form-control" placeholder="Password" name="password" type="password" value="" autocomplete="off">
                                    <input type="hidden" name="salt"/>
                                    <input type="hidden" name="iv"/>
                                </div>
                                <br/>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <input class="btn btn-lg btn-default btn-block" type="submit" value="<spring:message code="form.login"/>"/>
                                <br/>
                            </form>
                        </div>
                    </div>                    
                </div>
            </div>
            <div class="text-danger text-center">                        
                <c:choose>
                    <c:when test="${fn:length(sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message) > 100}">
                        ${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}
                    </c:when>    
                    <c:otherwise>
                        ${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty error}"> 
                    <c:out value="${error}" />
                </c:if>
            </div>
            <c:if test="${not empty logout}">
                <div class="text-default text-center">
                    <c:out value="${logout}"/>
                </div>
            </c:if>
        </div>
        <div class="footer navbar-fixed-bottom">
            <div class="col-lg-12">
                <div class="pull-left"><spring:message code="footer.left"/></div>
                <div class="pull-right"><a target="_blank" href="http://www.bahwancybertek.com/industries/bfsi"><spring:message code="footer.right"/></a></div>
            </div>            
        </div>           
    </body>
</html>
