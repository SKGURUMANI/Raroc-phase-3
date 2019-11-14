<%-- 
    Document   : createUser
    Created on : 19 Jun, 2013, 1:24:45 AM
    Author     : vaio
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/raroc/rarocFacilityGrid.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/raroc/rarocFacilityGrid_nfb.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/grid/raroc/rarocFacilityGrid_bonds.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/ajax/rarocFacility.js"/>"></script>
<script>
    $('[data-toggle="tooltip"]').tooltip();
</script>
<style>
    .col-sm-6 {
        width: 40%;
    }
</style>
<div class="col-lg-12 col-md-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            <fmt:message key="breadcrumb.raroc.master"/>
        </div>
    </div>
    <c:url var="curl" value="/raroc/new/facility"/>
    <form:form method="POST" commandName="formRarocFacility" action="${curl}" id="rarocFacility" htmlEscape="true">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>    
        <div class="panel panel-default">
            <div class="panel-heading">
                <a href="#"><em><fmt:message key="label.facDetails"/> -  <c:out value="${corpName}"/></em></a>
                <span class="pull-right clickable "><i class="glyphicon glyphicon-chevron-down"></i></span>
            </div>
            <ul class="nav nav-tabs" id="mytabs">
                <li class="active"><a data-toggle="tab" href="#home">Fund Base</a></li>
                <li><a data-toggle="tab" href="#menu1">Non Fund Base</a></li>
                <li><a data-toggle="tab" href="#menu2">Bonds</a></li>
                <li><a data-toggle="tab" href="#menu3">Derivatives</a></li>
            </ul>
            <div class="tab-content">
                <div id="home" class="tab-pane fade in active">
                    <div class="panel-body no-gutters">
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <form:hidden path="refrec" id="refId"/>
                                <input type="text" class="hidden" value="${expBanks}"  id="exBank">
                                <label  style="padding-top: 8px;" for="facDesc" class="control-label col-sm-6"><fmt:message key="label.facDesc"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="facDesc"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required]" path="facDesc" id="facDesc"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="tRating" class="control-label col-sm-6"><fmt:message key="label.tRating"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${tRating}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" id="tRating" path="tRating"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="avgfy" class="control-label col-sm-6"><fmt:message key="label.avgfy"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required,min[0]]" path="avgfy" id="avgfy"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="restStatus" class="control-label col-sm-6"><fmt:message key="label.restStatus"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="restStatus"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${restStatus}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required]" path="restStatus" id="restStatus"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="tenure" class="control-label col-sm-6"><fmt:message key="label.tenure"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required] tenure" path="tenure" id="tenure"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="astType" class="control-label col-sm-6"><fmt:message key="label.astType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${assetType}" itemValue="key" itemLabel="value" id="astType" cssClass="form-control form-control-sm validate[required] astType" path="astType"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="exgRate" class="control-label col-sm-6"><fmt:message key="label.exgRate"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]] exgRate" path="exgRate" id="exgRate" value="1"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="intRate" class="control-label col-sm-6"><fmt:message key="label.intRate"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="intRate"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]] intRate" path="intRate" id="intRate"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="synFee" class="control-label col-sm-6"><fmt:message key="label.synFee"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]] synFee" path="synFee" id="synFee" value="0"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cMargin" class="control-label col-sm-6"><fmt:message key="label.cMargin"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number],min[0],max[100]] cMargin" value="0" path="cMargin" id="cMargin"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="eGuar" class="control-label col-sm-6"><fmt:message key="label.eGuar"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number],min[0],max[100]] eGuar" value="0" path="eGuar" id="eGuar"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="ltRating" class="control-label col-sm-6"><fmt:message key="label.ltRating"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <input type="hidden" id="ltbank" value="${stVisibility}"/>
                                    <form:select items="${ltRating}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="ltRating" id="ltRating"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="giftCity" class="control-label col-sm-6"><fmt:message key="label.giftCity"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="giftCity" id="giftCity">
                                        <form:option value="NO" label="NO"/>
                                        <form:option value="YES" label="YES"/>                       
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="facType" class="control-label col-sm-6"><fmt:message key="label.facType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${facType}" itemValue="key" itemLabel="value" id="facTypes" cssClass="form-control form-control-sm validate[required] facType" path="facType"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="curfy" class="control-label col-sm-6"><fmt:message key="label.curfy"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm" path="curfy"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="avgnextfy" class="control-label col-sm-6"><fmt:message key="label.avgnextfy"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required,min[0]]" path="avgnextfy" id="avgnextfy"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="oridate" class="control-label col-sm-6"><fmt:message key="label.oridate"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm datepicker validate[required]" path="oridate" id="oridate"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="inttype" class="control-label col-sm-6"><fmt:message key="label.inttype"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required] inttype" path="inttype" id="inttype">
                                        <form:option value="" label="Select..."/>
                                        <form:option value="Floating" label="Floating"/>
                                        <form:option value="Fixed" label="Fixed"/>
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="nature" class="control-label col-sm-6"><fmt:message key="label.nature"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="nature"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${psl}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] psl" path="psl" id="psl"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="refreq" class="control-label col-sm-6"><fmt:message key="label.refreq"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${reFreq}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] refreq" path="refreq" id="refreq"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="region" class="control-label col-sm-6"><fmt:message key="label.region"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="region"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm" path="region" id="region">
                                        <form:option value="" label="Select..."/>
                                        <form:option value="India" label="India"/>
                                        <form:option value="Overseas" label="Overseas"/>
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cSecured" class="control-label col-sm-6"><fmt:message key="label.cSecured"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="cSecured" id="cSecured">
                                        <form:option value="N" label="NO"/>
                                        <form:option value="Y" label="YES"/>                       
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cMarginCurr" class="control-label col-sm-6"><fmt:message key="label.cMarginCurr"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="cMarginCurr"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${finSecurity}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="cMarginCurr" id="cMarginCurr"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="gExtRat" class="control-label col-sm-6"><fmt:message key="label.gExtRat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${guarExt}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="guarExtRat" id="guarExtRat"/>
                                </div>
                            </div>
                            <br/><br/>
                            <c:if test="${stVisibility == 'Y'}">
                                <div class="form-group">
                                    <label  style="padding-top: 8px;" for="stRating" class="control-label col-sm-6"><fmt:message key="label.stRating"/></label>
                                    <div class="col-lg-6 col-md-6" style="width: 60%;">
                                        <form:select items="${stRating}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="stRating" id="stRating"/>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="amount" class="control-label col-sm-6"><fmt:message key="label.amount"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount],min[0.01]] form-control form-control-sm" path="amount"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="nextfy" class="control-label col-sm-6"><fmt:message key="label.nextfy"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm" path="nextfy" id="nextfy"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="ucicf" class="control-label col-sm-6"><fmt:message key="label.ucicf"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="ucicf"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required] ucicf" path="ucicf" id="ucicf">
                                        <form:option value="N" label="NO"/>
                                        <form:option value="Y" label="YES"/>                            
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="oriMat" class="control-label col-sm-6"><fmt:message key="label.oriMat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${maturity}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] maturity" path="maturity" id="maturity"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="aFee" class="control-label col-sm-6"><fmt:message key="label.aFee"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="aFee"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm" path="aFee" id="aFee"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cur" class="control-label col-sm-6"><fmt:message key="label.cur"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${currency}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] cur" path="cur" id="cur"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="hidden">
                                <label  style="padding-top: 8px;" for="benchmark" class="control-label col-sm-6"><fmt:message key="label.benchmark"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${benchmark}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm  validate[required]" path="benchmark" id="benchmark">
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="uFee" class="control-label col-sm-6"><fmt:message key="label.uFee"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="uFee"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required,custom[amount]]" path="uFee" id="uFee"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="costFunds" class="control-label col-sm-6"><fmt:message key="label.costFunds"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required,custom[amount]" path="costFunds" id="costFunds"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="guarType" class="control-label col-sm-6"><fmt:message key="label.guarType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${guarType}" itemValue="key" itemLabel="value" cssClass="guarType form-control form-control-sm" path="guarType" id="guarType"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="gIntRat" class="control-label col-sm-6"><fmt:message key="label.gIntRat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${guarInt}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="guarIntRat" id="guarIntRat"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="extRated" class="control-label col-sm-6"><fmt:message key="label.extRated"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm" path="extRated" id="extRated">
                                        <form:option value="" label="Select..."/>
                                        <form:option value="Yes" label="Yes"/>
                                        <form:option value="No" label="No"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <br/>
                        <form:input cssClass="hidden" path="bbcdab" value="${bbcdab}"/>
                        <form:input cssClass="hidden" path="tdfee"  value="${tdfee}"/>
                        <form:input cssClass="hidden" path="forex"  value="${forex}"/>
                        <form:input cssClass="hidden" path="cafee"  value="${cafee}"/>
                        <form:input cssClass="hidden" path="cms"  value="${cms}"/>
                        <form:input cssClass="hidden" path="other"  value="${other}"/>
                        <form:input cssClass="hidden" path="facNo" id="facNo"/>
                        <input type="text" class="hidden" value="Add" id="butnId"/>
                        <div class="col-lg-12 col-md-12 text-center">
                            <div class="marginTop" style="align-content: center">
                                <input type="submit" id="butnA" class="btn btn-default" value="Add Facility"/>
                                <input type="submit" id="butnM" class="btn btn-default" value="Modify Facility"/>
                                <input type="button" id="rSet" class="btn btn-default" value="Reset"/>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
            <div id="menu1" class="tab-pane fade">
                <c:url var="curl_1" value="/raroc/new/facility/nfb"/>
                <form:form method="POST" commandName="formRarocFacility" action="${curl_1}" id="rarocFacility_nfb" htmlEscape="true">
                    <div class="panel-body no-gutters">
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="facDesc" class="control-label col-sm-6"><fmt:message key="label.facDesc"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:hidden path="refrec" id="refId"/>
                                    <form:input type="text" cssClass="form-control form-control-sm" path="facDesc" id="facDesc_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="tRating" class="control-label col-sm-6"><fmt:message key="label.tRating"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${tRating}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" id="tRating_nfb" path="tRating"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="ucicf" class="control-label col-sm-6"><fmt:message key="label.ucicf"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="ucicf" id="ucicf_nfb">
                                        <form:option value="N" label="NO"/>
                                        <form:option value="Y" label="YES"/>                            
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="avgMat" class="control-label col-sm-6"><fmt:message key="label.avgMat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm" path="avgMat" id="avgMat"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="exgRate" class="control-label col-sm-6"><fmt:message key="label.exgRate"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]]" path="exgRate" id="exgRate_nfb" value="1"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="synFee" class="control-label col-sm-6"><fmt:message key="label.synFee"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]]" path="synFee" id="synFee_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cMargin" class="control-label col-sm-6"><fmt:message key="label.cMargin"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number],min[0],max[100]]" value="0" path="cMargin" id="cMargin_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="eGuar" class="control-label col-sm-6"><fmt:message key="label.eGuar"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number],min[0],max[100]]" value="0" path="eGuar" id="eGuar_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="ltRating" class="control-label col-sm-6"><fmt:message key="label.ltRating"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <input type="hidden" id="ltbank" value="${stVisibility}"/>
                                    <form:select items="${ltRating}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="ltRating" id="ltRating_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="giftCity" class="control-label col-sm-6"><fmt:message key="label.giftCity"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="giftCity" id="giftCity_nfb">
                                        <form:option value="NO" label="NO"/>
                                        <form:option value="YES" label="YES"/>                       
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="facType" class="control-label col-sm-6"><fmt:message key="label.facType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${facType_nfb}" itemValue="key" itemLabel="value" id="facTypes_nfb" cssClass="form-control form-control-sm validate[required] facType" path="facType"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="avgfy" class="control-label col-sm-6"><fmt:message key="label.avgfy"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required,min[0]]" path="avgfy" id="avgfy_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="restStatus" class="control-label col-sm-6"><fmt:message key="label.restStatus"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${restStatus}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required]" path="restStatus" id="restStatus_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="astType" class="control-label col-sm-6"><fmt:message key="label.astType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${assetType}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] astType" path="astType" id="astType_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="commis" class="control-label col-sm-6"><fmt:message key="label.commis"/>

                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]]" path="intRate" id="intRate_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cSecured" class="control-label col-sm-6"><fmt:message key="label.cSecured"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="cSecured" id="cSecured_nfb">
                                        <form:option value="N" label="NO"/>
                                        <form:option value="Y" label="YES"/>                       
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cMarginCurr" class="control-label col-sm-6"><fmt:message key="label.cMarginCurr"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${finSecurity}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="cMarginCurr" id="cMarginCurr_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="gExtRat" class="control-label col-sm-6"><fmt:message key="label.gExtRat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${guarExt}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm guarExtRat" path="guarExtRat" id="guarExtRat_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <c:if test="${stVisibility == 'Y'}">
                                <div class="form-group">
                                    <label  style="padding-top: 8px;" for="stRating" class="control-label col-sm-6"><fmt:message key="label.stRating"/></label>
                                    <div class="col-lg-6 col-md-6" style="width: 60%;">
                                        <form:select items="${stRating}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm" path="stRating" id="stRating_nfb"/>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="amount" class="control-label col-sm-6"><fmt:message key="label.amount"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount],min[0.01]] form-control form-control-sm" path="amount" id="amount_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="avgnextfy" class="control-label col-sm-6"><fmt:message key="label.avgnextfy"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[required,min[0]]" path="avgnextfy" id="avgnextfy_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="oriMat" class="control-label col-sm-6"><fmt:message key="label.oriMat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${maturity}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] maturity" path="maturity" id="maturity_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cur" class="control-label col-sm-6"><fmt:message key="label.cur"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${currency}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required]" path="cur" id="cur_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="uFee" class="control-label col-sm-6"><fmt:message key="label.uFee"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]]" path="uFee" id="uFee_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="region" class="control-label col-sm-6"><fmt:message key="label.region"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm" path="region" id="region_nfb">
                                        <form:option value="India" label="India"/>
                                        <form:option value="Overseas" label="Overseas"/>
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="guarType" class="control-label col-sm-6"><fmt:message key="label.guarType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${guarType}" itemValue="key" itemLabel="value" cssClass="guarType form-control form-control-sm" path="guarType" id="guarType_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="gIntRat" class="control-label col-sm-6"><fmt:message key="label.gIntRat"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${guarInt}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm guarIntRat" path="guarIntRat" id="guarIntRat_nfb"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="extRated" class="control-label col-sm-6"><fmt:message key="label.extRated"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm" path="extRated" id="extRated_nfb">
                                        <form:option value="" label="Select..."/>
                                        <form:option value="Yes" label="Yes"/>
                                        <form:option value="No" label="No"/>
                                    </form:select>
                                </div>
                            </div>
                            <form:input cssClass="hidden" path="bbcdab" value="${bbcdab}"/>
                            <form:input cssClass="hidden" path="tdfee"  value="${tdfee}"/>
                            <form:input cssClass="hidden" path="forex"  value="${forex}"/>
                            <form:input cssClass="hidden" path="cafee"  value="${cafee}"/>
                            <form:input cssClass="hidden" path="cms"  value="${cms}"/>
                            <form:input cssClass="hidden" path="other"  value="${other}"/>
                            <form:input cssClass="hidden" path="facNo" id="facNo_nfb"/>
                            <input type="text" class="hidden" value="Add" id="butnId_nfb"/>
                        </div>
                        <br/>
                        <div class="col-lg-12 col-md-12 text-center">
                            <div class="marginTop" style="align-content: center">
                                <button type="submit" id="butnA_nfb" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing...">Add Facility</button>
                                <input type="submit" id="butnM_nfb" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing..." value="Modify Facility"/>
                                <input type="button" id="rSet_nfb" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing..." value="Reset"/>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
            <div id="menu2" class="tab-pane fade">
                <c:url var="curl_2" value="/raroc/new/facility/bonds"/>
                <form:form method="POST" commandName="formRarocFacility" action="${curl_2}" id="rarocFacility_bonds" htmlEscape="true">
                    <div class="panel-body no-gutters">
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="facDesc" class="control-label col-sm-6"><fmt:message key="label.facDesc"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:hidden path="refrec" id="refId"/>
                                    <form:input type="text" cssClass="form-control form-control-sm" path="facDesc" id="facDesc_bonds"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="bookType" class="control-label col-sm-6"><fmt:message key="label.bookType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="bookType" id="bookType">
                                        <form:option value="" label="Select..."/>
                                        <form:option value="AFS" label="AFS"/>
                                        <form:option value="HFT" label="HFT"/>                            
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="coupon" class="control-label col-sm-6"><fmt:message key="label.coupon"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="coupon" id="coupon">
                                        <form:option value="" label="Select..."/>
                                        <form:option value="Monthly" label="Monthly"/>
                                        <form:option value="Half-yearly" label="Half-yearly"/>
                                        <form:option value="Annually" label="Annually"/>
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="tentative" class="control-label col-sm-6"><fmt:message key="label.tentative"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount]] form-control form-control-sm" path="tentative" id="tentative"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="lcr" class="control-label col-sm-6"><fmt:message key="label.lcr"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm validate[required]" path="lcr" id="lcr">
                                        <form:option value="No" label="No"/>
                                        <form:option value="Yes" label="Yes"/>                            
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cur" class="control-label col-sm-6"><fmt:message key="label.cur"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${currency}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required]" path="cur" id="cur_bonds"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="facType" class="control-label col-sm-6"><fmt:message key="label.facType"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${facType_bonds}" itemValue="key" itemLabel="value" id="facTypes_bonds" cssClass="form-control form-control-sm validate[required] facType" path="facType" />
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="rTenure" class="control-label col-sm-6"><fmt:message key="label.rTenure"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount]] form-control form-control-sm" path="rTenure" id="rTenure"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="bondTenure" class="control-label col-sm-6"><fmt:message key="label.bondTenure"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount]] form-control form-control-sm" path="bondTenure" id="bondTenure"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="region" class="control-label col-sm-6"><fmt:message key="label.region"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select cssClass="form-control form-control-sm" path="region" id="region_bonds">
                                        <form:option value="India" label="India"/>
                                        <form:option value="Overseas" label="Overseas"/>
                                    </form:select>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="upfrontFee" class="control-label col-sm-6"><fmt:message key="label.upfrontFee"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount]] form-control form-control-sm" path="upfrontFee" id="uFee_bonds"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="exgRate" class="control-label col-sm-6"><fmt:message key="label.exgRate"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="form-control form-control-sm validate[custom[number]]" path="exgRate" id="exgRate_bonds" value="1"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4">
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="amount" class="control-label col-sm-6"><fmt:message key="label.amount"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount]] form-control form-control-sm" path="amount" id="amount_bonds"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="yeild" class="control-label col-sm-6"><fmt:message key="label.yeild"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount],min[0.01]] form-control form-control-sm" path="yeild" id="yeild"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="couponBond" class="control-label col-sm-6"><fmt:message key="label.couponBond"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required,custom[amount]] form-control form-control-sm" path="couponBond" id="couponBond"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="cetExt" class="control-label col-sm-6"><fmt:message key="label.cetExt"/>
                                    <span class="">
                                        <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="cet"/>">
                                            <i class="fa fa-default fa-1x fa-question"></i>
                                        </a>
                                    </span>
                                </label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:select items="${bondRat}" itemValue="key" itemLabel="value" cssClass="form-control form-control-sm validate[required] cetExt" path="cetExt" id="cetExt"/>
                                </div>
                            </div>
                            <br/><br/>
                            <div class="form-group">
                                <label  style="padding-top: 8px;" for="expIncome" class="control-label col-sm-6"><fmt:message key="label.expIncome"/></label>
                                <div class="col-lg-6 col-md-6" style="width: 60%;">
                                    <form:input type="text" cssClass="validate[required] form-control form-control-sm" path="expIncome" id="expIncome"/>
                                </div>
                            </div>
                            <form:input cssClass="hidden" path="bbcdab" value="${bbcdab}"/>
                            <form:input cssClass="hidden" path="tdfee"  value="${tdfee}"/>
                            <form:input cssClass="hidden" path="forex"  value="${forex}"/>
                            <form:input cssClass="hidden" path="cafee"  value="${cafee}"/>
                            <form:input cssClass="hidden" path="cms"  value="${cms}"/>
                            <form:input cssClass="hidden" path="other"  value="${other}"/>
                            <form:input cssClass="hidden" path="facNo" id="facNo_bonds"/>
                            <input type="text" class="hidden" value="Add" id="butnId_bonds"/>
                        </div>
                        <br/>
                        <div class="col-lg-12 col-md-12 text-center">
                            <div class="marginTop" style="align-content: center">
                                <button type="submit" id="butnA_bonds" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing...">Add Facility</button>
                                <input type="submit" id="butnM_bonds" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing..." value="Modify Facility"/>
                                <input type="button" id="rSet_bonds" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing..." value="Reset"/>
                            </div>
                        </div>
                    </div>
                </form:form>
            </div>
            <div id="menu3" class="tab-pane fade">
                <h3>Page Under Construction Derivatives</h3>
            </div>
        </div>
    </div>
</div>
<div class="col-lg-12 col-md-12 marginTop">
    <div class="panel panel-default">
        <div class="panel-heading">
            <a><em>Facility Details - Tabular View</em></a>
            <span class="pull-right clickable "><i class="glyphicon glyphicon-chevron-down"></i></span>
        </div>
        <div class="panel-body no-gutters">
            <div id="jqgrid" class="spacing"> 
                <table id="grid"></table>
                <div id="pager"></div>                    
            </div>
            <br />   
            <div id="jqgrid_nfb" class="spacing"> 
                <table id="grid_nfb"></table>
                <div id="pager_nfb"></div>                    
            </div>
            <br />
            <div id="jqgrid_bonds" class="spacing"> 
                <table id="grid_bonds"></table>
                <div id="pager_bonds"></div>                    
            </div>
        </div>
    </div>
</div>
<div class="col-lg-12 col-md-12 text-center">
    <div class="marginTop" style="align-content: center">
        <input type="button" id="back" class="btn btn-default" value="Back"/>
        <input type="button" id="rarocSubmit" class="btn btn-default" value="Calculate RAROC"/>
    </div>
</div>
