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
$(function() {  
    
    $(".butn").button();    
    
    $("#summary-butn").click(function() {
        if($("#summary-div").is(":visible")) {           
            $("#summary-butn").removeClass("tabVisible");
            $("#summary-butn").addClass("tabHidden");
            $("#summary-div").hide();  
            $("#summary-header").addClass("ui-corner-bottom");
        } else {
            $("#summary-butn").removeClass("tabHidden");
            $("#summary-butn").addClass("tabVisible"); 
            $("#summary-header").removeClass("ui-corner-bottom");
            $("#summary-div").show(); 
        }       
    });
    
    var initialized = [false, false, false, false, false], path;
    $("#juiTabs").tabs({
        show: function(event, ui) {
            if (ui.index === 1 && !initialized[1]){                
                $.get("rarocAdmin/view/rwaTab", function(html) {
                    $('#tabs-2').html(html);
                });
            }
            if (ui.index === 2 && !initialized[2]){                
                $.get("rarocAdmin/view/rarocTab", function(html) {
                    $('#tabs-3').html(html);
                });
            }
            if (ui.index === 3 && !initialized[3]){                
                path = "rarocAdmin/view/sensRwTab?ref="+$("#ref").val();
                $.get(path, function(html) {
                    $('#tabs-4').html(html);
                    drawRWChart("rarocAdmin");
                });                                
            }
            if (ui.index === 4 && !initialized[4]){                
                path = "rarocAdmin/view/sensUtilTab?ref="+$("#ref").val();
                $.get(path, function(html) {
                    $('#tabs-5').html(html);
                    drawUtilChart("rarocAdmin");
                });                
            }
            initialized[ui.index] = true;        
        }
    }).css({
        'min-height': '450px',
        'overflow': 'auto'
    });
    
    /*   
     *  RAROC - Raroc Output Table
     */    
    
    function totalFormat(cellvalue, options, rowObject) {
        if(rowObject.id === 43) {
            cellvalue = (parseFloat(cellvalue)*100).toFixed(2)+"%";
        } else if (rowObject.id === 31 || rowObject.id === 32 || rowObject.id === 34 || rowObject.id === 35 || 
            rowObject.id === 36 || rowObject.id === 37 || rowObject.id === 38 || rowObject.id === 39 ||
            rowObject.id === 40 || rowObject.id === 41 || rowObject.id === 42) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    function creditFormat(cellvalue, options, rowObject) {
        if(rowObject.id === 43) {
            cellvalue = (parseFloat(cellvalue)*100).toFixed(2)+"%";
        } else if (rowObject.id === 31 || rowObject.id === 32 || rowObject.id === 34 || 
            rowObject.id === 35 || rowObject.id === 37 || rowObject.id === 38 || rowObject.id === 39 ||
            rowObject.id === 40 || rowObject.id === 41 || rowObject.id === 42) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    function format(cellvalue, options, rowObject) {
        if(rowObject.id === 43 || rowObject.id === 11 || rowObject.id === 13 || rowObject.id === 14 || rowObject.id === 17
            || rowObject.id === 21 || rowObject.id === 25 || rowObject.id === 26 || rowObject.id === 27 || rowObject.id === 28
            || rowObject.id === 29 || rowObject.id === 30 || rowObject.id === 33) {
            cellvalue = (parseFloat(cellvalue)*100).toFixed(2)+"%";
        } else if (rowObject.id === 8 || rowObject.id === 15 || rowObject.id === 16 || rowObject.id === 31 || 
            rowObject.id === 32 || rowObject.id === 34 || rowObject.id === 35 || rowObject.id === 37 || rowObject.id === 38
            || rowObject.id === 39 || rowObject.id === 40 || rowObject.id === 41 || rowObject.id === 42) {
            cellvalue = parseFloat(cellvalue).toFixed(2);
        }
        return cellvalue === null ? "" : cellvalue;
    }
    var grid = $("#grid1"),
    URL = 'rarocAdmin/view/grid?ref='+$("#ref").val(),    
    params0 = { 
        name :'id',
        sorttype: 'int',
        hidden: true
    },
    params1 = { 
        name: 'outputName',
        label: 'Parameter',
        width: '220',
        sortable: false
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
        name: 'creditRaroc',            
        label: 'Credit Total',
        align: 'center',
        formatter: creditFormat,
        sortable: false        
    },
    params9 = { 
        name: 'total',            
        label: 'Customer Total',
        align: 'center',
        formatter: totalFormat,
        sortable: false 
    },
    options = {
        url : URL,
        colModel : [params0, params1, params2, params3, params4, params5, params6, params7, params8, params9],
        caption : 'RAROC',
        loadonce: true,
        pager: '#pager1',
        rowNum: 45,
        pgbuttons: false,
        pgtext: null,
        viewrecords: false,       
        height: '300',
        loadComplete: function() {             
            grid.jqGrid("setGridWidth", $("#jqgrid1").width() - 2);
        },
        onHeaderClick: function() {
            grid.jqGrid("setGridWidth", $("#jqgrid1").width() - 2);
        },
        footerrow: true,
        userDataOnFooter : true
    };

    grid.jqGrid(options).navGrid("#pager1",{
        edit : false, 
        add : false, 
        del : false,
        search : true,        
        refresh : false
    },{},{},{},{})
    .navButtonAdd('#pager1', {
        caption: "",
        buttonicon: "ui-icon-disk",
        onClickButton: function() {                  
            window.location.href = 'rarocAdmin/view?ref='+$("#ref").val()+'&viewType=XLS';
        }
    });
    grid.jqGrid("bindKeys", {
        scrollingRows: true
    });    
    
    $("input[type=submit]").click(function(e) {
        e.preventDefault();
        var path = $("#rarocAuthorize").attr('action');
        if ($(this).val() === "Reject") {
            path = path + "&action=R";
        } else {
            path = path + "&action=A";
        }        
        var data = $("#rarocAuthorize").serialize(); 
        $.ajax({
            type: 'POST',
            url: path,
            data: data,
            global: false,
            async: false,
            success: function() {
                location.reload();
            }
        });
    });
    
});