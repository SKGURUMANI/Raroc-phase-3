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
    
    var viewComment = function(rowid) {
        var ref = grid.getCell(rowid, 'rarocref');
        var path = "rarocAuth/view/comments?ref=" + ref;
        return hs.htmlExpand(this, {
            src: path,
            numberPosition: 'none',
            objectType: 'ajax',
            width: 800,
            height: 400,
            headingText: 'Approver comments for ' + ref
        });
    };
    
//    window.$message = $('<div></div>').dialog({
//        autoOpen: false,
//        resizable: false,
//        height:120,
//        hide: "explode",
//        buttons: {
//            "Ok": function() {
//                $( this ).dialog( "close" );
//            }
//        }
//    });
        
    var grid = $("#grid"),
    URL = 'rarocAuth/master/grid';
    var params1 = {
        name: 'id',
        index: 'id',
        hidden: true
    }, params2 = {
        name: 'rarocref',
        index: 'v_rec_ref_no',
        label: 'Reference No',
        width: '30'
    }, params3 = {
        name: 'cid',
        index: 'v_rating_tool_id',
        label: 'Rating ID',
        width: '30'
    }, params4 = {
        name: 'cname',
        index: 'v_cust_name',
        label: 'Customer Name'
    }, params5 = {
        name: 'cuser',
        index: 'v_created',
        label: 'Created By',
        width: '50'
    }, params6 = {
        name: 'cdate',
        index: 'd_created',
        label: 'Created On',
        width: '50'
    }, params7 = {
        name: 'muser',
        index: 'v_modified',
        label: 'Modified By',
        width: '30'
    }, params8 = {
        name: 'mdate',
        index: 'd_modified',
        label: 'Modified On',
        width: '50'
    }, params9 = {
        name: 'status',
        index: 'f_status',
        label: 'Status',
        search: false,
        width: '70',
        formatter: "dynamicLink",
        formatoptions: {
            onClick: viewComment
        }
    }, params10 = {
        name: 'auser',
        index: 'v_approved',
        label: 'Approved By',
        width: '30'
    }, params11 = {
        name: 'adate',
        index: 'd_approved',
        label: 'Approved On',
        width: '50'
    }, options = {
        url: URL,
        colModel: [params1, params2, params3, params4, params5, params6, params7, params8, params9,
            params10, params11],
        caption: 'Reference no. wise RAROC',
        pager : '#pager',
        rowNum: 15,
        rowList: [15,30,45],
        emptyrecords: 'Nothing to view',
        loadComplete: function() {
            if(grid.jqGrid("getGridParam", 'reccount') > 15) {
                grid.jqGrid("setGridHeight", 350);                                
            } else {
                grid.jqGrid("setGridHeight", 'auto');
            }
            setGridWidth(grid);
        },
        beforeProcessing: function(data, status, xhr) {            
            $("#_tk").val(xhr.getResponseHeader("_tk"));
        },
        onHeaderClick: function() {
            setGridWidth(grid);
        },
        sortname: 'd_created',
        sortorder: 'desc'
    };
    
    grid.jqGrid(options).navGrid("#pager", {
        add:false,
        edit:false,
        del:false
    },{},{},{},{
        sopt: ['eq','ne','cn','nc','bw','ew','nu','nn']
    }).navSeparatorAdd('#pager',{
        sepclass : "ui-separator"
    }).navButtonAdd('#pager',{
        caption: 'View Details', 
        buttonicon: 'ui-icon-zoomin',
        title: 'View Row',
        onClickButton: function(){            
            var selRowId = grid.jqGrid('getGridParam','selrow');
            if (selRowId !== null) {
                var path = "rarocAuth/view?ref="+grid.jqGrid ('getCell', selRowId, 'rarocref');
                //$('#container-fluid').html("<div class='loading'><img alt='Loading' src='resources/css/images/loader.gif'/></div>");
                $.get(path,function(data) {
                    $('#container-fluid').html(data);
                });
            } else {
//                $message.dialog('option','modal',true);
//                $message.dialog('option','title','Info');
//                $message.html('Please select a row to view').dialog('open');
            }      
            return true;
        },
        position:"last"
    });
    grid.jqGrid("bindKeys", {
        scrollingRows: true
    });
    grid.jqGrid(options).setSelection("1");
});