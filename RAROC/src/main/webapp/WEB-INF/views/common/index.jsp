<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
                    <sec:authorize url="<c:url value='/raroc'/>">
                        <div class="col-lg-4 col-md-4"  style="padding-top: 180px;">
                            <div class="panel"  style="background-color: #97144d">
                                <div class="panel-heading">
                                    <div class="row">
                                        <div class="col-xs-3">
                                            <i class="fa fa-user fa-3x"  style="color: floralwhite"></i>
                                        </div>
                                        <div class="col-xs-9 text-right">
                                            <div style="color: floralwhite"><h4>RAROC User</h4></div>
                                        </div>
                                    </div>
                                </div>
                                <a href="<c:url value='/raroc'/>">
                                    <div id="ewiDetails" class="panel-footer">
                                        <span class="pull-left">View Details</span>
                                        <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                        <div class="clearfix"></div>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </sec:authorize>
                    <sec:authorize url="<c:url value='/rarocAuth'/>">
                        <div class="col-lg-4 col-md-4"  style="padding-top: 180px;">
                            <div class="panel"  style="background-color: #97144d">
                                <div class="panel-heading">
                                    <div class="row">
                                        <div class="col-xs-3">
                                            <i class="fa fa-user-secret fa-3x"  style="color: floralwhite"></i>
                                        </div>
                                        <div class="col-xs-9 text-right">
                                            <div style="color: floralwhite"><h4>RAROC Authorizer</h4></div>
                                        </div>
                                    </div>
                                </div>
                                <a href="<c:url value='/rarocAuth'/>">
                                    <div id="ewiDetails" class="panel-footer">
                                        <span class="pull-left">View Details</span>
                                        <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                        <div class="clearfix"></div>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </sec:authorize>
 					<c:if test="${sessionScope.role=='Administrator'}">  
                         <sec:authorize url="<c:url value='/admin'/>">
                            <div class="col-lg-4 col-md-4"  style="padding-top: 180px;">

                                <div class="panel" style="background-color: #97144d">
                                    <div class="panel-heading">
                                        <div class="row">
                                            <div class="col-xs-3" style="color: floralwhite">
                                                <i class="fa fa-cog fa-fw fa-3x"></i>
                                            </div>
                                            <div class="col-xs-9 text-right">
                                                <div style="color: floralwhite"><h4>RAROC Administrator</h4></div>
                                            </div>
                                        </div>
                                    </div>
                                    <a href="<c:url value='/admin'/>">
                                        <div id="ewiDetails" class="panel-footer">
                                            <span class="pull-left">View Details</span>
                                            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                            <div class="clearfix"></div>
                                        </div>
                                    </a>
                                </div>
                        </sec:authorize>
                    </c:if>
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