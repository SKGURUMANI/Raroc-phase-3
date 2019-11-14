/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {    
    
    $(".butn").button();
    
    $("#from").datetimepicker({
        format: "DD-MM-YYYY"
    });
    $("#to").datetimepicker({
        format: 'DD-MM-YYYY'
    });
    
    /*   
     *  LOG - Operstions log
     */              
    var grid = $("#grid"),
    URL = 'admin/opsLog/grid',
    params0 = { 
        name :'id',
        sorttype: 'int',
        hidden: true
    },
    params1 = { 
        name: 'stime',
        index: 'a.d_change_dt',
        search: false,
        label: $("#col1").text()
    },
    params2 = { 
        name: 'userid',
        index: 'a.v_maker_cd',
        label: $("#col2").text()
    },
    params3 = { 
        name: 'userName',       
        index: 'c.v_username',
        label: $("#col3").text()
    },
    params4 = { 
        name: 'task',            
        index: 'a.v_change_type',
        label: $("#col4").text()
    },
    params5 = { 
        name:'remarks',
        index: 'a.v_change_description',
        label: $("#col5").text()
    },
    params6 = {
        name:'status',
        index: 'a.v_change_status',
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
                window.location.href = "admin/opsLog?viewtype=xls&fromDate="+$("#from").val()+"&toDate="+$("#to").val()+
                    "&searchField="+postData.searchField+"&searchOper="+postData.searchOper+
                    "&searchString="+postData.searchString;
            } else {
                window.location.href = "admin/opsLog?viewtype=xls&fromDate="+$("#from").val()+"&toDate="+$("#to").val();
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
                window.location.href = "admin/opsLog?viewtype=pdf&fromDate="+$("#from").val()+"&toDate="+$("#to").val()+
                    "&searchField="+postData.searchField+"&searchOper="+postData.searchOper+
                    "&searchString="+postData.searchString;
            } else {
                window.location.href = "admin/opsLog?viewtype=pdf&fromDate="+$("#from").val()+"&toDate="+$("#to").val();
            }            
        }
    });

    grid.jqGrid("bindKeys", {scrollingRows: true});
     
    $("#filterlog").click(function() {
        grid.trigger("reloadGrid");
    });
        
});