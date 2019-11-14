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

    window.$message = $("<div></div>").dialog({
        autoOpen: false,
        modal: true,
        hide: "explode",
        buttons: {
            "Ok": function() {
                $(this).dialog("close");
            }
        }
    });    

    /*   
     *  RAROC Existing grid
     */    
    var grid = $("#grid"),
    initialWidth = $("#jqgrid").width() - 2,
    afterWidth = initialWidth,
    refcode = $("#oldref").val(),
    URL = 'raroc/existing/grid?recref='+refcode, lastSel1 = -1,
    params0 = {
        name: 'id',
        index: 'n_facility_no',
        label: 'Facility No'
    },
    params1 = {
        name: 'rarocref',
        index: 'v_rec_ref_no',
        label: 'Reference Id',
        hidden: true
    },
    params2 = {
        name: 'facType',
        index: 'v_fac_type',
        label: 'Facility Type',
        align: 'left'
    },
    params3 = {
        name: 'facDesc',
        index: 'v_fac_desc',
        label: 'Facility Desc',
        align: 'left'
    },
    params4 = {
        name: 'cur',
        index: 'v_curr',
        label: 'Currency',
        align: 'left'
    },
    params5 = {
        name: 'amount',
        index: 'n_amount',
        label: 'Amount',
        align: 'right'
    },
    params6 = {
        name: 'tenure',
        index: 'n_tenure',
        label: 'Tenure',
        align: 'right'
    },
    options = {
        url: URL,
        colModel: [params0, params1, params2, params3, params4, params5, params6],
        gridview: true, // Not to be used with treeGrid, subGrid and afterInsertRow
        caption: 'List of existing facilities',
        pager: '#pager',
        autowidth: true,
        altRows: true,
        rowNum: 6,
        rowList: [6, 30, 45],
        emptyrecords: 'Nothing to view',
        height: 'auto',
        shrinkToFit: true,
        multiselect: true,
        loadComplete: function() {
            afterWidth = $("#jqgrid").width() - 2;
            grid.jqGrid("setGridWidth", afterWidth);
        },
        onHeaderClick: function(gridstate) {
            if (gridstate === "visible") {
                grid.jqGrid("setGridWidth", afterWidth);
            } else {
                grid.jqGrid("setGridWidth", initialWidth);
            }
        }
    };
    grid.jqGrid(options).navGrid("#pager", {
        edit: false,
        add: false,
        del: false,
        search: false,
        refresh: true
    }, {}, {}, {}, {
        sopt: ['cn','nc','bw','ew','eq']
    })
    .navButtonAdd('#pager', {
        caption: "Next",
        buttonicon: "ui-icon-check",
        onClickButton: function() {
            var facCount = $("#facility").val();            
            var recid = grid.jqGrid ('getGridParam', 'selarrrow');
            if(recid.length > facCount) {
                $message.dialog("option", "title", "Error");
                $message.dialog("option", "height", 150);
                $message.html("You cannot select more than "+facCount+" facility").dialog('open');                
            } else {
                var path = $("#recInfo").attr('action') + "?facs=" + recid + "&oldref=" + refcode;
                $.ajax({
                type: 'POST',
                url: path,
                data: $("#recInfo").serialize(),
                dataType: 'text',
                global: false,
                async: false,
                success: function(data) {
                        $('#inner').html("<div class='loading'><img src='resources/css/images/loader.gif' /></div>");
                        $('#inner').html(data);
                    }
                });          
            }
        },
        position: "last"
    });
    
    grid.jqGrid(options).bindKeys();
    grid.jqGrid(options).setSelection("1");
    grid.focus();

});