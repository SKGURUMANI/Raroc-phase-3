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

      function totalFormat1(cellvalue, options, rowObject) {
        if (rowObject.id === 32 || rowObject.id === 14 || rowObject.id === 15 || rowObject.id === 16) {
            cellvalue = parseFloat(cellvalue).toFixed(2) + "%";
        }
        return cellvalue === null ? "" : cellvalue;
    } 

    function totalFormat(cellvalue, options, rowObject) {
        if (rowObject.id === 32) {
            cellvalue = parseFloat(cellvalue).toFixed(2) + "%";
        }
        return cellvalue === null ? "" : cellvalue;
       }
    
    function creditFormat(cellvalue, options, rowObject) {
        if (rowObject.id === 35 || rowObject.id === 37 || rowObject.id === 38 || rowObject.id === 39 ||
                rowObject.id === 41 || rowObject.id === 42) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    function format(cellvalue, options, rowObject) {
        if (rowObject.id === 14
                || rowObject.id === 15 || rowObject.id === 16 || rowObject.id === 11
                || rowObject.id === 5 || rowObject.id === 7 || rowObject.id === 32) {
            cellvalue = parseFloat(cellvalue).toFixed(2) + "%";
        } else if (rowObject.id === 17 || rowObject.id === 18
                || rowObject.id === 20
                || rowObject.id === 22
                || rowObject.id === 23 || rowObject.id === 24 || rowObject.id === 25
                || rowObject.id === 27 || rowObject.id === 28
                || rowObject.id === 29) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    
    var grid = $("#grid2"),
            URL = 'raroc/view/next/grid?ref=' + $("#ref").val(),
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
        name: 'facility7',
        label: 'Facility 7',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 6 ? false : true
    }, params9 = {
        name: 'facility8',
        label: 'Facility 8',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 7 ? false : true
    },
    params10 = {
        name: 'facility9',
        label: 'Facility 9',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 8 ? false : true
    },
    params11 = {
        name: 'facility10',
        label: 'Facility 10',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 9 ? false : true
    },
    params12 = {
        name: 'facility11',
        label: 'Facility 11',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 10 ? false : true
    },
    params13 = {
        name: 'facility12',
        label: 'Facility 12',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 11 ? false : true
    },
    params14 = {
        name: 'facility13',
        label: 'Facility 13',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 12 ? false : true
    },
    params15 = {
        name: 'facility14',
        label: 'Facility 14',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 13 ? false : true
    },
    params16 = {
        name: 'facility15',
        label: 'Facility 15',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 14 ? false : true
    },
    params17 = {
        name: 'facility16',
        label: 'Facility 16',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 15 ? false : true
    },
    params18 = {
        name: 'facility17',
        label: 'Facility 17',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 16 ? false : true
    },
    params19 = {
        name: 'facility18',
        label: 'Facility 18',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 17 ? false : true
    },
    params20 = {
        name: 'facility19',
        label: 'Facility 19',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 18 ? false : true
    },
    params21 = {
        name: 'facility20',
        label: 'Facility 20',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 19 ? false : true
    },
    params22 = {
        name: 'facility21',
        label: 'Facility 21',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 20 ? false : true
    },
    params23 = {
        name: 'facility22',
        label: 'Facility 22',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 21 ? false : true
    },
    params24 = {
        name: 'facility23',
        label: 'Facility 23',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 22 ? false : true
    },
    params25 = {
        name: 'facility24',
        label: 'Facility 24',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 23 ? false : true
    },
    params26 = {
        name: 'facility25',
        label: 'Facility 25',
        align: 'center',
        formatter: format,
        sortable: false,
        hidden: parseInt($("#cnt").val()) > 24 ? false : true
    },
//    params27 = {
//        name: 'creditRaroc',
//        label: 'Credit Total',
//        align: 'center',
//        formatter: creditFormat,
//        sortable: false
//    },
//    params28 = {
//        name: 'total',
//        label: 'Customer Total',
//        align: 'center',
//        formatter: totalFormat,
//        sortable: false
//    },
    params27 = {
        name: 'ca',
        label: 'CA',
        align: 'center',
        formatter: creditFormat,
        sortable: false
    },
    params28 = {
        name: 'sa',
        label: 'SA',
        align: 'center',
        formatter: totalFormat,
        sortable: false
    },
    params29 = {
        name: 'td',
        label: 'TD',
        align: 'center',
        formatter: totalFormat,
        sortable: false
    },
    params30 = {
        name: 'cms',
        label: 'CMS',
        align: 'center',
        formatter: totalFormat,
        sortable: false
    },
    params31 = {
        name: 'fx',
        label: 'FX',
        align: 'center',
        formatter: totalFormat,
        sortable: false
    },
    params32 = {
        name: 'other',
        label: 'Other',
        align: 'center',
        formatter: totalFormat,
        sortable: false
    },
    params33 = {
        name: 'total',
        label: 'Total',
        align: 'center',
        formatter: totalFormat1,
        sortable: false
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params5, params6, params7, params8, params9,
            params10, params11, params12, params13, params14, params15, params16, params17, params18, params19,
            params20, params21, params22, params23, params24, params25, params26, params27, params28,
            params29, params30, params31, params32, params33],
        caption: 'RAROC Next FY',
        loadonce: true,
        pager: '#pager2',
        rowNum: 45,
        pgbuttons: false,
        pgtext: null,
        viewrecords: false,
        rownumbers: true,
        height: '300',
        loadComplete: function () {
            grid.jqGrid("setGridWidth", $("#jqgrid2").width() - 2);
        },
        onHeaderClick: function () {
            grid.jqGrid("setGridWidth", $("#jqgrid2").width() - 2);
        },
        footerrow: true,
        userDataOnFooter: true
    };

    grid.jqGrid(options).navGrid("#pager2", {
        edit: false,
        add: false,
        del: false,
        search: true,
        refresh: false
    }, {}, {}, {}, {})
            .navButtonAdd('#pager2', {
                caption: "XLS",
                buttonicon: "fa fa-file-excel-o",
                onClickButton: function () {
                    window.location.href = 'raroc/view/next?ref=' + $("#ref").val() + '&viewType=XLS';
                }
            })
            .navButtonAdd('#pager2', {
                caption: "PDF",
                buttonicon: "fa fa-file-pdf-o",
                onClickButton: function () {
                    window.location.href = 'raroc/view/next?ref=' + $("#ref").val() + '&viewType=PDF';
                }
            });
    grid.jqGrid("bindKeys", {
        scrollingRows: true
    });

});