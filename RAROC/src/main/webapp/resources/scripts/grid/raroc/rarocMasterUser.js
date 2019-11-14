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

    var viewComment = function (rowid) {

        var ref = grid.getCell(rowid, 'rarocref');
        var path = "raroc/view/comments?ref=" + ref;
        return hs.htmlExpand(this, {
            src: path,
            numberPosition: 'none',
            objectType: 'ajax',
            width: 800,
            height: 400,
            headingText: 'Approver comments for ' + ref
        });
    };


    $("#create").click(function () {
        $.get("raroc/new", function (html) {
            $("#container-fluid").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
            $("#container-fluid").html(html);
        });
    });

    var grid = $("#grid"), URL = 'raroc/master/grid';
    var delOptions = {
        onclickSubmit: function (params, postdata) {
            params.url = URL + '/del?recref=' + grid.jqGrid('getCell', postdata, 'rarocref');
        },
        errorTextFormat: function (r) {
            return "Only Incomplete records can be deleted";
        }
    },
    params1 = {
        name: 'id',
        index: 'id',
        hidden: true
    }, params2 = {
        name: 'rarocref',
        index: 'v_rec_ref_no',
        label: 'Reference No',
        width: '30'
    }, params3 = {
        name: 'cid',
        index: 'v_rating_tool_id',
        label: 'Rating ID',
        width: '30'
    }, params4 = {
        name: 'cname',
        index: 'v_cust_name',
        label: 'Customer Name',
        width: '85'
    }, params5 = {
        name: 'cdate',
        index: 'd_created',
        label: 'Created On',
        width: '45'
    }, params6 = {
        name: 'mdate',
        index: 'd_modified',
        label: 'Modified On',
        width: '35'
    }, params7 = {
        name: 'status',
        index: 'f_status',
        label: 'Status',
        search: false,
        width: '30',
        formatter: "dynamicLink",
        formatoptions: {
            onClick: viewComment
        }
    }, params8 = {
        name: 'auser',
        index: 'v_approved',
        label: 'Approved By',
        width: '30'
    }, params9 = {
        name: 'adate',
        index: 'd_approved',
        label: 'Approved On',
        width: '40'
    }, options = {
        url: URL,
        colModel: [params1, params2, params3, params4, params5, params6, params7, params8, params9],
        caption: 'Reference no. wise RAROC',
        pager: '#pager',
        rowNum: 13,
        rowList: [13, 30, 45],
        datatype: 'json',
        emptyrecords: 'Nothing to view',
        loadComplete: function () {
            if (grid.jqGrid("getGridParam", 'reccount') > 15) {
                grid.jqGrid("setGridHeight", 350);
            } else {
                grid.jqGrid("setGridHeight", 'auto');
            }
            setGridWidth(grid, "#jqgrid");
        },
        onHeaderClick: function () {
            setGridWidth(grid, "#jqgrid");
        },
        beforeProcessing: function (data, status, xhr) {
            $("#_tk").val(xhr.getResponseHeader("_tk"));
        },
        sortorder: ''
    };

    grid.jqGrid(options).navGrid("#pager", {
        add: false,
        edit: false,
        del: false
    }, {}, {}, {}, {
        sopt: ['eq', 'ne', 'cn', 'nc', 'bw', 'ew', 'nu', 'nn']
    })
            .navButtonAdd('#pager', {
                caption: '',
                buttonicon: 'fa fa fa-eye',
                title: 'View Row',
                onClickButton: function () {
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    if (selRowId !== null) {
                        var path = "raroc/view?ref=" + grid.jqGrid('getCell', selRowId, 'rarocref');
                        $("#container-fluid").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
                        $.get(path, function (data) {
                            $('#container-fluid').html(data);
                        });
                    } else {
                        BootstrapDialog.show({
                            title: 'Info',
                            message: "Select a row to view"
                        });
                    }
                    return true;
                },
                position: "first"
            })
            .navButtonAdd('#pager', {
                caption: '',
                buttonicon: 'fa fa-edit',
                title: 'Edit Row',
                onClickButton: function () {
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    var status = grid.jqGrid('getCell', selRowId, 'status');
                    if (selRowId !== null) {
                        if (status === "Incomplete" || status === "Rejected") {
                            var path = "raroc/edit?ref=" + grid.jqGrid('getCell', selRowId, 'rarocref');
                            $("#container-fluid").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
                            $.get(path, function (data) {
                                $('#container-fluid').html(data);
                            });
                        } else {
                            BootstrapDialog.show({
                                title: 'Info',
                                message: "Only Rejected / Incomplete record can be edited"
                            });
                        }
                    } else {
                        BootstrapDialog.show({
                            title: 'Info',
                            message: "Select a row to view"
                        });
                    }
                    return true;
                },
                position: "first"
            })
            .navButtonAdd('#pager', {
                caption: '',
                buttonicon: 'fa fa-trash',
                title: 'Delete Selected Row',
                onClickButton: function () {
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    var status = grid.jqGrid('getCell', selRowId, 'status');
                    if (selRowId !== null) {
                        if (status === "Incomplete" || status === "Rejected") {
                            BootstrapDialog.show({
                                title: 'Info',
                                message: "Due you want to delete selected Record",
                                buttons: [{
                                        label: 'Delete',
                                        action: function (dialog) {
                                            var path = "raroc/del/grid?refNo=" + grid.jqGrid('getCell', selRowId, 'rarocref');
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
                                message: "Only Rejected / Incomplete record can be deleted"
                            });
                        }
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
    grid.focus();
    grid.jqGrid("bindKeys", {scrollingRows: true});
    grid.jqGrid(options).setSelection("1");
});