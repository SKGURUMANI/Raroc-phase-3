/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {        
    
    /*   
     *  User Table Configuration
     */              
    var grid = $("#grid"), URL = 'admin/listUser', lastSel1 = -1,
    params0 = { 
        name :'id',            
        index :'id',       
        hidden : true
    },
    params1 = {
        name : 'userId',            
        index : 'v_user_id',
        label : $("#userid").val()
    },
    params2 = { 
        name : 'userName',            
        index : 'v_username',
        label : $("#userName").val()
    },
    params3 = { 
        name : 'email',            
        index : 'v_email',
        label : $("#email").val()
    },
    params4 = { 
        name : 'phone',
        index : 'v_phone',
        label : $("#phone").val()
    },
    params5 = { 
        name: 'activeStr',
        index: 'n_account_active',
        label: $("#active").val(),
        search: false
    },
    options = {
        url : URL,         
        colModel : [params0, params1, params2, params3, params4, params5],
        pager : '#pager',
        rowNum : 10,
        rowList: [10, 20, 50],
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
        }
    };

    grid.jqGrid(options).navGrid("#pager",{
        edit : false, 
        add : false, 
        del : false,
        search : true,        
        refresh : true
    },{},{},{},{
        sopt: ['eq','ne','cn','nc','bw','ew','nu','nn']
    })
    .navSeparatorAdd('#pager',{
        sepclass : "fa fa-ellipsis-v",
        sepcontent: ""
    })
    .navButtonAdd('#pager',{
        caption: $("#vbutn").val(),
        buttonicon:"fa fa-search", 
        onClickButton: function(){ 
            var selRowId = grid.jqGrid('getGridParam','selrow');
            if (selRowId !== null) {
                var id = $("#grid").jqGrid ('getCell', selRowId, 'userId');
                $("#page-wrapper").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
                $.get('admin/listUser/'+id,function(data) {
                    $('#page-wrapper').html(data);
                });
            } else {
                BootstrapDialog.show({
                           title: $("#iheader").val(),
                           message: $("#nalert").val()
                });
            }
        }, 
        position:"last"
    });    
    grid.jqGrid(options).bindKeys();    
    grid.jqGrid(options).setSelection("1");                 
});