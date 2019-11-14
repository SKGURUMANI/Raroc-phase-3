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

/*   
 *  RAROC - Raroc Output Table
 */
$(function () {

    function totalFormat(cellvalue, options, rowObject) {
        if (rowObject.id === 23) {
            cellvalue = (parseFloat(cellvalue) * 100).toFixed(2) + "%";
        } else if (rowObject.id === 4 || rowObject.id === 5 || rowObject.id === 9 || rowObject.id === 13
                || rowObject.id === 14 || rowObject.id === 15 || rowObject.id === 16 || rowObject.id === 17
                || rowObject.id === 18 || rowObject.id === 19 || rowObject.id === 20 || rowObject.id === 21
                || rowObject.id === 22) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    function format(cellvalue, options, rowObject) {
        if (rowObject.id === 1 || rowObject.id === 2 || rowObject.id === 3 || rowObject.id === 6 || rowObject.id === 7
                || rowObject.id === 8 || rowObject.id === 10 || rowObject.id === 11 || rowObject.id === 12 || rowObject.id === 23) {
            cellvalue = (parseFloat(cellvalue) * 100).toFixed(2) + "%";
        } else if (rowObject.id === 4 || rowObject.id === 5 || rowObject.id === 9 || rowObject.id === 13
                || rowObject.id === 14 || rowObject.id === 15 || rowObject.id === 17 || rowObject.id === 18
                || rowObject.id === 19 || rowObject.id === 20 || rowObject.id === 21 || rowObject.id === 22) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    var grid = $("#grid3"),
            URL = 'rarocAdmin/view/rarocTab/grid?ref=' + $("#ref").val(),
            params0 = {
                name: 'id',
                sorttype: 'int',
                hidden: true
            },
    params1 = {
        name: 'outputName',
        label: 'Parameter',
        width: '220',
        sortable: false,
        formatter: function (cellvalue, options, rowObject) {
                return '<span style="background-color: #F5F5F5; display: block; width: 100%; height: 100%; ">' + cellvalue + '</span>';
        }
    },
    params2 = {
        name: 'facility1',
        label: 'Facility 1',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 0 ? false : true
    },
    params3 = {
        name: 'facility2',
        label: 'Facility 2',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 1 ? false : true
    },
    params4 = {
        name: 'facility3',
        label: 'Facility 3',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 2 ? false : true
    },
    params5 = {
        name: 'facility4',
        label: 'Facility 4',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 3 ? false : true
    },
    params6 = {
        name: 'facility5',
        label: 'Facility 5',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 4 ? false : true
    },
    params7 = {
        name: 'facility6',
        label: 'Facility 6',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 5 ? false : true
    },
    params8 = {
        name: 'total',
        label: 'Total',
        align: 'center',
        formatter: totalFormat,
        sortable: false
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params5, params6, params7, params8],
        caption: 'RAROC Calculation - Output',
        loadonce: true,
        pager: '#pager3',
        rowNum: 22,
        pgbuttons: false,
        pgtext: null,
        viewrecords: false,
        rownumbers: true,
        height: '300',
        loadComplete: function () {
            grid.jqGrid("setGridWidth", $("#jqgrid3").width() - 2);
        },
        onHeaderClick: function () {
            grid.jqGrid("setGridWidth", $("#jqgrid3").width() - 2);
        },
        footerrow: true,
        userDataOnFooter: true
    };

    grid.jqGrid(options).navGrid("#pager3", {
        edit: false,
        add: false,
        del: false,
        search: true,
        refresh: false
    }, {}, {}, {}, {});
    grid.jqGrid("bindKeys", {
        scrollingRows: true
    });

});
