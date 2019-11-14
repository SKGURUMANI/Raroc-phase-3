/* 
 * © 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 * 
 * Asymmetrix Solutions Private Limited
 * 115, Bldg 2, Sector 3, Millennium Business Park,
 * Navi Mumbai, India, 410701
 */
$(function () {
    $("#derType1").hide();
    $("#derType2").hide();

    $("#oridate").datetimepicker({
        format: "DD-MMM-YYYY"
    });

    var test = $("#exBank").val();
    if (test === "More than 100 upto 200 crore" || test === "Upto 100 crore" || test === "More than 200 crore") {
        $("select[name='extRated']").prop("disabled", true);
    } else {
        $("select[name='extRated']").prop("disabled", false);
    }
    
    if (test === "More than 100 upto 200 crore" || test === "Upto 100 crore" || test === "More than 200 crore") {
        $("#extRated_nfb").prop("disabled", true);
    } else {
        $("#extRated_nfb").prop("disabled", false);
    }

    $("#form-butn").click(function () {
        if ($("#form-body").is(":visible")) {
            $("#form-butn").removeClass("tabVisible");
            $("#form-butn").addClass("tabHidden");
            $("#form-body").hide("blind", {}, 500);
            $("#form-header").addClass("ui-corner-bottom");
        } else {
            $("#form-butn").removeClass("tabHidden");
            $("#form-butn").addClass("tabVisible");
            $("#form-header").removeClass("ui-corner-bottom");
            $("#form-body").show("blind", {}, 500);
        }
    });


    $("#rarocFacility").validationEngine('attach', {
        promptPosition: "centerRight:-170,-5"
    });


    $("#region").change(function () {
        var facT = $("#facTypes").val();
        var region = $("#region").val();
        var intType = $("#inttype").val();
        var curr = $("#cur").val();
        var val = $("#refreq").val();
        var date = $("#oridate").val();
        var Mat = $("#maturity").val();

        //if (facT !== null || region !== null || intType !== null || curr !== null || val !== null) {
        if (1 === 1) {
            var id = val, reFrqe,mult;
            if (typeof id === "undefined") {
                id = "";
                reFrqe = "";
                mult = "";
            } else {
                id = val.split("-");
                reFrqe = id[0];
                mult = id[1];
            }
            var path = "raroc/get/cf?facT=" + facT + "&region=" + region.toUpperCase() + "&intType=" + intType.toUpperCase() + "&curr=" + curr.toUpperCase() + "&reFrqe=" + reFrqe + "&mult=" + mult + "&date=" + date + "&Mat=" + Mat;
            $.getJSON(path, function (data) {
                if (data === -2) {
                    $("#costFunds").empty();
                    $("#costFunds").prop("readonly", false);
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Cost of fund Calculation returns null value.\n\
                                  Please Contact IT Team'
                    });
                } else if (data === -1) {
                    $("#costFunds").empty();
                    $("#costFunds").prop("readonly", false);
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Cost of fund Calculation Failed.\n\
                                  Please Contact IT Team'
                    });
                } else {
                    $("#costFunds").empty();
                    $("#costFunds").val(data);
                    $("#costFunds").prop("readonly", true);
                }
            });
        } else {
            BootstrapDialog.show({
                title: 'Server Message',
                message: 'Cost of fund Calculation returns null value.\n\
                          Please Contact IT Team'
            });
        }
    });

    $(".cur").change(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = name.split(".");
        if (val === "INR") {
            $("input[name='exgRate']").val("1");
        } else {
            $("input[name='exgRate']").val("");
        }
    });

    $(".guarType").each(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = val.split("~");
        val = id[1];
        //if (val === "Corporate" || val === "Foreign Bank" || val === "Foreign Corporate") {
        if (id[0] === "Yes") {
            $("select[name='guarIntRat']").val("");
            $("select[name='guarExtRat']").val("");
            $("select[name='guarIntRat']").prop("disabled", false);
            $("select[name='guarExtRat']").prop("disabled", false);
            var path = "raroc/intrating/guar?model=" + val;
            $.getJSON(path, function (data) {
                var len = data.length, html = '<option value= "" >Select...</option>';
                for (var i = 0; i < len; i++) {
                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                }
                $("select[name='guarIntRat']").empty();
                $("select[name='guarIntRat']").append(html);
            });

            if (val === "Corporate") {
                val = "Other-corporates";
            }
            path = "raroc/extrating/lt?model=" + val;
            $.getJSON(path, function (data) {
                var len = data.length, html = "";
                for (var i = 0; i < len; i++) {
                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                }
                $("select[name='guarExtRat']").empty();
                $("select[name='guarExtRat']").append(html);
            });
        } else {
            $("select[name='guarIntRat']").val("");
            $("select[name='guarExtRat']").val("");
            $("select[name='guarIntRat']").prop("disabled", true);
            $("select[name='guarExtRat']").prop("disabled", true);
        }
    });

    $(".guarType").change(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = val.split("~");
        val = id[1];

        //if (val === "Corporate" || val === "Foreign Bank" || val === "Foreign Corporate") {
        if (id[0] === "Yes") {
            $("select[name='guarIntRat']").val("");
            $("select[name='guarExtRat']").val("");
            $("select[name='guarIntRat']").prop("disabled", false);
            $("select[name='guarExtRat']").prop("disabled", false);
            var path = "raroc/intrating/guar?model=" + val;
            $.getJSON(path, function (data) {
                var len = data.length, html = '<option value= "" >Select...</option>';
                for (var i = 0; i < len; i++) {
                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                }
                $("select[name='guarIntRat']").empty();
                $("select[name='guarIntRat']").append(html);
            });

            if (val === "Corporate") {
                val = "Other-corporates";
            }
            path = "raroc/extrating/lt?model=" + val;
            $.getJSON(path, function (data) {
                var len = data.length, html = "";
                for (var i = 0; i < len; i++) {
                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                }
                $("select[name='guarExtRat']").empty();
                $("select[name='guarExtRat']").append(html);
            });
        } else {
            $("select[name='guarIntRat']").val("");
            $("select[name='guarExtRat']").val("");
            $("select[name='guarIntRat']").prop("disabled", true);
            $("select[name='guarExtRat']").prop("disabled", true);
        }
    });

    $(".inttype").each(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = name.split(".");
        if (val === "Floating") {
            $("select[name='refreq']").prop("disabled", false);
            $("select[name='benchmark']").prop("disabled", false);
        } else {
            $("select[name='refreq']").val("");
            //$("select[name='refreq']").prop("disabled", true);

            $("select[name='benchmark']").val("");
            $("select[name='benchmark']").prop("disabled", true);
        }
    });

    $(".inttype").change(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = name.split(".");
        if (val === "Floating") {
            $("select[name='refreq']").prop("disabled", false);
            $("select[name='benchmark']").prop("disabled", false);
        } else {
            $("select[name='refreq']").val("");
            $("select[name='refreq']").prop("disabled", true);

            $("select[name='benchmark']").val("");
            $("select[name='benchmark']").prop("disabled", true);
        }
    });

    $("#stRating").change(function () {
        if ($("#stRating").val() === "UNRATED" || $("#stRating").val() === "Corp UNRATED" || $("#stRating").val() === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
            $("select[name='extRated']").prop("disabled", false);
        } else {
            $("select[name='extRated']").prop("disabled", true);
        }
    });

    $("#ltRating").change(function () {
        if ($("#ltRating").val() === "UNRATED" || $("#ltRating").val() === "Corp UNRATED" || $("#ltRating").val() === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
            $("select[name='extRated']").prop("disabled", false);
        } else {
            $("select[name='extRated']").prop("disabled", true);
        }
    });
    
     $("#stRating_nfb").change(function () {
        if ($("#stRating_nfb").val() === "UNRATED" || $("#stRating_nfb").val() === "Corp UNRATED" || $("#stRating_nfb").val() === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
            $("#extRated_nfb").prop("disabled", false);
        } else {
            $("#extRated_nfb").prop("disabled", true);
        }
    });

    $("#ltRating_nfb").change(function () {
        if ($("#ltRating_nfb").val() === "UNRATED" || $("#ltRating_nfb").val() === "Corp UNRATED" || $("#ltRating_nfb").val() === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
            $("#extRated_nfb").prop("disabled", false);
        } else {
            $("#extRated_nfb").prop("disabled", true);
        }
    });


    $(".facType").change(function () {
        var val = $(this).val();
        if (val === "Derivative") {
            $("#derType1").show();
            $("#derType2").show();
        } else {
            $("#derType1").hide();
            $("#derType2").hide();
        }
    });

    $("#rarocFacility_nfb").submit(function (e) {
        var path;
        if ($("#butnId_nfb").val() === "Add") {
            path = 'raroc/new/facility_nfb';
        } else {
            path = 'raroc/update/facility_nfb';
        }
        e.preventDefault();
        if ($(this).validationEngine('validate')) {
            $(this).validationEngine('hideAll');
            var message = $.ajax({
                type: 'POST',
                url: path,
                data: $(this).serialize(),
                dataType: 'text',
                global: false,
                async: false,
                success: function (data) {
                    return data;
                }
            }).responseText;
            var substr = message;
            if (substr === "success") {
                $('#grid_nfb').trigger('reloadGrid');
                if ($("#butnId_nfb").val() === "Add") {
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Facility added successfully'
                    });
                } else {
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Facility updated successfully'
                    });
                }
                $('#rarocFacility_nfb').trigger("reset");
            } else {
                BootstrapDialog.show({
                    title: 'Server Message',
                    message: 'Please check Application logs'
                });
            }
            return true;
        }
    });

    $("#rarocFacility_bonds").submit(function (e) {
        var path;
        if ($("#butnId_bonds").val() === "Add") {
            path = 'raroc/new/facility_bonds';
        } else {
            path = 'raroc/update/facility_bonds';
        }
        e.preventDefault();
        if ($(this).validationEngine('validate')) {
            $(this).validationEngine('hideAll');
            var message = $.ajax({
                type: 'POST',
                url: path,
                data: $(this).serialize(),
                dataType: 'text',
                global: false,
                async: false,
                success: function (data) {
                    return data;
                }
            }).responseText;
            var substr = message;
            if (substr === "success") {
                $('#grid_bonds').trigger('reloadGrid');
                if ($("#butnId_bonds").val() === "Add") {
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Facility added successfully'
                    });
                } else {
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Facility updated successfully'
                    });
                }
                $('#rarocFacility_bonds').trigger("reset");
            } else {
                BootstrapDialog.show({
                    title: 'Server Message',
                    message: 'Please check Application logs'
                });
            }
            return true;
        }
    });


    $("#rarocFacility").submit(function (e) {
        var path;

        if ($("#butnId").val() === "Add") {
            path = 'raroc/new/facility';
        } else {
            path = 'raroc/update/facility';
        }
        e.preventDefault();
        if ($(this).validationEngine('validate')) {
            $(this).validationEngine('hideAll');
            var message = $.ajax({
                type: 'POST',
                url: path,
                data: $(this).serialize(),
                dataType: 'text',
                global: false,
                async: false,
                success: function (data) {
                    return data;
                }
            }).responseText;
            var substr = message;
            if (substr === "success") {
                $('#grid').trigger('reloadGrid');
                if ($("#butnId").val() === "Add") {
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Facility added successfully'
                    });
                } else {
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: 'Facility updated successfully'
                    });
                }
                $('#rarocFacility').trigger("reset");
            } else {
                BootstrapDialog.show({
                    title: 'Server Message',
                    message: 'Please check Application logs'
                });
            }
            return true;
        }
    });

    $("#butnM").hide();
    $("#rSet").click(function (e) {
        $('#rarocFacility').trigger("reset");
        $("#butnA").show();
        $("#butnM").hide();
    });

    $("#butnM_nfb").hide();
    $("#rSet_nfb").click(function (e) {
        $('#rarocFacility_nfb').trigger("reset");
        $("#butnA_nfb").show();
        $("#butnM_nfb").hide();
    });

    $("#butnM_bonds").hide();
    $("#rSet_bonds").click(function (e) {
        $('#rarocFacility_bonds').trigger("reset");
        $("#butnA_bonds").show();
        $("#butnM_bonds").hide();
    });

    //Final Submit of form to Calculate total RAROC
    $("#rarocSubmit").click(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'GET',
            url: 'raroc/final/submit?refId=' + $("#refId").val() + '&facTy=' + $("#facTypes").val(),
            //url: 'raroc/final/submit?refId=Ref-2055&facTy=t',
            async: false,
            success: function (data) {
                $("#container-fluid").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
                $('#container-fluid').html(data);
            }
        });
        return true;

    });


    $(".facType").each(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = name.split(".");
        if (val === "LC") {
            $("select[name='inttype']").val("");
            $("select[name='benchmark']").val("");
            $("select[name='inttype']").prop("disabled", true);
            $("select[name='benchmark']").prop("disabled", true);

            //    $("input[name='avgfy']").prop("disabled", false);
            //    $("input[name='avgnextfy']").prop("disabled", false);
            //    $("input[name='curfy']").prop("disabled", false);
            //    $("input[name='nextfy']").prop("disabled", false);
        } else if (val === "CC") {
            $("select[name='ltRating']").prop("disabled", false);
            $("select[name='stRating']").prop("disabled", true);

            $("input[name='avgfy']").prop("disabled", false);
            $("input[name='avgnextfy']").prop("disabled", false);
            $("input[name='curfy']").prop("disabled", true);
            $("input[name='nextfy']").prop("disabled", true);
        } else if (val === "LTL/STL" || val === "WCDL" || val === "Other Bills" || val === "EBRD" || val === "PCFC" || val === "Other FB") {
            $("input[name='curfy']").prop("disabled", false);
            $("input[name='nextfy']").prop("disabled", false);
            $("input[name='avgfy']").prop("disabled", true);
            $("input[name='avgnextfy']").prop("disabled", true);
        } else {
            $("select[name='inttype']").val("");
            $("select[name='benchmark']").val("");
            $("select[name='inttype']").prop("disabled", false);
            $("select[name='benchmark']").prop("disabled", false);

            //$("input[name='avgfy']").prop("disabled", false);
            //$("input[name='avgnextfy']").prop("disabled", false);
            //$("input[name='curfy']").prop("disabled", true);
            //$("input[name='nextfy']").prop("disabled", true);
        }
    });


    $(".facType").change(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = name.split(".");
        if (val === "LC") {
            $("select[name='inttype']").val("");
            $("select[name='benchmark']").val("");
            $("select[name='inttype']").prop("disabled", true);
            $("select[name='benchmark']").prop("disabled", true);

        } else if (val === "CC") {
            $("select[name='ltRating']").prop("disabled", false);
            $("select[name='stRating']").prop("disabled", true);
            $("select[name='refreq']").val("12-M");
            $("select[name='refreq']").prop('readonly', true);

            $("select[name='inttype']").val("Floating");
            $("select[name='inttype']").prop('readonly', true);
            $("select[name='refreq']").prop("disabled", false);


            $("input[name='avgfy']").prop("disabled", false);
            $("input[name='avgnextfy']").prop("disabled", false);
            $("input[name='curfy']").prop("disabled", true);
            $("input[name='nextfy']").prop("disabled", true);

        } else if (val === "OD") {
            $("select[name='refreq']").val("12-M");
            $("select[name='inttype']").val("Floating");
            $("select[name='refreq']").prop("disabled", false);

            $("input[name='avgfy']").prop("disabled", false);
            $("input[name='avgnextfy']").prop("disabled", false);
            $("input[name='curfy']").prop("disabled", true);
            $("input[name='nextfy']").prop("disabled", true);

        } else if (val === "LTL/STL") {
            $("input[name='curfy']").prop("disabled", false);
            $("input[name='nextfy']").prop("disabled", false);
            $("input[name='avgfy']").prop("disabled", true);
            $("input[name='avgnextfy']").prop("disabled", true);

        } else if (val === "WCDL" || val === "Other Bills" || val === "EBRD" || val === "PCFC" || val === "Other FB") {

            $("input[name='curfy']").prop("disabled", false);
            $("input[name='nextfy']").prop("disabled", false);
            $("input[name='avgfy']").prop("disabled", true);
            $("input[name='avgnextfy']").prop("disabled", true);

            $("select[name='inttype']").val("Fixed");
            //$("select[name='inttype']").prop('readonly', true);
            $("select[name='refreq']").val("");
            $("select[name='refreq']").prop("disabled", true);

        } else {
            $("select[name='inttype']").val("");
            $("select[name='benchmark']").val("");
            $("select[name='inttype']").prop("disabled", false);
            $("select[name='benchmark']").prop("disabled", false);

            $("input[name='avgfy']").prop("disabled", false);
            $("input[name='avgnextfy']").prop("disabled", false);
            $("input[name='curfy']").prop("disabled", true);
            $("input[name='nextfy']").prop("disabled", true);
        }
    });

    $(".astType").change(function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var id = name.split(".");
        if (val !== "Other-corporates" && val !== "NBFC ND-SI" && val !== "Commercial Real Estate"
                && val !== "Capital Market" && val !== "Commercial Real Estate (RH)" && val !== "Regulatory Retail") {
            //$("select[name='restStatus']").val("Not Restructured");
            $("select[name='ucicf']").val("N");
        } else {
            //$("select[name='restStatus']").val("");
            $("select[name='ucicf']").val("Y");
        }
        var path = "raroc/extrating/lt?model=" + val;
        $.getJSON(path, function (data) {
            var len = data.length, html = "";
            for (var i = 0; i < len; i++) {
                html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
            }
            $("select[name='ltRating']").empty();
            $("select[name='ltRating']").append(html);
        });
    });

    $("#facTypes_bonds").change(function () {
        
        var val = $(this).val();
        var path;
        if (val === "Corporate Bond/Commercial Paper") {
            path = "raroc/bond/rating";
        }else{
            path = "raroc/bond/cet";
        }
        $.getJSON(path, function (data) {
            var len = data.length, html = "";
            for (var i = 0; i < len; i++) {
                html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
            }
            $("select[name='cetExt']").empty();
            $("select[name='cetExt']").append(html);
        });
    });



