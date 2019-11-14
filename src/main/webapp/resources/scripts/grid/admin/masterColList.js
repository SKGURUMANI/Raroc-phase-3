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

//List Script Dialogs
    var dialog, form,
            lists = $("#lValue"),
            table = $("#tab"),
            column = $("#hidd"),
            allFields = $([]).add(lists).add(table).add(column),
            tips = $(".validateTips");

    function updateTips(t) {
        tips.text(t).addClass("ui-state-highlight");
        setTimeout(function () {
            tips.removeClass("ui-state-highlight", 1500);
        }, 500);
    }

    function isNumber(list, dataType, tab, col) {
        if (dataType === "NUMBER") {
            if (/^(\d{1,2},)*\d{1,2}$/.test(list)) {
                return true;
            } else {
                $(this).addClass("ui-state-error");
                updateTips("Number Field");
                return false;
            }
        } else if (dataType === "VARCHAR2") {
            var message = $.ajax({
                type: 'GET',
                url: 'admin/val/dataLength?tab=' + tab + '&col=' + col + '&sList=' + list,
                data: $(this).serialize(),
                dataType: 'text',
                global: false,
                async: false,
                success: function (data) {
                    return data;
                }
            }).responseText;
            if (message === 'less') {
                return true;
            } else {
                $(this).addClass("ui-state-error");
                updateTips("Column Length Exceed");
                return false;
            }
        } else if (dataType === "DATE") {
            return true;
        }

    }

    var detailsWindow = function (cellvalue) {
        cellvalue = cellvalue === null ? "" : cellvalue;
        var content = cellvalue.replace(/,/g, "<br />");
        return "<a class=\"link\" onclick=\"return hs.htmlExpand(this, {width: 300, dimmingOpacity: 0.60, headingText:'Column Details', maincontentText:'" + content + "'});\">" + cellvalue + "</a>";
    };

    function checkLength(o, n, min, max) {
        if (o.length > max || o.length < min) {
            $(this).addClass("ui-state-error");
            updateTips("Length of " + n + " must be between " +
                    min + " and " + max + ".");
            return false;
        } else {
            return true;
        }
    }

    function checkRegexp(o, regexp, n) {
        if (!(regexp.test(o.val()))) {
            o.addClass("ui-state-error");
            updateTips(n);
            return false;
        } else {
            return true;
        }
    }


    dialog = $("#dialog-form").dialog({
        autoOpen: false,
        height: 380,
        width: 330,
        modal: true,
        hide: "explode",
        buttons: {
            "Save": function () {
                var valid = true;
                allFields.removeClass("ui-state-error");
                var list = $("#lValue").val();
                var tab = $("#tab").val();
                var col = $("#hidd").val();
                var dType = $("#dType").val();
                var path = "admin/master/saveList?tab=" + tab + "&col=" + col + "&scrpt=" + list;

                //valid = valid && checkLength(table, "Table", 3, 16);
                valid = valid && isNumber(list, dType, tab, col);
                if (valid) {
                    $.get(path);
                    dialog.dialog("close");
                    grid.trigger("reloadGrid");
                }
            },
            Cancel: function () {
                dialog.dialog("close");
                grid.trigger("reloadGrid");
            }
        },
        close: function () {
            form[ 0 ].reset();
            allFields.removeClass("ui-state-error");
            grid.trigger("reloadGrid");
        }
    });

    form = dialog.find("#dForm").on("submit", function (event) {
        event.preventDefault();

    });

//    

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
            table = $("#tableId").val(),
            URL = 'admin/master/col/grid/' + table, lastSel = -1, editingRowId = -1;
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

    var getColumnIndexByName = function (grid, columnName) {
        var cm = grid.jqGrid('getGridParam', 'colModel'), i, l;
        for (i = 0, l = cm.length; i < l; i += 1) {
            if (cm[i].name === columnName) {
                return i; // return the index
            }
        }
        return -1;
    };
    function checkBoxFormatter(cellvalue, options, rowObject) {
        var html;
        if (cellvalue === "Yes") {
            html = "<input type='checkbox' checked='checked' value='" + cellvalue + "' />";
        } else {
            if (rowObject.isfk === "Y") {
                html = "<input type='checkbox' value='" + cellvalue + "'  disabled='disabled' />";
            } else {
                html = "<input type='checkbox' value='" + cellvalue + "' />";
            }
        }
        return html;
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
        name: 'hide',
        index: 'V_STATUS',
        label: 'Hidden',
        edittype: 'select',
        editoptions: {value: "true:True;false:False"},
        editable: true
    }, params7 = {
        name: 'align',
        index: 'V_ALIGNMENT',
        label: 'Alignment',
        edittype: 'select',
        editoptions: {value: "left:Left;right:Right;center:Center"},
        editable: true
    }, params8 = {
        name: 'islist',
        index: 'F_IS_LIST',
        label: 'List Flag',
        align: 'center',
        formatter: checkBoxFormatter,
        formatoptions: {disabled: false},
        editoptions: {value: "Yes:No", defaultValue: "Yes"},
        stype: "select",
        searchoptions: {
            sopt: ["eq", "ne"],
            value: ":Any;true:Yes;false:No"
        }
    }, params9 = {
        name: 'listscript',
        index: 'V_LIST_SCRIPT',
        formatter: detailsWindow,
        label: 'List Script'
    }, params10 = {
        name: 'isfk',
        index: 'F_IS_FK',
        label: 'Foreign Key',
        hidden: true
    }, params11 = {
        name: 'dependent',
        index: 'V_DEPENDENT_COL',
        label: 'Dependent Column',
        edittype: 'select',
        editoptions:{
            dataUrl: "admin/master/selectTabCol/"+table
        },
        editable: true
    },
    options = {
        url: URL,
        colModel: [params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11],
        caption: 'Column List of ' + toTitleCase(table),
        pager: '#pager',
        rowNum: 15,
        rownumbers: true,
        rowList: [15, 30, 45],
        shrinkToFit: true,
        beforeProcessing: function (data, status, xhr) {
            $("#_tk").val(xhr.getResponseHeader("_tk"));
        },
        loadComplete: function () {
            var iCol = getColumnIndexByName($(this), 'islist'), rows = this.rows, i, c = rows.length;
            for (i = 0; i < c; i += 1) {
                $(rows[i].cells[iCol]).click(function (e) {
                    var id = $(e.target).closest('tr')[0].id, isChecked = $(e.target).is(':checked');
                    if (isChecked) {
                        $("#hidd").val(grid.jqGrid('getCell', id, 'colcd'));
                        $("#dType").val(grid.jqGrid('getCell', id, 'datatype'));
                        dialog.dialog("open");
                    } else {
                        var tab = $("#tab").val();
                        var col = grid.jqGrid('getCell', id, 'colcd');
                        var list = "not_checked";
                        var path = "admin/master/saveList?tab=" + tab + "&col=" + col + "&scrpt=" + list;
                        $.get(path);
                        grid.trigger("reloadGrid");
                    }
                    //alert('clicked on the checkbox in the row with id=' + id + '\nNow the checkbox is ' + (isChecked ? 'checked' : 'not checked'));
                });
            }

            if (grid.jqGrid("getGridParam", 'reccount') > 15) {
                grid.jqGrid("setGridHeight", 350);
            } else {
                grid.jqGrid("setGridHeight", 'auto');
            }
            setGridWidth(grid);
        },
        postData: {
            reportId: function () {
                return $("#reportId").val();
            }
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