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
    var grid = $("#grid_bonds"),
            initialWidth = $("#jqgrid_bonds").width() - 2,
            afterWidth = initialWidth,
            refcode = $("#refId").val(),
            //refcode = "Ref-593",
            URL = 'raroc/facility/bonds/grid?recref=' + refcode, lastSel1 = -1,
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
        name: 'bookType',
        index: 'V_BOOK_TYPE',
        label: 'Book Type',
        align: 'left'
    },
    params6 = {
        name: 'rTenure',
        index: 'N_RESI_TENOR',
        label: 'Residual Tenor (Months)',
        align: 'right'
    },
    params7 = {
        name: 'yeild',
        index: 'N_YEILD',
        label: 'Yield (%)',
        align: 'right'
    },
    params8 = {
        name: 'coupon',
        index: 'V_COUPON_FREQ',
        label: 'Coupon payment frequency',
        align: 'left'
    },
    params9 = {
        name: 'region',
        index: 'V_REGION',
        label: 'Exposure location',
        align: 'left'
    },
    params10 = {
        name: 'cetExt',
        index: 'V_EXT_RATING',
        label: 'External Rating / CET 1',
        align: 'right'
    },
    params11 = {
        name: 'lcr',
        index: 'V_LCR',
        label: 'LCR Eligibility',
        align: 'left'
    },
    params12 = {
        name: 'uFee',
        index: 'N_UPFRONT_FEE',
        label: 'Upfront Fee',
        align: 'right'
    },
    params13 = {
        name: 'expIncome',
        index: 'N_EXP_TRADE',
        label: 'Expected trading income (Rs. Lakh)',
        align: 'right'
    },
    params14 = {
        name: 'cur',
        index: 'V_CURR',
        label: 'Currency',
        align: 'right'
    },
    params15 = {
        name: 'exgRate',
        index: 'N_EXCGE_RATE',
        label: 'Exchange Rate (Rs/Currency)',
        align: 'right'
    },
    params16 = {
        name: 'couponBond',
        index: 'N_COUPON',
        label: 'Coupon (%)',
        align: 'right'
    },
    params17 = {
        name: 'bondTenure',
        index: 'N_BOND_TENURE',
        label: 'Tenor of the Bond (in years)',
        align: 'right'
    },
    params18 = {
        name: 'tentative',
        index: 'N_BOND_HOLD_PRD',
        label: 'Tentative Holding period (in years)',
        align: 'right'
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params5, params6,
            params7, params8, params9, params10, params11, params12, params13,
            params14, params15, params16, params17, params18],
        gridview: true, // Not to be used with treeGrid, subGrid and afterInsertRow
        caption: 'List of Facilities - Bonds',
        pager: '#pager_bonds',
        autowidth: true,
        altRows: true,
        rowNum: 6,
        rowList: [6, 20, 40],
        emptyrecords: 'Nothing to view',
        height: 'auto',
        shrinkToFit: false,
        loadComplete: function () {
            afterWidth = $("#jqgrid_bonds").width() - 2;
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
    grid.jqGrid(options).navGrid("#pager_bonds", {
        edit: false,
        add: false,
        del: false,
        search: false,
        refresh: true
    }, {}, {}, {}, {
        sopt: ['cn', 'nc', 'bw', 'ew', 'eq']
    })
            .navButtonAdd('#pager_bonds', {
                caption: "Modify",
                buttonicon: "fa fa-edit",
                onClickButton: function () {
                    $('#mytabs a[href="#menu2"]').tab('show');
                    $("#butnA_bonds").hide();
                    $("#butnM_bonds").show();
                    $("#butnId_bonds").val("Modify");
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
                        $("#facNo_bonds").val(facId);
                        $("#facDesc_bonds").val(grid.jqGrid('getCell', selRowId, 'facDesc'));
                        $("#facTypes_bonds").val(grid.jqGrid('getCell', selRowId, 'facType'));

                        $("#bookType").val(grid.jqGrid('getCell', selRowId, 'bookType'));
                        $("#coupon").val(grid.jqGrid('getCell', selRowId, 'coupon'));
                        $("#lcr").val(grid.jqGrid('getCell', selRowId, 'lcr'));
                        $("#cur_bonds").val(grid.jqGrid('getCell', selRowId, 'cur'));
                        $("#rTenure").val(grid.jqGrid('getCell', selRowId, 'rTenure'));

                        $("#region_bonds").val(grid.jqGrid('getCell', selRowId, 'region'));
                        $("#uFee_bonds").val(grid.jqGrid('getCell', selRowId, 'uFee'));
                        $("#exgRate_bonds").val(grid.jqGrid('getCell', selRowId, 'exgRate'));

                        $("#amount_bonds").val(grid.jqGrid('getCell', selRowId, 'amount'));
                        $("#yeild").val(grid.jqGrid('getCell', selRowId, 'yeild'));
                        $("#cetExt").val(grid.jqGrid('getCell', selRowId, 'cetExt'));
                        $("#expIncome").val(grid.jqGrid('getCell', selRowId, 'expIncome'));
                        
                        $("#bondTenure").val(grid.jqGrid('getCell', selRowId, 'bondTenure'));
                        $("#couponBond").val(grid.jqGrid('getCell', selRowId, 'couponBond'));
                        $("#tentative").val(grid.jqGrid('getCell', selRowId, 'tentative'));
                    }
                },
                position: "last"
            }).navButtonAdd('#pager_bonds', {
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