//Change .tenure to maturity as per Phase I Changes and CC/OD to CC
//    $(".maturity").each(function () {
//        var name = $(this).attr("name");
//        var val = $(this).val();
//        var id = name.split(".");
//        if (($("select[name='facType']").val() === "CC") || (val > 12 || $("#ltbank").val() === "N")) {
//            $("select[name='ltRating']").val("");
//            $("select[name='stRating']").val("");
//            $("select[name='ltRating']").prop("disabled", false);
//            $("select[name='stRating']").prop("disabled", true);
//        } else {
//            $("select[name='ltRating']").val("");
//            $("select[name='stRating']").val("");
//            $("select[name='ltRating']").prop("disabled", true);
//            $("select[name='stRating']").prop("disabled", false);
//        }
//    });

    $(".maturity").on('input propertychange paste', function () {
        var name = $(this).attr("name");
        var val = $(this).val();
        var fNum = Number(val.match(/\d+/g));
        var SAplha = val.replace(fNum, "");
        var maurity;
        if (SAplha === "D") {
            maurity = fNum / 30;
        } else if (SAplha === "M") {
            maurity = fNum;
        } else {
            maurity = fNum * 12;
        }
        if (($("select[name='facType']").val() === "CC") || (Number(maurity) > 12 || $("#ltbank").val() === "N")) {
            $("select[name='ltRating']").val("");
            $("select[name='stRating']").val("");
            $("select[name='ltRating']").prop("disabled", false);
            $("select[name='stRating']").prop("disabled", true);
        } else {
            $("select[name='ltRating']").val("");
            $("select[name='stRating']").val("");
            $("select[name='ltRating']").prop("disabled", true);
            $("select[name='stRating']").prop("disabled", false);
        }
    });
    
    $("#maturity_nfb").on('input propertychange paste', function () {
        var val = $(this).val();
        var fNum = Number(val.match(/\d+/g));
        var SAplha = val.replace(fNum, "");
        var maurity;
        if (SAplha === "D") {
            maurity = fNum / 30;
        } else if (SAplha === "M") {
            maurity = fNum;
        } else {
            maurity = fNum * 12;
        }
        if ((Number(maurity) > 12 || $("#ltbank").val() === "N")) {
            $("#ltRating_nfb").val("");
            $("#stRating_nfb").val("");
            $("#ltRating_nfb").prop("disabled", false);
            $("#stRating_nfb").prop("disabled", true);
        } else {
            $("#ltRating_nfb").val("");
            $("#stRating_nfb").val("");
            $("#ltRating_nfb").prop("disabled", true);
            $("#stRating_nfb").prop("disabled", false);
        }
    });

    $("#back").click(function () {
        var path = "raroc/edit?ref=" + $("input[name=refrec]").val();
        $("#container-fluid").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
        $.get(path, function (data) {
            $('#container-fluid').html(data);
        });
    });
});



            