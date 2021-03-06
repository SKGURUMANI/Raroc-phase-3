<%-- 
    Document   : rarocAdminFacilityEdit
    Created on : Aug 21, 2014, 1:13:26 PM
    Author     : vinoy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="resources/scripts/config/juiWidgetConfig.js"></script>
<script type="text/javascript" src="resources/scripts/ajax/rarocFacility.js"></script>
<div class="container">
    <div class="filterBox ui-corner-all spacingBottom">
        <div class="breadcrumb"><label><fmt:message key="breadcrumb.raroc.facility.edit"/></label></div>        
    </div>
    <c:url var="curl" value="/rarocAdmin/edit/facility"/>
    <form:form method="POST" commandName="formRarocFacility" action="${curl}" id="rarocFacility" htmlEscape="true">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div id="form-header" class="ui-widget-header ui-corner-top heading">
            <fmt:message key="label.facDetails"/>-<c:out value="${corpName}"/>
        </div>
        <table class="datagrid">            
            <thead>
                <tr>
                    <th>
                        <form:hidden path="refrec"/>
                    </th>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">
                        <th>
                            <c:out value="Facility ${status.count}"/>
                            <form:hidden path="facilities[${status.count-1}].facNo"/>
                        </th>   
                    </c:forEach>
                </tr>
            </thead>
            <tbody>                                    
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.facDesc"/></div>
                        <div title="<fmt:message key="facDesc"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:input cssClass="raroc validate[required]" path="facilities[${status.count-1}].facDesc"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.facType"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:select items="${facType}" itemValue="key" itemLabel="value" cssClass="raroc validate[required]" path="facilities[${status.count-1}].facType"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.astType"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:select items="${assetType}" itemValue="key" itemLabel="value" cssClass="raroc validate[required]" path="facilities[${status.count-1}].astType"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.tRating"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:select items="${tRating}" itemValue="key" itemLabel="value" cssClass="raroc" path="facilities[${status.count-1}].tRating"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.cur"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select items="${currency}" itemValue="key" itemLabel="value" cssClass="cur raroc validate[required]" path="facilities[${status.count-1}].cur"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.amount"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:input cssClass="raroc validate[required,custom[amount],min[0.01]]" path="facilities[${status.count-1}].amount"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.tenure"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:input cssClass="raroc validate[required,custom[number]]" path="facilities[${status.count-1}].tenure"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.exgRate"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:input cssClass="raroc validate[custom[number]]" path="facilities[${status.count-1}].exgRate" value="1"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.restStatus"/></div>
                        <div title="<fmt:message key="restStatus"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:select items="${restStatus}" itemValue="key" itemLabel="value" cssClass="raroc validate[required]" path="facilities[${status.count-1}].restStatus"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.ucicf"/></div>
                        <div title="<fmt:message key="ucicf"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:select cssClass="raroc validate[required]" path="facilities[${status.count-1}].ucicf">
                            <form:option value="Y" label="YES"/>
                            <form:option value="N" label="NO"/>
                        </form:select>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.avgUtil"/></div>
                        <div title="<fmt:message key="avgUtil"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:input cssClass="raroc validate[custom[number],min[0],max[100]]" path="facilities[${status.count-1}].avgUtil"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.inttype"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select cssClass="raroc validate[required]" path="facilities[${status.count-1}].inttype">
                                <form:option value="Floating" label="Floating"/>
                                <form:option value="Fixed" label="Fixed"/>
                            </form:select>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.intRate"/></div>
                        <div title="<fmt:message key="intRate"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:input cssClass="raroc validate[custom[number],min[0],max[100]]" path="facilities[${status.count-1}].intRate"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.costFunds"/></div>
                        <div title="<fmt:message key="costFunds"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:input cssClass="raroc validate[custom[number],min[0],max[100]]" path="facilities[${status.count-1}].costFunds"/>
                    </td>
                    </c:forEach>
                </tr>                
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.aFee"/></div>
                        <div title="<fmt:message key="aFee"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                       <form:input cssClass="raroc validate[custom[number]]" path="facilities[${status.count-1}].aFee"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>
                    <td class="text">                        
                        <div class="floatL"><fmt:message key="label.uFee"/></div>
                        <div title="<fmt:message key="uFee"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:input cssClass="raroc validate[custom[number]]" path="facilities[${status.count-1}].uFee"/>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.cMargin"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:input cssClass="raroc validate[custom[number],min[0],max[100]]" path="facilities[${status.count-1}].cMargin"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <div class="floatL"><fmt:message key="label.cMarginCurr"/></div>
                        <div title="<fmt:message key="cMarginCurr"/>" class="ui-icon ui-icon-help tips floatL"></div>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                    <td class="middle">
                        <form:select cssClass="raroc" path="facilities[${status.count-1}].cMarginCurr">
                            <form:option value="Y" label="Y"/>
                            <form:option value="N" label="N"/>
                        </form:select>
                    </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.ltRating"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select items="${ltRating}" itemValue="key" itemLabel="value" cssClass="raroc" path="facilities[${status.count-1}].ltRating"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.stRating"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select items="${stRating}" itemValue="key" itemLabel="value" cssClass="raroc" path="facilities[${status.count-1}].stRating"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.eGuar"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:input cssClass="raroc validate[custom[number],min[0],max[100]]" path="facilities[${status.count-1}].eGuar"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.guarType"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select items="${guarType}" itemValue="key" itemLabel="value" cssClass="guarType raroc" path="facilities[${status.count-1}].guarType"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.gIntRat"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select items="${guarInt}" itemValue="key" itemLabel="value" cssClass="raroc" path="facilities[${status.count-1}].guarIntRat"/>
                        </td>
                    </c:forEach>
                </tr>
                <tr>            
                    <td class="text">
                        <fmt:message key="label.gExtRat"/>
                    </td>
                    <c:forEach begin="1" end="${facCount}" var="object" varStatus="status">                    
                        <td class="middle">
                            <form:select items="${guarExt}" itemValue="key" itemLabel="value" cssClass="raroc" path="facilities[${status.count-1}].guarExtRat"/>
                        </td>
                    </c:forEach>
                </tr>
            </tbody>
        </table>
        <div style="width:40%" class="spacing">
            <div class="ui-widget-header ui-corner-top heading">
                <fmt:message key="label.othinc"/>
                <div title="<fmt:message key="othinc"/>" class="ui-icon ui-icon-help tips floatR"></div>
            </div>
            <div class="ui-widget-content ui-corner-bottom">
                <table class="formTable">
                    <tr>
                        <td class="forlabel">
                            <fmt:message key="label.bbcdab"/>
                        </td>
                        <td class="field">
                            <form:input cssClass="uniform validate[custom[number]]" path="bbcdab"/>
                        </td>
                    <tr>
                    <tr>
                        <td class="forlabel">
                            <fmt:message key="label.bbfee"/>
                        </td>
                        <td class="field">
                            <form:input cssClass="uniform validate[custom[number]]" path="bbfee"/>
                        </td>
                    <tr>          
                    <tr>
                        <td class="forlabel">
                            <fmt:message key="label.trsry"/>
                        </td>
                        <td class="field">
                            <form:input cssClass="uniform validate[custom[number]]" path="trsry"/>
                        </td>
                    <tr>
                    <tr>
                        <td class="forlabel">
                            <fmt:message key="label.other"/>
                        </td>
                        <td class="field">
                            <form:input cssClass="uniform validate[custom[number]]" path="other"/>
                        </td>
                    <tr>                        
                </table>
            </div>
        </div>
        <div class="cAlign">
            <input type="submit" class="butn" value="Calculate" />
        </div>
    </form:form>
</div>