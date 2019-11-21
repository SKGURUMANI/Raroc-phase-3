<%-- 
    Document   : navbar
    Created on : Apr 25, 2016, 2:21:42 PM
    Author     : Vinoy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/HoldOn.min.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/HoldOn.min.js"/>"></script>
<!--lock screen implementation-->
<script>
    $(document).ready(function () {

        var options = {
            theme: "sk-cube-grid",
            message: 'Screen has been locked. <br> <input value="Unlock" class="btn btn-default" onclick="HoldOn.close();" type="button">',
            backgroundColor: "#ff4d82",
            textColor: "white"
        };

        var time = 60000;
        var idleTimer = null;
        $('*').bind('mousemove click mouseup mousedown keydown keypress keyup submit change mouseenter scroll resize dblclick', function () {
            clearTimeout(idleTimer);
            idleTimer = setTimeout(function () {
                lockScreen();
            }, time);
        });
        $("body").trigger("mousemove");

        function lockScreen() {
            if (!($("#holdon-overlay").length)) {
                HoldOn.open(options);
            }
        }
    });
</script>
<style type="text/css">
    .iconCss {
        height: 30px;
        width: 30px;
    }

    .caret {
        border: 5px solid transparent;
        display: inline-block;
        width: 0;
        height: 0;
        opacity: 0.5;
        vertical-align: top;
    }

    .caret.up {
        border-bottom: 5px solid;
    }

    .caret.right {
        border-left: 5px solid;
        vertical-align: middle;
    }

    .caret.down {
        border-top: 5px solid;
    }

    .caret.left {
        border-right: 5px solid;
    }

    .childMenu:hover>ul.submenu {
        display: block;
    }

    .dropdown-submenu>.dropdown-menu {
        padding-bottom: 100px 50px;
        top: 0;
        left: 100%;
        margin-top: -6px;
        margin-left: -1px;
    }

    .childMenu {
        position: relative;
    }
</style>
<!-- Navigation -->
<nav class="navbar navbar-axis navbar-default navbar-static-top" role="navigation">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse"
                data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span>
            <span class="icon-bar"></span> <span class="icon-bar"></span>
        </button>
    </div>
    <!-- /.navbar-header -->
    <%-- <div class="col-lg-6 hidden-sm hidden-xs help-block">
        <div class="col-lg-12">
            <small>${sessionScope.username} (${sessionScope.userroles})</small>
        </div>
        <div class="col-lg-12">
            <small>Last Login: ${sessionScope.lastLogin}</small>
        </div>
    </div> --%>
    <ul class="nav navbar-top-links navbar-right">
        <li class="dropdown">
            <small  style="color: #fff;">Last Login: ${sessionScope.lastLogin}</small>
         </li>
        <li class="dropdown"><a
                href="<c:url value='${sessionScope.homepage}'/>"> <i
                    class="fa fa-home fa-fw"></i> Home
            </a></li>

        <sec:authorize url="/admin">
            <li class="dropdown"><a href="<c:url value='/admin'/>"> <i
                        class="fa fa-cog fa-fw"></i> Admin
                </a></li>
            </sec:authorize>
        <li class="dropdown"><a href="#"> <i
                    class="fa fa-question-circle fa-fw"></i> Help
            </a></li>
        <li class="dropdown">
            <form id="logout" action="<c:url value="/logout" />" method="post">
                <input type="hidden" name="${_csrf.parameterName}"
                       value="${_csrf.token}" />
            </form> <a href="#" onclick="document.getElementById('logout').submit()">
                <i class="fa fa-sign-out fa-fw"></i> Log Out
            </a>
        </li>
    </ul>
    <c:if test="${not empty menu}">
        <c:choose>
            <c:when test="${menu == 'reports'}">
                <div class="navbar sidebar-tree" role="navigation">
                    <div id="reportTree"></div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="navbar-default sidebar" role="navigation">
                    <div class="sidebar-nav navbar-collapse">
                        <ul class="nav" id="side-menu">
                            <c:if test="${menu == 'admin'}">
                                <li><a href="javascript:void(0)"><i
                                            class="fa fa-wrench fa-fw"></i> <fmt:message
                                            key="label.admin.userman" /><span class="fa arrow"></span></a>
                                    <ul class="nav nav-second-level">
                                        <li><a href="<c:url value='/admin/create'/>"
                                               class="ajaxNav"><fmt:message key="label.admin.create" /></a></li>
                                        <li><a href="<c:url value='/admin/modify'/>"
                                               class="ajaxNav"><fmt:message key="label.admin.modify" /></a></li>
                                        <li><a href="<c:url value='/admin/sack'/>"
                                               class="ajaxNav"><fmt:message key="label.admin.sack" /></a></li>
                                    </ul></li>
                                <li><a href="javascript:void(0)"><i
                                            class="fa fa-flag fa-fw"></i> <fmt:message
                                            key="label.admin.audit" /><span class="fa arrow"></span></a>
                                    <ul class="nav nav-second-level">
                                        <li><a href="<c:url value='admin/sysLog'/>"
                                               class="ajaxNav"><fmt:message key="label.admin.sys" /></a></li>
                                        <li><a href="<c:url value='admin/opsLog'/>"
                                               class="ajaxNav"><fmt:message key="label.admin.ops" /></a></li>
                                        <li><a href="<c:url value='admin/prsLog'/>"
                                               class="ajaxNav"><fmt:message key="label.admin.prs" /></a></li>
                                    </ul></li>
