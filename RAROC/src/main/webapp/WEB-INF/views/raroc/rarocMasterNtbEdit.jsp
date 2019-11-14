<%-- 
    Document   : rarocMasterNtbEdit
    Created on : Jul 7, 2019, 11:56:35 AM
    Author     : Amolraj
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script type="text/javascript" src="<c:url value="/resources/js/typeahead.bundle.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/ajax/rarocMaster.js"/>"></script>
<script>
    $('[data-toggle="tooltip"]').tooltip();

    $(".panel-heading span.clickable").parents('.panel').find('.panel-body').show();
    /*panel collapse start*/
    $(".panel-heading span.clickable").click(function () {
        var $this = $(this);
        if (!$this.hasClass('panel-collapsed')) {
            $this.parents('.panel').find('.panel-body').slideDown();
            //$this.parents('.panel').find('.panel-body').show();
            $this.addClass('panel-collapsed');
            $this.find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
        } else {
            $this.parents('.panel').find('.panel-body').slideUp();
            $this.removeClass('panel-collapsed');
            $this.find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        }
    });
    /*panel collapse End*/

</script>
<div class="col-lg-12 col-md-12 marginTop">
    <div class="panel panel-breadcrumb">
        <div class="panel-body">
            <fmt:message key="breadcrumb.raroc.master"/>
        </div>
    </div>
    <c:url var="curl" value="/raroc/edit"/>
    <form:form method="POST" commandName="formRarocMaster" action="${curl}" id="rarocMaster" htmlEscape="true">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>    
        <div class="panel panel-default">
            <div class="panel-heading" style="height: 60px;">
                <a href="#"><em>Borrower Details</em></a>
                <span>
                    <div class="form-inline required pull-right">
                        <div class="form-group has-feedback">
                            <c:set var="chk_ntb" value=""/>
                            <c:if test="${radio == 'NTB'}">
                                <c:set var="chk_ntb" value="checked"/>
                            </c:if>
                            <c:set var="chk_etb" value=""/>
                            <c:if test="${radio == 'ETB'}">
                                <c:set var="chk_etb" value="checked"/>
                            </c:if>
                            <label class="input-group">
                                <span class="input-group-addon">
                                    <input type="radio" name="custType" value="ETB" ${chk_etb}/>
                                </span>
                                <div class="form-control form-control-static">
                                    ETB
                                </div>
                                <span class="glyphicon form-control-feedback "></span>
                            </label>
                        </div>
                        <div class="form-group has-feedback ">
                            <label class="input-group">
                                <span class="input-group-addon">
                                    <input type="radio" name="custType" value="NTB"  ${chk_ntb}/>
                                </span>
                                <div class="form-control form-control-static">
                                    NTB
                                </div>
                                <span class="glyphicon form-control-feedback "></span>
                            </label>
                        </div>&nbsp;&nbsp;&nbsp;
                        <span style="margin-top: 10px;" class="pull-right clickable "><i class="glyphicon glyphicon-chevron-down"></i></span>
                    </div>
                </span>
            </div>
            <div id="avaRating">
                <div class="panel-body no-gutters">
                    <div class="col-lg-6 col-md-6">
                        <div class="form-group">
                            <label for="rid" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.avaRating"/></label>
                            <div class="col-lg-8 col-md-8">

                                <form:select cssClass="form-control" path="avaRating">
                                    <form:option value="" label="Select...."/>
                                    <form:option value="Yes" label="Yes"/>
                                    <form:option value="No" label="No"/>
                                </form:select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel-body no-gutters spacing">
                <div class="col-lg-6 col-md-6">
                    <div class="form-group">
                        <label for="rid" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.rid"/>
                            <span class="">
                                <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="rid"/>">
                                    <i class="fa fa-default fa-1x fa-question"></i>
                                </a>
                            </span>
                        </label>
                        <div class="col-lg-8 col-md-8">
                            <!--<form:input type="text" cssClass="form-control" path="rid"/>-->
                            <div id="rids">
                                <input type="text" class="form-control" style="width: 385px;" name="rid" id="ratingId" value="${ratingIds}" readonly="true"/>
                            </div>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <form:hidden path="rarocref"/> 
                        <label for="pan" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.pan"/></label>
                        <div class="col-lg-8 col-md-8">
                            <input type="hidden" id="phrase" value="<c:out value="${pageContext.session.id}"/>"/>
                            <form:input type="text" cssClass="form-control validate[required,custom[panNum]]" path="pan" id="panNums"/>
                            <form:input cssClass="hidden" path="panEnc" id="panText"/>
                            <input type="hidden" name="salt"/>
                            <input type="hidden" name="iv"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="cid" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.cid"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="cid" id="cid"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="rtool" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.rtool"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select items="${rtool}" itemLabel="value" itemValue="key" cssClass="form-control validate[required]" path="rtool">
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="bussunit" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.bussunit"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select items="${bu}" itemLabel="value" itemValue="key" cssClass="form-control  validate[required]" path="bussunit">
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="ebid" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.ebid"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass=" form-control" path="ebid"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="expBanks" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.expBanks"/>
                            <span class="">
                                <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="expBanks"/>">
                                    <i class="fa fa-default fa-1x fa-question"></i>
                                </a>
                            </span>
                        </label>
                        <div class="col-lg-8 col-md-8">
                            <form:select cssClass="form-control  validate[required]" path="expBanks">
                                <form:option value="" label="Select...."/>
                                <form:option value="Upto 100 crore" label="Upto 100 crore"/>
                                <form:option value="More than 100 upto 200 crore" label="More than 100 upto 200 crore"/>
                                <form:option value="More than 200 crore" label="More than 200 crore"/>
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="portion" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.portion"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control  validate[required]" path="portion"/>
                        </div>
                    </div>
                    <br/><br/><br/><br/>
                    <div class="form-group">
                        <label for="nonFinColl" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.improp"/>
                            <span class="">
                                <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="nonFinColl"/>">
                                    <i class="fa fa-default fa-1x fa-question"></i>
                                </a>
                            </span>
                        </label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control  validate[required]" path="nonFinColl"/>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6 col-md-6">
                    <div class="form-group">
                        <label for="cname" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.cname"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control" path="cname" id="cname"  readonly="true"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="cif" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.cif"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="cif" id="cif"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="intRat" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.intRat"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select items="${rcode}" itemLabel="value" itemValue="key" cssClass="form-control validate[required]" path="intRat"></form:select>
                            </div>
                        </div>
                        <br/><br/>
                        <div class="form-group">
                            <label for="ind" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.ind"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select items="${ind}" itemLabel="value" itemValue="key" cssClass="form-control validate[required]" path="ind">
                            </form:select>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="cpsid" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.cpsid"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="cpsid" id="cpsid"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="ufce" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.ufce"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="ufce"/>
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="npll" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.npll"/>
                            <span class="">
                                <a href="#" data-toggle="tooltip" data-placement="right" title="<fmt:message key="npll"/>">
                                    <i class="fa fa-default fa-1x fa-question"></i>
                                </a>
                            </span>
                        </label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="npll"/>
                        </div>
                    </div>
                    <br/><br/><br/><br/><br/><br/><br/>
                    <div class="form-group">
                        <label for="workcap" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.workcap"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:select cssClass="form-control validate[required]" path="workcap">
                                <form:option value="" label="Select...."/>
                                <form:option value="Yes" label="Yes"/>
                                <form:option value="No" label="No"/>
                            </form:select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="panel panel-default">
            <div class="panel-heading">
                <p>
                    <a href="manual"><em><fmt:message key="label.othinc"/></em></a>
                    <span class="pull-right">
                        All Amounts are in Lakhs
                    </span>
                </p>
            </div>
            <div class="panel-body no-gutters">
                <div class="col-lg-6 col-md-6">
                    <div class="form-group">
                        <label for="bbcdab" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.bbcdab"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="bbcdab" />
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="td" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.td"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="tdfee" />
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="forex" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.forex"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="forex" />
                        </div>
                    </div>
                </div>
                <div class="col-lg-6 col-md-6">
                    <div class="form-group">
                        <label for="cafee" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.cafee"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="cafee" />
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="cms" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.cms"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="cms" />
                        </div>
                    </div>
                    <br/><br/>
                    <div class="form-group">
                        <label for="other" class="col-lg-4 col-md-4 control-label"><fmt:message key="label.other"/></label>
                        <div class="col-lg-8 col-md-8">
                            <form:input type="text" cssClass="form-control validate[required]" path="other" />
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-12 col-md-12">
            <div class="marginTop">
                <button type="submit" class="btn btn-default" data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing...">Next</button>
            </div>
        </div>
    </form:form>
</div>