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
    
    var grid = $("#grid");
    
    function reload(rowid, response) {            
        grid.trigger("reloadGrid");        
    }        
            
    /*   
     *  Master Table Configuration
     */          
    var URL = 'admin/defrag/grid', lastSel1 = -1, editingRowId1 = -1,    
    parameters = {
        add: true,
        edit: false,
        editParams: {            
            keys : true,                
            url : URL,
            oneditfunc : null,
            restoreAfterError : true,
            aftersavefunc: reload          
        },
        addParams: {            
            addRowParams: {
                keys : true,                
                url : URL,
                afterrestorefunc : null,
                restoreAfterError : null,
                aftersavefunc: reload,
                mtype : 'POST'        
            }
        }
    };                      
    function fnTableList(path, rowId, row) {
        $.getJSON(path, function(data){
            var html;
            var len = data.length;
            for (var i = 0; i< len; i++) {
                html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
            }
            $("select#" + rowId + "_tableName", row[0]).empty();
            $("select#" + rowId + "_tableName", row[0]).append(html);
        });
    }
    var delOptions = {        
        onclickSubmit : function(params, postdata) {
            params.url = URL + '/' + grid.getCell(postdata, 'id');
        }
    },
    params0 = { 
        name :'id',            
        index :'id',
        editable: true,
        hidden : true
    },
    params1 = { 
        name : 'schemaName',
        index : 'v_schema_name',
        label : 'Schema Name',
        editable: true,
        edittype: 'select',
        editoptions:{
            dataUrl: "admin/defrag/schemaList",
            dataInit: function (elem) {                
                var row = $(elem).closest('tr.jqgrow'), path;
                var rowId = row.attr('id');                
                path = "admin/defrag/tableList/"+$(elem).val();                                
                fnTableList(path, rowId, row);
            },
            dataEvents: [{
                type: 'change',
                fn: function(e) {                    
                    var row = $(e.target).closest('tr.jqgrow');
                    var rowId = row.attr('id');
                    var path="admin/defrag/tableList/"+$(e.target).val();
                    fnTableList(path, rowId, row);
                }
            }]
        }
    },
    params2 = { 
        name : 'tableName',
        index : 'v_table_name',
        label : 'Table Name',
        editable: true,
        edittype: 'select',
        editoptions: {value: {}},
        width: '300'
    },
    options = {
        url : URL,         
        colModel : [params0, params1, params2],
        pager : '#pager',
        rownumbers: true,
        rowNum : 15,
        rowList: [15, 30, 50],
        loadComplete: function() {
            if(grid.jqGrid("getGridParam", 'reccount') > 15) {
                grid.jqGrid("setGridHeight", 350);                                
            } else {
                grid.jqGrid("setGridHeight", 'auto');
            }
            setGridWidth(grid, "#jqgrid");
        },
        onHeaderClick: function() {
            setGridWidth(grid, "#jqgrid");
        },
        onSelectRow : function(rowid) {
            if (rowid && rowid !== lastSel1) {
                grid.jqGrid('restoreRow',lastSel1);
                lastSel1 = rowid;
            }
        }
    };

    grid.jqGrid(options).navGrid("#pager",{
        edit : false, 
        add : false     
    },{},{},delOptions,{
        sopt: ['eq','ne','cn','nc','bw','ew','nu','nn']
    })    
    .navSeparatorAdd('#pager',{
        sepclass : "ui-separator"
    })
    .navButtonAdd('#pager',{
        caption:"Defrag",
        buttonicon:"ui-icon-check", 
        position: "last",
        onClickButton: function() {
            var path = "admin/defrag/execute";
            $.getJSON(path, function(data) {
               BootstrapDialog.show({
                        title: 'Server Message',
                        message: data.mesgValue
                    });
            });            
        }
    });
    grid.jqGrid(options).bindKeys();    
    grid.jqGrid(options).setSelection("1");  
    grid.jqGrid(options).inlineNav('#pager',parameters);    
});