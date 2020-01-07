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
		<link href="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.css" rel="stylesheet">
  		<script src="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"></script>
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
    <%-- <div class="maroontheme"> 
     	<div class="topBand">
     		<div class="container">
     			<div class="row">
     				<div class="col-xs-12"></div>
     			</div>
     		</div>
     	</div>
     	<div class="topMenu personalHeader">
     	<div class="container">
	     	<div class="row">
		     	<div class="col-md-12">
		     	 <div class="logoSect">
				  <img alt="Axis Bank logo to homepage" src="<c:url value="/resources/css/images/AXIS/logo-white.png"/>"/> 
				 </div>
				 <div class="whitebg d-inline-block">
				 	<div class="navbarSect">
					  
				  	</div>
		  		</div>
		  		<div class="logoSect1">
				 
				 </div>
		  		</div>
		  	</div>
		  </div>
		  </div>
		</div> --%>
	  <nav class="navbar1 navbar-inverse navbar-fixed-top">
	      <div class="">
	        <div class="navbar-header nav-logo-curve">
	          <div class="LogoPart">
	            <a class="navbar-brand logo"></a>
	          </div>
	          <div class="logoCurve"></div>
	        </div>
	        <div id="navbar" class="navbar-collapse collapse menu-container">
	        </div>
	      </div>
      </nav>
        <div class="container">
            <div class="row">                
                <div class="col-md-4 col-md-offset-4">
                    <div class="login-panel">
                         <div class="panel-body">
                         	<br/><br/><br/><br/>
                             <c:if test="${not empty logout}">
				                <div class="text-default text-center">
				                  <h5><c:out value="${logout}"/></h5>
				                </div>
				            </c:if>
                            <input type="hidden" id="phrase" value="<c:out value="${pageContext.session.id}"/>"/>
                            <form role="form" name='f' action="<c:url value='/login' />" method='POST'>
                                <br/>
                                <a class="btn btn-lg btn-default btn-login"  style="margin-left:40%;"href="<c:url value='/login'/>">Login</a>
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
       
        </div>
        <div class="footer navbar-fixed-bottom">
		    <div class="col-lg-12">
		    	<div class="pull-left"><a class="Copyright-2019-Bah" target="_blank" href="http://www.bahwancybertek.com/industries/bfsi"><spring:message code="footer.right"/></a></div>
		        <div class="pull-right"><img class="rt360_white_logo_1 center-block" src="<c:url value="/resources/css/images/rt-360-white-logo-1.png"/>"/></div>
		    </div>            
</div>   
   </body>
</html>
