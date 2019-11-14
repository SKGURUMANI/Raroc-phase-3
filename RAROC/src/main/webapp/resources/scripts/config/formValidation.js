/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
    // binds form submission and fields to the validation engine    
    $("#formID").validationEngine();
    window.$required = $('<div></div>').dialog({
        autoOpen: false,
        resizable: false,
        height:120,
        title: 'Server Message',
        modal: true,
        hide: "explode",
        buttons: {
            "Ok": function() {
                $(this).dialog("close");
            }
        }
    });
    $("#formID").submit(function(e) {
        e.preventDefault();
        if ($("#formID").validationEngine('validate')) {
            $required.html("Data Saved Successfully").dialog('open');        
        }
    });
});
