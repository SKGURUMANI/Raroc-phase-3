<%-- 
    Document   : rarocAdminMasterEdit
    Created on : Aug 21, 2014, 1:13:10 PM
    Author     : vinoy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="resources/scripts/config/juiWidgetConfig.js"></script>
<script type="text/javascript" src="resources/scripts/ajax/rarocMasterAdmin.js"></script>
<div class="container">
    <div class="filterBox ui-corner-all spacingBottom">
        <div class="breadcrumb"><label><fmt:message key="breadcrumb.raroc.master.edit"/></label></div>
    </div>
    <c:url var="curl" value="/rarocAdmin/edit"/>
    <form:form method="POST" commandName="formRarocMaster" action="${curl}" id="rarocMaster" htmlEscape="true">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div id="form-header" class="ui-widget-header ui-corner-top heading">
            <div class="formIcon">
                <a class="tabVisible" id="form-butn" href="javascript:void(0);"></a>
            </div>
            <fmt:message key="label.rarocMaster"/>
        </div>
        <div id="form-body" class="widget-content ui-corner-bottom spacingForm">
            <table class="formTable">
                <tr>
                    <td class="forlabel">
                        <fmt:message key="label.cname"/>
                    </td>
                    <td class="field">
                        <form:hidden path="rarocref"/>
                        <form:input type="text" cssClass="validate[required] uniform" readonly="true" path="cname"/>                        
                    </td>
                    <td class="forlabel">
                        <fmt:message key="label.cid"/>
                    </td>
                    <td class="field">
                        <form:input type="text" readonly="true" cssClass="validate[minSize[9],maxSize[9],custom[integer]] uniform" path="cid"/>
                    </td>
                </tr>
                <tr>
                    <td class="forlabel">
                        <fmt:message key="label.bussunit"/>
                    </td>
                    <td class="field">
                        <form:select items="${bu}" itemLabel="value" itemValue="key" cssClass="validate[required] uniform" path="bussunit">                            
                        </form:select>
                    </td>
                    <td class="forlabel">
                        <fmt:message key="label.facility"/>
                    </td>
                    <td class="field">
                        <form:input type="text" readonly="true" cssClass="validate[required] uniform" path="facility"/>
                    </td>
                </tr>
                <tr>
                    <td class="forlabel">
                        <fmt:message key="label.ebid"/>
                    </td>
                    <td class="field">
                        <form:input cssClass="validate[required] uniform" path="ebid"/>
                    </td>
                    <td class="forlabel">
                        <fmt:message key="label.ufce"/>
                    </td>
                    <td class="field">
                        <form:input cssClass="validate[required] uniform" path="ufce"/>
                    </td>                    
                </tr>
                <tr>
                    <td class="forlabel">
                        <fmt:message key="label.rtool"/>
                    </td>
                    <td class="field">
                        <form:select items="${rtool}" itemLabel="value" itemValue="key" cssClass="validate[required] uniform" path="rtool">                            
                        </form:select>
                    </td>
                    <td class="forlabel">
                        <fmt:message key="label.intRat"/>
                    </td>
                    <td class="field">
                        <form:select items="${rcode}" itemLabel="value" itemValue="key" cssClass="validate[required] uniform" path="intRat"></form:select>
                    </td>
                </tr>
                <tr>
                    <td class="forlabel">
                        <div class="floatL"><fmt:message key="label.rid"/></div>
                        <div title="<fmt:message key="rid"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <td class="field">
                        <form:input cssClass="validate[required] uniform"  readonly="true" path="rid"/>
                    </td>
                    <td class="forlabel">
                        <fmt:message key="label.ind"/>
                    </td>
                    <td class="field">
                        <form:select items="${ind}" itemLabel="value" itemValue="key" cssClass="validate[required] uniform" path="ind">                            
                        </form:select>
                    </td>
                </tr>
                <tr>                    
                    <td colspan="4"><input type="submit" value="Next" class="butn uniform"/></td>
                </tr>
            </table>
        </div>
    </form:form>
</div>
<c:import url="/WEB-INF/views/layout/footer.jsp" />
