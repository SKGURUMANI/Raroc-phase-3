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

function toTitleCase(str) {
    var res = str.substring(4);
    var replace = res.replace(/[^A-Z0-9]+/ig, " ") + " Master";
    return replace.replace(/\w\S*/g, function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
}

$(function () {

    window.$message = $('<div></div>').dialog({
        autoOpen: false,
        resizable: false,
        height: 120,
        hide: "explode",
        buttons: {
            "Ok": function () {
                $(this).dialog("close");
            }
        }
    });
    var grid = $("#grid"),
            repId = $("#rId").val(),
            repName = $("#rName").val(),
            URL = 'admin/report/col/grid/' + repId, lastSel = -1, editingRowId = -1;
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
        add: false,
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
        onclickSubmit: function (params, postdata) {
            params.url = URL + '?reportId=' + grid.jqGrid('getCell', postdata, 'row');
        }
    };

    function reload(rowid, response) {
        grid.trigger("reloadGrid");
    }


    var params1 = {
        name: 'id',
        index: 'id',
        hidden: true
    }, params2 = {
        name: 'colid',
        index: 'N_COLUMN_ID',
        label: 'Column ID',
        editable: true,
        hidden: true
    }, params3 = {
        name: 'colcd',
        index: 'V_COLUMN_CD',
        label: 'Column Code',
        editable: false
    }, params4 = {
        name: 'coldesc',
        index: 'V_COLUMN_DESC',
        label: 'Column Desc',
        editable: true
    }, params5 = {
        name: 'datatype',
        index: 'V_DATA_TYPE',
        label: 'Data Type',
        edittype: 'select',
        editoptions: {value: "VARCHAR2:Varchar;NUMBER:Number;DATE:Date;CLOB:Clob"},
        editable: true
    }, params6 = {
        name: 'header',
        index: 'F_HEADER_FLAG',
        label: 'Header',
        edittype: 'select',
        editoptions: {value: "N:No;Y:Yes"},
        editable: true
    }, params7 = {
        name: 'hide',
        index: 'F_HIDE_FLAG',
        label: 'Hide Flag',
        edittype: 'select',
        editoptions: {value: "N:No;Y:Yes"},
        editable: true
    }, params8 = {
        name: 'align',
        index: 'V_ALIGNMENT',
        label: 'Alignment',
        edittype: 'select',
        editoptions: {value: "left:Left;right:Right;center:Center"},
        editable: true
    },
    options = {
        url: URL,
        colModel: [params1, params2, params3, params4, params5, params6, params7, params8],
        caption: 'Column List of ' +repName+' Report',
        pager: '#pager',
        rowNum: 15,
        rownumbers: true,
        rowList: [15, 30, 45],
        shrinkToFit: true,
        beforeProcessing: function (data, status, xhr) {
            $("#_tk").val(xhr.getResponseHeader("_tk"));
        },
        loadComplete: function () {
            if (grid.jqGrid("getGridParam", 'reccount') > 15) {
                grid.jqGrid("setGridHeight", 350);
            } else {
                grid.jqGrid("setGridHeight", 'auto');
            }
            setGridWidth(grid);
        }
    };

    grid.jqGrid(options).navGrid("#pager", {
        edit: false,
        add: false,
        del: false
    }, {}, {}, {}, {
        sopt: ['eq', 'ne', 'cn', 'nc', 'bw', 'ew', 'nu', 'nn']
    })
    .navSeparatorAdd("#pager", {sepclass: 'ui-separator', sepcontent: ''})
    .navButtonAdd('#pager', {
        caption: "Back",
        buttonicon: "ui-icon-seek-prev",
        title: "Back",
        position: "first",
        onClickButton: function () {
            var path = "admin/master";
            $('#right').html("<div class='loading'><img alt='Loading' src='resources/css/images/loader.gif'/></div>");
            $.get(path, function (data) {
                $('#right').html(data);
                });
            }
        });
    grid.jqGrid(options).bindKeys();
    grid.jqGrid(options).setSelection("1");
    grid.jqGrid(options).inlineNav('#pager', parameters);
});