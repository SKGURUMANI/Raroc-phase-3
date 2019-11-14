function errorFunc(rowid, response) {
    $message.dialog('option', 'title', 'Server Message: Error');
    if (response.status === 2001) {
        $message.html("Duplicate Key").dialog('open');
    } else if (response.status === 2002) {
        $message.html("Database error. Please check the error log.").dialog('open');
    } else {
        $message.html("Internal error. Please check the error log.").dialog('open');
    }
}

$(function() {

    window.$message = $('<div></div>').dialog({
        autoOpen: false,
        resizable: false,
        height: 120,
        hide: "explode",
        buttons: {
            "Ok": function() {
                $(this).dialog("close");
            }
        }
    });
    var grid = $("#grid"),
            URL = 'admin/report/grid', lastSel = -1, editingRowId = -1;
    var editparameters = {
        keys: true,
        url: URL,
        extraparam: {},
        aftersavefunc: reload,
        errorfunc: errorFunc,
        restoreAfterError: false,
        mtype: "POST"
    },
    parameters = {
        edit: true,
        add: true,
        editParams: {
            keys: true,
            url: URL,
            restoreAfterError: false,
            aftersavefunc: reload,
            errorfunc: errorFunc
        },
        addParams: {
            addRowParams: {
                keys: true,
                url: URL,
                restoreAfterError: false,
                aftersavefunc: reload,
                errorfunc: errorFunc,
                mtype: 'POST'
            }
        }
    },
    delOptions = {
        onclickSubmit: function(params, postdata) {
            params.url = URL + '?repId=' + grid.jqGrid('getCell', postdata, 'id');
        }
    };

    function reload(rowid, response) {
        grid.trigger("reloadGrid");
    }

    var params0 = {
        name: 'id',
        hidden: true,
        editable: true
    },
    params1 = {
        name: 'repId',
        index: 'V_REPORT_ID',
        width: '60',
        editable: false,
        label: $("#col1").text()
    },
    params2 = {
        name: 'repName',
        index: 'V_REPORT_NAME',
        width: '300',
        editable: true,
        label: $("#col2").text()
    },
    params3 = {
        name: 'repGrp',
        index: 'V_GROUP',
        width: '175',
        editable: true,
        edittype: 'select',
        editoptions: {value: "Capital Computation:Capital Computation;Data Quality:Data Quality"},
        label: $("#col3").text()
    },
    params4 = {
        name: 'repView',
        index: 'V_VIEW_NAME',
        width: '175',
        editable: true,
        edittype: 'select',
        editoptions:{
            dataUrl: "admin/report/repView"
        },
        label: $("#col4").text()
    },
    params5 = {
        name: 'active',
        index: 'V_DEPLOYED',
        width: '175',
        editable: true,
        edittype: 'select',
        editoptions: {value: "Y:Yes;N:No"},
        label: $("#col5").text()
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params5],
        caption: $("#caption").text(),
        pager: '#pager',
        rowNum: 15,
        rowList: [15, 30, 45],
        shrinkToFit: true,
        beforeProcessing: function(data, status, xhr) {
            $("#_tk").val(xhr.getResponseHeader("_tk"));
        },
        loadComplete: function() {
            if (grid.jqGrid("getGridParam", 'reccount') > 15) {
                grid.jqGrid("setGridHeight", 350);
            } else {
                grid.jqGrid("setGridHeight", 'auto');
            }
            setGridWidth(grid);
        },
        ondblClickRow: function(rowid) {
            grid.jqGrid('editRow', rowid, editparameters);
            return;
        }
    };

    grid.jqGrid(options).navGrid("#pager", {
        edit: false,
        add: false
    }, {}, {}, delOptions, {
        sopt: ['eq', 'ne', 'cn', 'nc', 'bw', 'ew', 'nu', 'nn']
    })
            .navSeparatorAdd("#pager", {sepclass: 'ui-separator', sepcontent: ''})
            .navButtonAdd('#pager', {
                caption: "Next",
                buttonicon: "ui-icon-seek-next",
                title: "Next",
                position: "first",
                onClickButton: function() {
                    var selRowId = grid.jqGrid('getGridParam', 'selrow');
                    if (selRowId !== null) {
                        var repId = grid.jqGrid('getCell', selRowId, 'id'),
                                repName = grid.jqGrid('getCell', selRowId, 'repName'),
                                path = "admin/report/page/" + repId+"/"+repName;
                        $('#right').html("<div class='loading'><img alt='Loading' src='resources/css/images/loader.gif'/></div>");
                        $.get(path, function(data) {
                            $('#right').html(data);
                        });
                    } else {
                        $message.dialog('option', 'modal', true);
                        $message.dialog('option', 'title', 'Info');
                        $message.html("Please select a row").dialog('open');
                    }

                }
            });
    grid.jqGrid(options).bindKeys();
    grid.jqGrid(options).setSelection("1");
    grid.jqGrid(options).inlineNav('#pager', parameters);
});