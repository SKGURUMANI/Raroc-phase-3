/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function() {    
    
    /*   
     *  LOG - Process log
     */              
    var grid = $("#grid"),
    URL = 'admin/prsLog/grid',
    params0 = { 
        name :'id',
        sorttype: 'int',
        hidden: true
    },
    params1 = { 
        name: 'batchId',
        index: 'v_batch_id',
        width: '150',
        label: $("#col1").text()
    },
    params2 = { 
        name: 'task',
        index: 'v_task_name',
        width: '300',
        label: $("#col2").text()
    },
    params3 = { 
        name: 'stime',       
        index: 't_start',
        width: '175',
        label: $("#col3").text()
    },
    params4 = { 
        name: 'etime',            
        index: 't_end',
        width: '175',
        label: $("#col4").text()
    },
    params5 = { 
        name:'status',
        index: 'v_status',
        label: $("#col5").text()
    },
    params6 = {
        name:'remarks',
        index: 'v_remarks',
        label: $("#col6").text()
    },
    params7 = {
        name:'error',
        index: 'v_error_details',
        label: $("#col7").text()
    },
    options = {
        url : URL,         
        colModel : [params0, params1, params2, params3, params4, params5, params6, params7],
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
        sortorder: 'desc'
    };

    grid.jqGrid(options).navGrid("#pager",{
        edit : false, 
        add : false, 
        del : false,
        search : true
    },{},{},{},{
        sopt: ['eq','ne','cn','nc','bw','ew','nu','nn']
    });
    grid.jqGrid("bindKeys", {scrollingRows: true});
        
});