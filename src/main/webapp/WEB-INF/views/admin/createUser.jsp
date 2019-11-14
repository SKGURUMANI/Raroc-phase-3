<%-- 
    Document   : createUser
    Created on : 19 Jun, 2013, 1:24:45 AM
    Author     : vaio
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/ajax/admin.js"/>"></script>
<div class="col-lg-12 col-md-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            <fmt:message key="breadcrumb.createUser"/>
        </div>

    </div>
    <div class="panel panel-default">
        <div class="panel-body no-gutters">
            <c:url var="curl" value="/admin/create"/>
            <form:form  commandName="userModel" action="${curl}" id="formAdmin" autocomplete="off" htmlEscape="true">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <form:hidden path="passChangeDate" value="01-01-2018"/>                
                <form:hidden path="homePage" value="${homePage}"/>
                <div class="col-lg-6 col-md-6">
                    <div class="form-group">
                        <label for="userId" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.userid"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[required,maxSize[20]] form-control" path="userId"/>
                        </div>
                    </div>
                    <br/><br/>
                    <c:if test="${authType == 'DB'}">
                        <div class="form-group">
                            <label for="password" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.passwd"/></label>
                            <div class="col-lg-8 col-md-8">
                                <form:input cssClass="validate[required,maxSize[20]] form-control" path="password"/>
                            </div>
                        </div>
                        <br/><br/>
                    </c:if>    
                    <c:if test="${authType == 'LDAP'}">
                        <form:hidden path="password" value="Dummy#123"/>
                    </c:if>   

                    <div class="form-group">
                        <label for="active" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.active"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select cssClass="form-control" path="active">                            
                                <form:option value="0" label="No"/>
                                <form:option value="1" label="Yes"/>
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>    
                    <div class="form-group">
                        <label for="email" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.email"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[maxSize[100],custom[email]] form-control" path="email"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="unit" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.unit"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select cssClass="form-control" path="unit">
                                <form:option value="1" label="actuals"/>
                                <form:option value="100000" label="lacs"/>
                                <form:option value="1000000" label="millions"/>
                                <form:option value="10000000" label="crores"/>
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="sessionTime" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.session"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[required,custom[integer]] form-control" path="sessionTime"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="department" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.department"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[required] form-control" path="department"/>
                        </div>
                    </div> 
                    <br/><br/>
                    <div class="form-group">
                        <label for="address" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.address"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:textarea rows="2" cssClass="validate[maxSize[200]] form-control" path="address"/>
                        </div>
                    </div>
                </div>                    

                <div class="col-lg-6 col-md-6">
                    <div class="form-group">
                        <label for="userName" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.userName"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[required,maxSize[50]] form-control" path="userName"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="enabled" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.enabled"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select cssClass="form-control" path="enabled">
                                <form:option value="0" label="No"/>
                                <form:option value="1" label="Yes"/>
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="roles" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.roles"/></label>
                        <div class="col-lg-8 col-md-8 btn-group">
                            <form:select multiple="true" cssClass="validate[required] form-control" path="roles">
                                <form:option value="ROLE_USER" label="Risk User"/>
                                <form:option value="ROLE_RAROC_CORP" label="RAROC Corp"/>
                                <form:option value="ROLE_RAROC_AUTH" label="RAROC Authorizer"/>
                                <form:option value="ROLE_ADMIN" label="Administrator"/>
                                <form:option value="ROLE_OPERATOR" label="Operator"/>
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="locale" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.locale"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select cssClass="form-control" path="locale">
                                <form:options items="${locale}" />
                            </form:select>
                        </div>
                    </div>    
                    <br/><br/>
                    <div class="form-group">
                        <label for="phone" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.phone"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[maxSize[20],custom[onlyNumberSp]] form-control" path="phone"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="failedAttempt" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.failedAttempt"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input cssClass="validate[required,custom[integer]] form-control" path="failedAttempt"/>
                        </div>
                    </div>  
                    <c:if test="${authType == 'DB'}">
                        <div class="form-group">
                            <label for="passExpiry" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.passExpiry"/></label>
                            <div class="col-lg-8 col-md-8">
                                <form:input cssClass="validate[required,custom[integer]] form-control" path="passExpiry"/>
                            </div>
                        </div>
                        <br/><br/>
                    </c:if>    
                    <c:if test="${authType == 'LDAP'}">
                        <form:hidden path="passExpiry" value="${passExpiry}"/>
                    </c:if>
                </div>
                <div class="col-lg-12 col-md-12">
                    <div class="marginTop text-center">
                        <button type="submit" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing...">Submit</button>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>