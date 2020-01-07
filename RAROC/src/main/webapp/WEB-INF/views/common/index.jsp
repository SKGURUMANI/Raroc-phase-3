<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
        <title><fmt:message key="headerTitle"/></title>

        <link rel="icon" type="image/x-icon" href="<c:url value="/resources/css/images/bct.ico"/>">
        <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/css/images/bct.ico"/>">
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
        <script type="text/javascript" src="<c:url value="/resources/js/bootstrap-dialog.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/bootstrap-multiselect.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/metisMenu.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/jquery.jqGrid.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/jquery.validationEngine.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/jquery.validationEngine-en.js"/>"></script>
<!--        <script type="text/javascript" src="<c:url value="/resources/js/highcharts.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/highcharts-more.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/highcharts-heatmap.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/highcharts-export.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/js/highcharts-drilldown.js"/>"></script>-->
        <script type="text/javascript" src="<c:url value="/resources/scripts/config/jqgridConfig.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/scripts/config/appSec.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/resources/scripts/template.js"/>"></script>
<!--        <script type="text/javascript" src="<c:url value="/resources/scripts/charts/Jbanner.js"/>"></script>-->
    </head>
    <body>
        <div id="wrapper">
            <c:import url="/WEB-INF/views/layout/navbar.jsp" />

            <div id="page-wrapper">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-lg-12">
				            <div class="container">
				            <div class="row">                
				                <div class="col-md-3 col-md-offset-3 panel-view-1">
				                    <sec:authorize url="/raroc">
				                     <a href="<c:url value='/raroc'/>">
					                    <div class="login-panel panel panel-default panel-border">
					                        <div class="panel-heading panel-index">
					                            <h4 class="calc calc-panel">
					                                RAROC 
					                            </h4>
					                        </div>
					                    </div>    
				                     </a> 
				                    </sec:authorize>                
				                </div>
				                 <div class="col-md-3 col-md-offset-3 panel-view-2">
				                  <sec:authorize url="/rarocAuth">
				                     <a href="<c:url value='/rarocAuth'/>">
					                    <div class="login-panel panel panel-default panel-border">
					                        <div class="panel-heading panel-index">
					                            <h4 class="calc calc-panel">
					                                RAROC Admin
					                            </h4>
					                        </div>
					                    </div>  
				                    </a>
				                    </sec:authorize>                  
				                </div>
				            </div>
				        </div>
                      </div>
                    </div>
                </div>
            </div>
            <!-- /#page-wrapper -->
        </div>
  		<div class="footer navbar-fixed-bottom">
		    <div class="col-lg-12">
		    	<div class="pull-left"><a class="Copyright-2019-Bah" target="_blank" href="http://www.bahwancybertek.com/industries/bfsi"><spring:message code="footer.right"/></a></div>
		        <div class="pull-right"><img class="rt360_white_logo_1 center-block" src="<c:url value="/resources/css/images/rt-360-white-logo-1.png"/>"/></div>
		    </div> 
    </body>
</html>