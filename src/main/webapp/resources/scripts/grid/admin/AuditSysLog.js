/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {    
    
    $(".butn").button();
    
    $("#from").datetimepicker({
        format: "DD-MMM-YYYY"
    });
    $("#to").datetimepicker({
        format: 'DD-MMM-YYYY'
    });
    
    /*   
     *  LOG - System log
     */              
    var grid = $("#grid"),
    URL = 'admin/sysLog/grid',
    params0 = { 
        name :'id',
        sorttype: 'int',
        hidden: true
    },
    params1 = { 
        name: 'userId',
        index: 'a.v_user_id',
        label: $("#col1").text()
    },
    params2 = { 
        name: 'userName',       
        index: 'c.v_username',
        label: $("#col2").text()
    },
    params3 = { 
        name: 'roles',            
        index: 'a.v_action',
        label: $("#col3").text()
    },
    params4 = { 
        name:'address',
        index: 'a.v_action_result',
        label: $("#col4").text()
    },
    params5 = {
        name:'activeStr',
        index: 'a.v_ip_address',
        label: $("#col5").text()
    },
    params6 = {
        name:'sessionTime',
        index: 'a.t_time',
        search: false,
        label: $("#col6").text()
    },
    options = {
        url : URL,         
        colModel : [params0, params1, params2, params3, params4, params5, params6],
        pager : '#pager',
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
        postData: {
            fromDate: function() {return $("#from").val();},
            toDate: function() {return $("#to").val();}
        },
        sortorder: 'desc'
    };

    grid.jqGrid(options).navGrid("#pager",{
        edit : false, 
        add : false, 
        del : false,
        search : true
    },{},{},{},{
        sopt: ['eq','ne','cn','nc','bw','ew','nu','nn']
    })
    .navSeparatorAdd('#pager',{
        sepclass : "fa fa-ellipsis-v"
    })
    .navButtonAdd('#pager',{
        caption:"XLS", 
        buttonicon:"fa fa-file-excel-o",
        onClickButton: function() {
            var postData = $(this).jqGrid("getGridParam", "postData");
            if(postData.searchField) {
                window.location.href = "admin/sysLog?viewtype=xls&fromDate="+$("#from").val()+"&toDate="+$("#to").val()+
                    "&searchField="+postData.searchField+"&searchOper="+postData.searchOper+
                    "&searchString="+postData.searchString;
            } else {
                window.location.href = "admin/sysLog?viewtype=xls&fromDate="+$("#from").val()+"&toDate="+$("#to").val();
            }            
        }
    })
    .navSeparatorAdd('#pager',{
        sepclass : "fa fa-ellipsis-v"
    })
    .navButtonAdd('#pager',{
        caption:"PDF", 
        buttonicon:"fa fa-file-pdf-o", 
        onClickButton: function() {
            var postData = $(this).jqGrid("getGridParam", "postData");
            if(postData.searchField) {
                window.location.href = "admin/sysLog?viewtype=pdf&fromDate="+$("#from").val()+"&toDate="+$("#to").val()+
                    "&searchField="+postData.searchField+"&searchOper="+postData.searchOper+
                    "&searchString="+postData.searchString;
            } else {
                window.location.href = "admin/sysLog?viewtype=pdf&fromDate="+$("#from").val()+"&toDate="+$("#to").val();
            }            
        }
    });

    grid.jqGrid("bindKeys", {scrollingRows: true});
     
    $("#filterlog").click(function() {
        grid.trigger("reloadGrid");
    });
        
});