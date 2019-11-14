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

    var grid = $("#grid_nfb"),
            initialWidth = $("#jqgrid_nfb").width() - 2,
            afterWidth = initialWidth,
            refcode = $("#refId").val(),
            //refcode = "Ref-593",
            URL = 'raroc/facility/nfb/grid?recref=' + refcode, lastSel1 = -1,
            delOptions = {
                onclickSubmit: function (params, postdata) {
                    params.url = URL + '&id=' + grid.jqGrid('getCell', postdata, 'facNo');
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
    params5 = {
        name: 'tRating',
        index: 'V_TEMPLATED_RATING',
        label: 'Template Product',
        align: 'left'
    },
    params6 = {
        name: 'avgfy',
        index: 'N_AVG_UTL_CUR',
        label: 'Average Utilization in current FY  (in %)',
        align: 'left'
    },
    params7 = {
        name: 'avgnextfy',
        index: 'N_AVG_UTL_NEXT',
        label: 'Average Utilization in next FY  (in %)',
        align: 'left'
    },
    params8 = {
        name: 'ucicf',
        index: 'V_UCICF',
        label: 'Unconditionally Cancelable',
        align: 'left'
    },
    params9 = {
        name: 'restStatus',
        index: 'V_RESTRUCTURED_STATUS',
        label: 'Restructuring Status',
        align: 'left'
    },
    params10 = {
        name: 'maturity',
        index: 'N_ORIGINAL_MATURITY',
        label: 'Original Maturity',
        align: 'left'
    },
    params11 = {
        name: 'avgMat',
        index: 'N_AVG_MATURITY',
        label: 'Average Maturity current FY (in Months)',
        align: 'right'
    },
    params12 = {
        name: 'astType',
        index: 'V_ASSET_TYPE',
        label: 'Basel Asset Class',
        align: 'left'
    },
    params13 = {
        name: 'cur',
        index: 'v_curr',
        label: 'Currency',
        align: 'left'
    },
    params14 = {
        name: 'exgRate',
        index: 'N_EXCGE_RATE',
        label: 'Exchange Rate (Rs/Currency)',
        align: 'right'
    },
    params15 = {
        name: 'intRate',
        index: 'N_INT_RATE_COMM',
        label: 'Commission(in %)',
        align: 'right'
    },
    params16 = {
        name: 'uFee',
        index: 'N_UPFRONT_FEE',
        label: 'Processing fees on hold position (Rs. Lakh)',
        align: 'right'
    },
    params17 = {
        name: 'synFee',
        index: 'N_SYNDICATION_FEE',
        label: 'Syndication fees (Rs. Lakh)',
        align: 'right'
    },
    params18 = {
        name: 'cSecured',
        index: 'V_COMP_SECURED',
        label: 'Completely Unsecured',
        align: 'left'
    },
    params19 = {
        name: 'region',
        index: 'V_REGION',
        label: 'Exposure Location',
        align: 'left'
    },
    params20 = {
        name: 'cMargin',
        index: 'N_CASH_MARGIN',
        label: 'Financial collateral over facility amt (in %)',
        align: 'right'
    },
    params21 = {
        name: 'cMarginCurr',
        index: 'V_CASH_MISMATCH',
        label: 'Financial Security Type',
        align: 'left'
    },
    params22 = {
        name: 'guarType',
        index: 'V_GUAR_TYPE',
        label: 'Guarantor Type',
        align: 'left'
    },
    params23 = {
        name: 'eGuar',
        index: 'N_EXP_GUAR',
        label: 'Exposure Guaranteed (in %)',
        align: 'right'
    },
    params24 = {
        name: 'guarExtRat',
        index: 'V_GUARANTOR_EXT',
        label: 'Guarantor External Rating',
        align: 'left'
    },
    params25 = {
        name: 'guarIntRat',
        index: 'V_GUARANTOR_INT',
        label: 'Guarantor Internal Rating',
        align: 'left'
    },
    params26 = {
        name: 'ltRating',
        index: 'V_LONG_EXT',
        label: 'Facility Long Term Ext. Rating',
        align: 'left'
    },
    params27 = {
        name: 'stRating',
        index: 'V_SHORT_EXT',
        label: 'Facility Short Term Ext. Rating',
        align: 'left'
    },
    params28 = {
        name: 'extRated',
        index: 'V_EXT_RATED',
        label: 'Was the facility externally rated anytime earlier',
        align: 'left'
    },
    params29 = {
        name: 'giftCity',
        index: 'V_GIFT_CITY',
        label: 'GIFT City',
        align: 'left'
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params5, params6,
            params7, params8, params9, params10, params11, params12, params13,
            params14, params15, params16, params17, params18, params19, params20,
            params21, params22, params23, params24, params25, params26, params27,
            params28, params29],
        gridview: true, // Not to be used with treeGrid, subGrid and afterInsertRow
        caption: 'List of Facilities - Non Fund Base',
        pager: '#pager_nfb',
        autowidth: true,
        altRows: true,
        rowNum: 6,
        rowList: [6, 20, 40],
        emptyrecords: 'Nothing to view',
        height: 'auto',
        shrinkToFit: false,
        loadComplete: function () {
            afterWidth = $("#jqgrid_nfb").width() - 2;
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
    grid.jqGrid(options).navGrid("#pager_nfb", {
        edit: false,
        add: false,
        del: false,
        search: false,
        refresh: true
    }, {}, {}, {}, {
        sopt: ['cn', 'nc', 'bw', 'ew', 'eq']
    })
            .navButtonAdd('#pager_nfb', {
                caption: "Modify",
                buttonicon: "fa fa-edit",
                onClickButton: function () {
                    $('#mytabs a[href="#menu1"]').tab('show');
                    var unit = $("#unit").val();
                    $("#butnA_nfb").hide();
                    $("#butnM_nfb").show();
                    $("#butnId_nfb").val("Modify");
                    var facCount = $("#facility").val();
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    var facId = grid.jqGrid('getCell', selRowId, 'facNo');
                    var recid = grid.jqGrid('getGridParam', 'selarrrow');
                    if (recid.length > facCount) {
                        BootstrapDialog.show({
                            title: 'Validation Message',
                            message: "You cannot select more than " + facCount + " facility"
                        });
                    } else {
                        $("#facNo_nfb").val(facId);
                        $("#facDesc_nfb").val(grid.jqGrid('getCell', selRowId, 'facDesc'));
                        $("#facTypes_nfb").val(grid.jqGrid('getCell', selRowId, 'facType'));


                        var val = $("#facTypes_nfb").val();

                        if (val === "LC" || val === "Financial BG / SBLC / Buyers Credit" || val === "Performance BG / Bid Bonds") {
                            $("select[name='inttype']").val("");
                            $("select[name='benchmark']").val("");
                            $("select[name='inttype']").prop("disabled", true);
                            $("select[name='benchmark']").prop("disabled", true);
                        } else if (val === "CC") {
                            $("select[name='ltRating']").prop("disabled", false);
                            $("select[name='stRating']").prop("disabled", true);
                        } else {
                            $("select[name='inttype']").val("");
                            $("select[name='benchmark']").val("");
                            $("select[name='inttype']").prop("disabled", false);
                            $("select[name='benchmark']").prop("disabled", false);
                        }

                        if (val === "Derivative") {
                            $("#derType1").show();
                            $("#derType2").show();
                        } else {
                            $("#derType1").hide();
                            $("#derType2").hide();
                        }

                        
                        if (grid.jqGrid('getCell', selRowId, 'astType') !== "Other-corporates" && grid.jqGrid('getCell', selRowId, 'astType') !== "NBFC ND-SI" && grid.jqGrid('getCell', selRowId, 'astType') !== "Commercial Real Estate"
                                    && grid.jqGrid('getCell', selRowId, 'astType') !== "Capital Market" && grid.jqGrid('getCell', selRowId, 'astType') !== "Commercial Real Estate (RH)" && grid.jqGrid('getCell', selRowId, 'astType') !== "Regulatory Retail") {
                                //$("select[name='restStatus']").val("Not Restructured");
                                $("#ucicf_nfb").val("N");
                            } else {
                                //$("select[name='restStatus']").val("");
                                $("#ucicf_nfb").val("Y");
                            }
                            var path = "raroc/extrating/lt?model=" + grid.jqGrid('getCell', selRowId, 'astType');
                            $.getJSON(path, function (data) {
                                var len = data.length, html = "";
                                for (var i = 0; i < len; i++) {
                                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                                }
                                $("#ltRating_nfb").empty();
                                $("#ltRating_nfb").append(html);
                            });
                        
                        
                        $("#tRating").val(grid.jqGrid('getCell', selRowId, 'tRating'));

                        $("#tRating_nfb").val(grid.jqGrid('getCell', selRowId, 'tRating'));
                        $("#avgfy_nfb").val(grid.jqGrid('getCell', selRowId, 'avgfy'));
                        $("#avgnextfy_nfb").val(grid.jqGrid('getCell', selRowId, 'avgnextfy'));
                        $("#avgMat").val(grid.jqGrid('getCell', selRowId, 'avgMat'));
                        $("#synFee_nfb").val(grid.jqGrid('getCell', selRowId, 'synFee'));
                        $("#cur_nfb").val(grid.jqGrid('getCell', selRowId, 'cur'));
                        $("#amount_nfb").val(grid.jqGrid('getCell', selRowId, 'amount'));
                        $("#maturity_nfb").val(grid.jqGrid('getCell', selRowId, 'maturity'));

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

                        $("#exgRate_nfb").val(grid.jqGrid('getCell', selRowId, 'exgRate'));
                        $("#restStatus_nfb").val(grid.jqGrid('getCell', selRowId, 'restStatus'));
                        $("#ucicf_nfb").val(grid.jqGrid('getCell', selRowId, 'ucicf'));
                        $("#inttype").val(grid.jqGrid('getCell', selRowId, 'inttype'));


                        $("#region_nfb").val(grid.jqGrid('getCell', selRowId, 'region'));
                        $("#intRate_nfb").val(grid.jqGrid('getCell', selRowId, 'intRate'));
                        $("#uFee_nfb").val(grid.jqGrid('getCell', selRowId, 'uFee'));

                        $("#cMargin_nfb").val(grid.jqGrid('getCell', selRowId, 'cMargin'));
                        $("#cMarginCurr_nfb").val(grid.jqGrid('getCell', selRowId, 'cMarginCurr'));
                        $("#cSecured_nfb").val(grid.jqGrid('getCell', selRowId, 'cSecured'));

                        var test = $("#exBank").val();
                        $("#extRated_nfb").val(grid.jqGrid('getCell', selRowId, 'extRated'));
                        
                        
                        if (grid.jqGrid('getCell', selRowId, 'stRating') === "UNRATED" || grid.jqGrid('getCell', selRowId, 'stRating') === "Corp UNRATED" || grid.jqGrid('getCell', selRowId, 'stRating') === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
                            $("#extRated_nfb").prop("disabled", false);
                        } else {
                            $("#extRated_nfb").prop("disabled", true);
                        }
                        
                        
                        if (grid.jqGrid('getCell', selRowId, 'ltRating') === "UNRATED" || grid.jqGrid('getCell', selRowId, 'ltRating') === "Corp UNRATED" || grid.jqGrid('getCell', selRowId, 'ltRating') === "FCor UNRATED" && test === "More than 100 upto 200 crore") {
                            $("#extRated_nfb").prop("disabled", false);
                        } else {
                            $("#extRated_nfb").prop("disabled", true);
                        }
                        $("#eGuar_nfb").val(grid.jqGrid('getCell', selRowId, 'eGuar'));

                        $("#giftCity_nfb").val(grid.jqGrid('getCell', selRowId, 'giftCity'));

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

                        $("#guarType_nfb").val(mapCol + "~" + grid.jqGrid('getCell', selRowId, 'guarType'));
                        if (mapCol === "Yes") {
                            $("#guarIntRat_nfb").prop("disabled", false);
                            $("#guarExtRat_nfb").prop("disabled", false);
                        } else {
                            $("#guarIntRat_nfb").val("");
                            $("#guarExtRat_nfb").val("");
                            $("#guarIntRat_nfb").prop("disabled", true);
                            $("#guarExtRat_nfb").prop("disabled", true);
                        }

                        $("#guarIntRat_nfb").val(grid.jqGrid('getCell', selRowId, 'guarIntRat'));
                        $("#guarExtRat_nfb").val(grid.jqGrid('getCell', selRowId, 'guarExtRat'));
                        $("#astType_nfb").val(grid.jqGrid('getCell', selRowId, 'astType'));
                        $("#ltRating_nfb").val(grid.jqGrid('getCell', selRowId, 'ltRating'));
                        $("#stRating_nfb").val(grid.jqGrid('getCell', selRowId, 'stRating'));
                    }
                },
                position: "last"
            })
            .navButtonAdd('#pager_nfb', {
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
                                            $('#grid_nfb').trigger('reloadGrid');
                                            $('#grid_bonds').trigger('reloadGrid');
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