/* 
 * jqGrid Default Configuration
 */

function errorFunc(rowid, response) {
    if (response.status === 2001) {
        BootstrapDialog.show({
            title: 'Server Message',
            message: 'Duplicate Key'
        });
    } else if (response.status === 2002) {
        BootstrapDialog.show({
            title: 'Error Message',
            message: 'Database error. Please check the error log.'
        });
    } else {
        BootstrapDialog.show({
            title: 'Error Message',
            message: 'Internal error. Please check the error log.'
        });

    }
}

$.extend($.jgrid.defaults, {
    datatype: 'json',
    jsonReader: {
        repeatitems: false,
        total: function (result) {
            //Total number of pages
            return Math.ceil(result.total / result.max);
        },
        records: function (result) {
            //Total number of records
            return result.total;
        }
    },
    prmNames: {
        rows: 'max',
        search: null
    },
    autoencode: true,
    viewrecords: true,
    autowidth: true,
    altRows: true,
    altclass: 'gridAltRow',
    gridview: true, // Not to be used with treeGrid, subGrid and afterInsertRow
    emptyrecords: 'No data to view',
    height: 'auto',
    shrinkToFit: true,
    ajaxRowOptions: {
        contentType: "application/json",
        beforeSend: function (jqXHR) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            jqXHR.setRequestHeader(header, token);
        },
        async: true
    },
    serializeRowData: function (data) {
        var propertyName, propertyValue, dataToSend = {};
        for (propertyName in data) {
            if (data.hasOwnProperty(propertyName)) {
                propertyValue = data[propertyName];
                if ($.isFunction(propertyValue)) {
                    dataToSend[propertyName] = propertyValue();
                } else {
                    dataToSend[propertyName] = propertyValue;
                }
            }
        }
        return JSON.stringify(dataToSend);
    },
    loadError: function (xhr, status, error) {
        BootstrapDialog.show({
            title: 'Server Message: Error',
            message: status + '-' + error
        });
    }
});

$.extend($.jgrid.del, {
    mtype: 'DELETE',
    ajaxDelOptions: {
        contentType: "application/json",
        beforeSend: function (jqXHR) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            jqXHR.setRequestHeader(header, token);
        }
    },
    serializeDelData: function () {
        return "";
    }
});

//$.extend($.jgrid.del, {
//    mtype : 'DELETE',
//    ajaxDelOptions : {
//        contentType : "application/json",            
//        beforeSend : function(jqXHR) {
//            jqXHR.setRequestHeader("_tk", $("#_tk").val());
//        }            
//    },
//    serializeDelData : function() {            
//        return "";
//    }
//});

function setGridWidth(grid) {
    grid.jqGrid("setGridWidth", $("#jqgrid").width() - 2);
}