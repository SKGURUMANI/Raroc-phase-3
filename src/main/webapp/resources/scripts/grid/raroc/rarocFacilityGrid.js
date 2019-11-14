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

    $(".butn").button();

    /*   
     *  RAROC Existing grid
     */

    function fbuttons(cellvalue, options, rowObject) {
        return '<button title="View" id="view-' + rowObject['id'] + '" class="btn btn-default btn-xs action"><i class="fa fa-eye"></i>\n\
                </button> \n\
                <button title="Edit" id="edit-' + rowObject['id'] + '" class="btn btn-default btn-xs action"><i class="fa fa-pencil"></i></button>';
    }

    var grid = $("#grid"),
            initialWidth = $("#jqgrid").width() - 2,
            afterWidth = initialWidth,
            refcode = $("#refId").val(),
            URL = 'raroc/facility/grid?recref=' + refcode, lastSel1 = -1,
            delOptions = {
                onclickSubmit: function (params, postdata) {
                    params.url = 'raroc/facility/grid/fb?recref=' + refcode + '&id=' + grid.jqGrid('getCell', postdata, 'facNo');
                },
                errorTextFormat: function (r) {
                    return "Please Check Application logs";
                }
            },
    params0 = {
        name: 'facNo',
        index: 'n_facility_no',
        label: 'Facility No',
        align: 'center'
    },
    params1 = {
        name: 'refrec',
        index: 'v_rec_ref_no',
        label: 'Reference Id',
        hidden: true
    },
    params2 = {
        name: 'facType',
        index: 'v_fac_type',
        label: 'Facility Type',
        align: 'left'
    },
    params3 = {
        name: 'facDesc',
        index: 'v_fac_desc',
        label: 'Facility Desc',
        align: 'left'
    },
    params4 = {
        name: 'amount',
        index: 'n_amount',
        label: 'Amount',
        align: 'right'
    },
    params6 = {
        name: 'tRating',
        index: 'V_TEMPLATED_RATING',
        label: 'Template Product',
        align: 'left'
    },
    params7 = {
        name: 'curfy',
        index: 'N_AVG_BAL_CUR',
        label: 'Average Balance current FY (Rs. Lakh)',
        align: 'left'
    },
    params8 = {
        name: 'nextfy',
        index: 'N_AVG_BAL_NEXT',
        label: 'Average Balance next FY (Rs. Lakh)',
        align: 'left'
    },
    params9 = {
        name: 'avgfy',
        index: 'N_AVG_UTL_CUR',
        label: 'Average Utilization in current FY  (in %)',
        align: 'left'
    },
    params10 = {
        name: 'avgnextfy',
        index: 'N_AVG_UTL_NEXT',
        label: 'Average Utilization in next FY  (in %)',
        align: 'left'
    },
    params11 = {
        name: 'ucicf',
        index: 'V_UCICF',
        label: 'Unconditionally Cancelable',
        align: 'left'
    },
    params12 = {
        name: 'restStatus',
        index: 'V_RESTRUCTURED_STATUS',
        label: 'Restructuring Status',
        align: 'left'
    },
    params13 = {
        name: 'oridate',
        index: 'D_ORIGIN_DATE',
        label: 'Origination date',
        align: 'left'
    },
    params14 = {
        name: 'maturity',
        index: 'N_ORIGINAL_MATURITY',
        label: 'Original Maturity',
        align: 'left'
    },
    params15 = {
        name: 'tenure',
        index: 'n_tenure',
        label: 'Average Tenure  (in Months)',
        align: 'right'
    },
    params16 = {
        name: 'refreq',
        index: 'V_REPRICE_FREQ',
        label: 'Re-pricing frequency',
        align: 'right'
    },
    params17 = {
        name: 'aFee',
        index: 'N_ANNUAL_FEE',
        label: 'Annual Fee (in %), if any',
        align: 'right'
    },
    params18 = {
        name: 'astType',
        index: 'V_ASSET_TYPE',
        label: 'Basel Asset Class',
        align: 'left'
    },
    params19 = {
        name: 'psl',
        index: 'V_PSL',
        label: 'PSL category',
        align: 'left'
    },
    params20 = {
        name: 'cur',
        index: 'v_curr',
        label: 'Currency',
        align: 'left'
    },
    params21 = {
        name: 'exgRate',
        index: 'N_EXCGE_RATE',
        label: 'Exchange Rate (Rs/Currency)',
        align: 'right'
    },
    params22 = {
        name: 'inttype',
        index: 'V_INT_TYPE',
        label: 'Interest Type',
        align: 'left'
    },
    params23 = {
        name: 'benchmark',
        index: 'V_BENCHMARK',
        label: 'Benchmark',
        align: 'left'
    },
    params24 = {
        name: 'intRate',
        index: 'N_INT_RATE_COMM',
        label: 'Interest Rate(in %)',
        align: 'right'
    },
    params251 = {
        name: 'costFunds',
        index: 'N_COST_FUNDS',
        label: 'Cost of Fund',
        align: 'left'
    },
    params25 = {
        name: 'uFee',
        index: 'N_UPFRONT_FEE',
        label: 'Processing fees on hold position (Rs. Lakh)',
        align: 'right'
    },
    params26 = {
        name: 'synfee',
        index: 'N_SYNDICATION_FEE',
        label: 'Syndication fees (Rs. Lakh)',
        align: 'right'
    },
    params27 = {
        name: 'cSecured',
        index: 'V_COMP_SECURED',
        label: 'Completely Unsecured',
        align: 'left'
    },
    params28 = {
        name: 'region',
        index: 'V_REGION',
        label: 'Origination',
        align: 'left'
    },
    params29 = {
        name: 'cMargin',
        index: 'N_CASH_MARGIN',
        label: 'Financial collateral over facility amt (in %)',
        align: 'right'
    },
    params30 = {
        name: 'cMarginCurr',
        index: 'V_CASH_MISMATCH',
        label: 'Financial Security Type',
        align: 'left'
    },
    params31 = {
        name: 'guarType',
        index: 'V_GUAR_TYPE',
        label: 'Guarantor Type',
        align: 'left'
    },
    params32 = {
        name: 'eGuar',
        index: 'N_EXP_GUAR',
        label: 'Exposure Guaranteed (in %)',
        align: 'right'
    },
    params33 = {
        name: 'guarExtRat',
        index: 'V_GUARANTOR_EXT',
        label: 'Guarantor External Rating',
        align: 'left'
    },
    params34 = {
        name: 'guarIntRat',
        index: 'V_GUARANTOR_INT',
        label: 'Guarantor Internal Rating',
        align: 'left'
    },
    params35 = {
        name: 'ltRating',
        index: 'V_LONG_EXT',
        label: 'Facility Long Term Ext. Rating',
        align: 'left'
    },
    params36 = {
        name: 'stRating',
        index: 'V_SHORT_EXT',
        label: 'Facility Short Term Ext. Rating',
        align: 'left'
    },
    params37 = {
        name: 'extRated',
        index: 'V_EXT_RATED',
        label: 'Was the facility externally rated anytime earlier',
        align: 'left'
    },
    params38 = {
        name: 'giftCity',
        index: 'V_GIFT_CITY',
        label: 'GIFT City',
        align: 'left'
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params6,
            params7, params8, params9, params10, params11, params12, params13,
            params14, params15, params16, params17, params18, params19, params20,
            params21, params22, params23, params24, params251, params25, params26, params27,
            params28, params29, params30, params31,
            params32, params33, params34,
            params35, params36, params37, params38],
        gridview: true, // Not to be used with treeGrid, subGrid and afterInsertRow
        caption: 'List of Facilities - Fund Base',
        pager: '#pager',
        autowidth: true,
        altRows: true,
        rowNum: 6,
        rowList: [6, 20, 40],
        emptyrecords: 'Nothing to view',
        height: 'auto',
        shrinkToFit: false,
        loadComplete: function () {
            afterWidth = $("#jqgrid").width() - 2;
            grid.jqGrid("setGridWidth", afterWidth);
        },
        onHeaderClick: function (gridstate) {
            if (gridstate === "visible") {
                grid.jqGrid("setGridWidth", afterWidth);
            } else {
                grid.jqGrid("setGridWidth", initialWidth);
            }
        }
    };
    grid.jqGrid(options).navGrid("#pager", {
        edit: false,
        add: false,
        del: false,
        search: false,
        refresh: true
    }, {}, {}, {}, {
        sopt: ['cn', 'nc', 'bw', 'ew', 'eq']
    })
            .navButtonAdd('#pager', {
                caption: "Modify",
                buttonicon: "fa fa-edit",
                onClickButton: function () {
                    $('#mytabs a[href="#home"]').tab('show');

                    $("#butnA").hide();
                    $("#butnM").show();
                    $("#butnId").val("Modify");
                    var facCount = $("#facility").val();
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    if (selRowId !== null) {
                        var facId = grid.jqGrid('getCell', selRowId, 'facNo');
                        var recid = grid.jqGrid('getGridParam', 'selarrrow');
                        if (recid.length > facCount) {
                            BootstrapDialog.show({
                                title: 'Validation Message',
                                message: "You cannot select more than " + facCount + " facility"
                            });
                        } else {
                            $("#facNo").val(facId);
                            $("#facDesc").val(grid.jqGrid('getCell', selRowId, 'facDesc'));
                            $("#facTypes").val(grid.jqGrid('getCell', selRowId, 'facType'));


                            var val = $("#facTypes").val();

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



                            if (grid.jqGrid('getCell', selRowId, 'astType') !== "Other-corporates" && grid.jqGrid('getCell', selRowId, 'astType') !== "NBFC ND-SI" && grid.jqGrid('getCell', selRowId, 'astType') !== "Commercial Real Estate"
                                    && grid.jqGrid('getCell', selRowId, 'astType') !== "Capital Market" && grid.jqGrid('getCell', selRowId, 'astType') !== "Commercial Real Estate (RH)" && grid.jqGrid('getCell', selRowId, 'astType') !== "Regulatory Retail") {
                                //$("select[name='restStatus']").val("Not Restructured");
                                $("select[name='ucicf']").val("N");
                            } else {
                                //$("select[name='restStatus']").val("");
                                $("select[name='ucicf']").val("Y");
                            }
                            var path = "raroc/extrating/lt?model=" + grid.jqGrid('getCell', selRowId, 'astType');
                            $.getJSON(path, function (data) {
                                var len = data.length, html = "";
                                for (var i = 0; i < len; i++) {
                                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                                }
                                $("select[name='ltRating']").empty();
                                $("select[name='ltRating']").append(html);
                                $("#ltRating").val(grid.jqGrid('getCell', selRowId, 'ltRating'));
                            });
                            $("#astType").val(grid.jqGrid('getCell', selRowId, 'astType'));
                            $("#tRating").val(grid.jqGrid('getCell', selRowId, 'tRating'));

                            $("#tRating").val(grid.jqGrid('getCell', selRowId, 'tRating'));
                            $("#cur").val(grid.jqGrid('getCell', selRowId, 'cur'));



                            $("#amount").val(grid.jqGrid('getCell', selRowId, 'amount'));
                            $("#tenure").val(grid.jqGrid('getCell', selRowId, 'tenure'));
                            $("#maturity").val(grid.jqGrid('getCell', selRowId, 'maturity'));


                            $("#avgfy").val(grid.jqGrid('getCell', selRowId, 'avgfy'));
                            $("#avgnextfy").val(grid.jqGrid('getCell', selRowId, 'avgnextfy'));
                            $("#oridate").val(grid.jqGrid('getCell', selRowId, 'oridate'));
                            $("#refreq").val(grid.jqGrid('getCell', selRowId, 'refreq'));
                            $("#nextfy").val(grid.jqGrid('getCell', selRowId, 'nextfy'));
                            $("#curfy").val(grid.jqGrid('getCell', selRowId, 'curfy'));

                            var mat = grid.jqGrid('getCell', selRowId, 'maturity');
                            var fNum = Number(mat.match(/\d+/g));
                            var SAplha = mat.replace(fNum, "");
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

                            $("#exgRate").val(grid.jqGrid('getCell', selRowId, 'exgRate'));
                            $("#restStatus").val(grid.jqGrid('getCell', selRowId, 'restStatus'));
                            $("#ucicf").val(grid.jqGrid('getCell', selRowId, 'ucicf'));
                            $("#avgUtil").val(grid.jqGrid('getCell', selRowId, 'avgUtil'));
                            $("#inttype").val(grid.jqGrid('getCell', selRowId, 'inttype'));

                            var val = grid.jqGrid('getCell', selRowId, 'inttype');

                            if (val === "Floating") {
                                $("select[name='refreq']").prop("disabled", false);
                                $("select[name='benchmark']").prop("disabled", false);
                            } else {
                                $("select[name='refreq']").val("");
                                $("select[name='refreq']").prop("disabled", true);

                                $("select[name='benchmark']").val("");
                                $("select[name='benchmark']").prop("disabled", true);
                            }

                            $("#benchmark").val(grid.jqGrid('getCell', selRowId, 'benchmark'));
                            $("#region").val(grid.jqGrid('getCell', selRowId, 'region'));
                            $("#intRate").val(grid.jqGrid('getCell', selRowId, 'intRate'));
                            $("#psl").val(grid.jqGrid('getCell', selRowId, 'psl'));
                            $("#aFee").val(grid.jqGrid('getCell', selRowId, 'aFee'));
                            $("#uFee").val(grid.jqGrid('getCell', selRowId, 'uFee'));

                            $("#cMargin").val(grid.jqGrid('getCell', selRowId, 'cMargin'));
                            $("#cMarginCurr").val(grid.jqGrid('getCell', selRowId, 'cMarginCurr'));
                            $("#cSecured").val(grid.jqGrid('getCell', selRowId, 'cSecured'));

                            var test = $("#exBank").val();

                            if (grid.jqGrid('getCell', selRowId, 'ltRating') === "UNRATED" || grid.jqGrid('getCell', selRowId, 'ltRating') === "Corp UNRATED" || grid.jqGrid('getCell', selRowId, 'ltRating') === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
                                $("select[name='extRated']").prop("disabled", false);
                            } else {
                                $("select[name='extRated']").prop("disabled", true);
                            }


                            if (grid.jqGrid('getCell', selRowId, 'stRating') === "UNRATED" || grid.jqGrid('getCell', selRowId, 'stRating') === "Corp UNRATED" || grid.jqGrid('getCell', selRowId, 'stRating') === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
                                $("select[name='extRated']").prop("disabled", false);
                            } else {
                                $("select[name='extRated']").prop("disabled", true);
                            }
                            $("#eGuar").val(grid.jqGrid('getCell', selRowId, 'eGuar'));

                            var ex = $("#exBank").val();
                            if (grid.jqGrid('getCell', selRowId, 'ltRating') === "UNRATED" && ex === "More than 100 upto 200 crore") {
                                $("select[name='extRated']").prop("disabled", false);
                            } else {
                                $("select[name='extRated']").prop("disabled", true);
                            }

                            if (grid.jqGrid('getCell', selRowId, 'stRating') === "UNRATED" && ex === "More than 100 upto 200 crore") {
                                $("select[name='extRated']").prop("disabled", false);
                            } else {
                                $("select[name='extRated']").prop("disabled", true);
                            }

                            var mapCol = $.ajax({
                                type: 'GET',
                                url: 'raroc/get/mapCol?id=' + grid.jqGrid('getCell', selRowId, 'guarType'),
                                data: $(this).serialize(),
                                dataType: 'text',
                                global: false,
                                async: false,
                                success: function (data) {
                                    return data;
                                }
                            }).responseText;

                            $("#guarType").val(mapCol + "~" + grid.jqGrid('getCell', selRowId, 'guarType'));
                            if (mapCol === "Yes") {
                                var val_d = grid.jqGrid('getCell', selRowId, 'guarType');

                                $("select[name='guarIntRat']").val("");
                                $("select[name='guarExtRat']").val("");
                                $("select[name='guarIntRat']").prop("disabled", false);
                                $("select[name='guarExtRat']").prop("disabled", false);
                                var path = "raroc/intrating/guar?model=" + val_d;
                                $.getJSON(path, function (data) {
                                    var len = data.length, html = '<option value= "" >Select...</option>';
                                    for (var i = 0; i < len; i++) {
                                        html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                                    }
                                    $("select[name='guarIntRat']").empty();
                                    $("select[name='guarIntRat']").append(html);
                                });
                                if (val_d === "Corporate") {
                                    val_d = "Other-corporates";
                                }
                                path = "raroc/extrating/lt?model=" + val_d;
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

                            setTimeout(function () {
                                $("#guarIntRat").val(grid.jqGrid('getCell', selRowId, 'guarIntRat'));
                                $("#guarExtRat").val(grid.jqGrid('getCell', selRowId, 'guarExtRat'));
                            }, 300);


                            $("#costFunds").val(grid.jqGrid('getCell', selRowId, 'costFunds'));

                            $("#extRated").val(grid.jqGrid('getCell', selRowId, 'extRated'));
                            $("#giftCity").val(grid.jqGrid('getCell', selRowId, 'giftCity'));

                            
                            
                            
                            // $("#ltRating").val(grid.jqGrid('getCell', selRowId, 'ltRating'));
                            
                            $("#stRating").val(grid.jqGrid('getCell', selRowId, 'stRating'));

                        }
                    } else {
                        BootstrapDialog.show({
                            title: 'Info',
                            message: "Select a Row to Modify"
                        });
                    }
                },
                position: "last"
            })
            .navButtonAdd('#pager', {
                caption: '',
                buttonicon: 'fa fa-trash',
                title: 'Delete Selected Row',
                onClickButton: function () {
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    var status = grid.jqGrid('getCell', selRowId, 'status');
                    if (selRowId !== null) {
                        BootstrapDialog.show({
                            title: 'Info',
                            message: "Due you want to delete selected Record",
                            buttons: [{
                                    label: 'Delete',
                                    action: function (dialog) {
                                        //var path = "raroc/del/grid?refNo=" + grid.jqGrid('getCell', selRowId, 'rarocref');
                                        var path = 'raroc/facility/grid/fb/get?recref=' + refcode + '&id=' + grid.jqGrid('getCell', selRowId, 'facNo');
                                        var message = $.ajax({
                                            type: 'GET',
                                            url: path,
                                            data: $(this).serialize(),
                                            dataType: 'text',
                                            global: false,
                                            async: false,
                                            success: function (data) {
                                                return data;
                                            }
                                        }).responseText;
                                        if (message === "Success") {
                                            $('#grid').trigger('reloadGrid');
                                            dialog.close();
                                        } else {
                                            BootstrapDialog.show({
                                                title: 'Server Message',
                                                message: message
                                            });
                                        }
                                    }
                                }, {
                                    label: 'Cancel',
                                    action: function (dialog) {
                                        dialog.close();
                                    }
                                }]
                        });

                    } else {
                        BootstrapDialog.show({
                            title: 'Info',
                            message: "Select a row to delete"
                        });
                    }
                    return true;
                },
                position: "first"
            });

    grid.jqGrid(options).bindKeys();
    grid.jqGrid(options).setSelection("1");
    grid.focus();

});