<!--                                <li><a href="<c:url value='/admin/defrag'/>"
                                       class="ajaxNav"><i class="fa fa-eraser fa-fw"></i> <fmt:message
                                            key="label.admin.defrag" /></a></li>-->
                                    </c:if>
                                    <c:if test="${menu == 'operations'}">
                                <li><a href="<c:url value='operations/upload'/>"
                                       class="ajaxNav"><i class="fa fa-upload fa-fw"></i> Data
                                        Loading</a></li>
                                <li><a href="<c:url value='job/create'/>" class="ajaxNav"><i
                                            class="fa fa-pencil fa-fw"></i> Design Job</a></li>
                                <li><a href="<c:url value='job/execute'/>" class="ajaxNav"><i
                                            class="fa fa-bolt fa-fw"></i> Execute Job</a></li>
                                <li><a href="<c:url value='job/schedule/list'/>"
                                       class="ajaxNav"><i class="fa fa-clock-o fa-fw"></i>
                                        Schedule Job</a></li>
                                <li><a href="javascript:void(0)"><i
                                            class="fa fa-database fa-fw"></i> Data Management<span
                                            class="fa arrow"></span></a>
                                    <ul class="nav nav-second-level">
                                        <li><a href="<c:url value='operations/result/temp'/>"
                                               class="ajaxNav"><i class="fa fa-table fa-fw"></i>
                                                Temporary Results</a></li>
                                        <li><a href="<c:url value='operations/result/final'/>"
                                               class="ajaxNav"><i class="fa fa-table fa-fw"></i> Final
                                                Results</a></li>
                                        <li><a href="<c:url value='operations/move'/>"
                                               class="ajaxNav"><i class="fa fa-exchange fa-fw"></i> Move
                                                Data</a></li>
                                    </ul></li>
                                <li><a href="<c:url value='operations/mail'/>"
                                       class="ajaxNav"><i class="fa fa-envelope fa-fw"></i> Mail
                                        Alerts</a></li>
                                <li><a href="javascript:void(0)"><i
                                            class="fa fa-desktop fa-fw"></i> Monitor Job<span
                                            class="fa arrow"></span></a>
                                    <ul class="nav nav-second-level">
                                        <li><a href="<c:url value='job/monitor'/>"
                                               class="ajaxNav"><i class="fa fa-search fa-fw"></i> Job
                                                Monitor</a></li>
                                        <li><a
                                                href="<c:url value='operations/etl/dataLoadStatus'/>"
                                                class="ajaxNav"><i class="fa fa-list-alt fa-fw"></i>
                                                Loading Status</a></li>
                                        <li><a
                                                href="<c:url value='operations/etl/dataLoadLogs'/>"
                                                class="ajaxNav"><i class="fa fa-sticky-note fa-fw"></i>
                                                Loading Logs</a></li>
                                    </ul></li>
                                </c:if>
                                <c:if test="${menu == 'thresholds'}">
                                <li><a class="ajaxNav"
                                       href="<c:url value="/thresholds/etl"/>"><i
                                            class="fa fa-filter fa-fw"></i> ETL Thresholds</a></li>
                                <li><a class="ajaxNav"
                                       href="<c:url value="/thresholds/incident"/>"><i
                                            class="fa fa-exclamation-triangle fa-fw"></i> Incident
                                        Thresholds</a></li>
                                <li><a class="ajaxNav"
                                       href="<c:url value="/thresholds/alert"/>"><i
                                            class="fa fa-bell fa-fw"></i> Alert Thresholds</a></li>
                                <li><a class="ajaxNav"
                                       href="<c:url value="/thresholds/model"/>"><i
                                            class="fa fa-bell fa-fw"></i> Model Weights</a></li>
                                    </c:if>
                        </ul>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>
</nav>
    <nav class="navbar  navbar-fixed-top"  style="top: 28px;">
	       
	        <div class="navbar-header nav-logo-curve">
	          <div class="LogoPart">
	            <a class="navbar-brand logo"></a>
	          </div>
	          <div class="logoCurve" style="height: 70px;"></div>
	        </div>
	        <div id="navbar" class="navbar-collapse collapse menu-container">
			     <div id="navbar" class="navbar-collapse collapse menu-container">
		        	<div class="panel panel-breadcrumb" style="height: 84px;border-left: none;-webkit-box-shadow: 0 1px rgba(0,0,0,0.3);">
                         <div class="panel-body">
						    <%-- <ul class="nav navbar-nav">
							    <li><a href="<c:url value='${sessionScope.homepage}'/>">Home</a></li>
							     <li class="dropdown">
								      <a href="#">Admin</a>
									     <div class="dropdown-content">
										    <a class="dropdown" href="#">User Management >></a>
										    	<div class="dropdown-content">
												    <a href="#">User Management</a>
												    <a href="#">Audit Log</a>
										  		</div>
										    <a href="#">Audit Logs >></a>
										  </div>
							     </li>
							     <li><a href="#">Help</a></li>
							 </ul>
 --%>                          
							 <ul class="nav navbar-nav">
							  <li><a href="#">Home</a></li>
							  <li><a href="#">Admin</a>
							    <ul>
							      <li><a href="#">HTML</a></li>
							      <li><a href="#">CSS</a>
							        <ul>
							          <li><a href="#">Resets</a></li>
							          <li><a href="#">Grids</a></li>
							          <li><a href="#">Frameworks</a></li>
							        </ul>
							      </li>
							      <li><a href="#">JavaScript</a>
							        <ul>
							          <li><a href="#">Ajax</a></li>
							          <li><a href="#">jQuery</a></li>
							        </ul>
							      </li>
							    </ul>
							  </li>
							  <li><a href="#">Help</a></li>
							</ul>
						</div>
                    </div> 
	       	 	</div>
	        </div>
      </nav>