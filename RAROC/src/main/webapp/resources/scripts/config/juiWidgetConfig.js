/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(function () {
    //Icon Arrow Button
    $(".filterbutn").button({
        icons: {
            primary: "ui-icon-circle-arrow-s"
        }
    });

    //Text Button
    $(".butn").button();

    //Tool Tips
    $(".tips").tooltip({
        content: function (callback) {
            callback($(this).prop('title'));
        }/*,
         open: function (e) {
         setTimeout(function () {
         $(e.target).tooltip('close');
         }, 3000);
         }*/
    });

    //Tabs
    $(".juiTabs").tabs();

    //Accordion
    $(".juiAccordion").accordion({
        collapsible: true,
        autoHeight: false,
        active: false
    });

    //Datepicker
    $(".datepicker").datepicker({
        dateFormat: 'dd-M-yy',
        changeMonth: true,
        changeYear: true
    });

    //Date Range
    $("#from").datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        numberOfMonths: 3,
        onSelect: function (selectedDate) {
            $("#to").datepicker("option", "minDate", selectedDate);
        }
    });
    $("#to").datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        numberOfMonths: 3,
        onSelect: function (selectedDate) {
            $("#from").datepicker("option", "maxDate", selectedDate);
        }
    });


    //Save Button
    $(".savemedia").button({
        icons: {
            primary: "ui-icon-disk"
        }
    });

    //Info Button  
    $(".info").button({
        icons: {
            primary: "ui-icon-info"
        }
    });

    window.$message = $('<div></div>').dialog({
        autoOpen: false,
        resizable: false,
        height: 120,
        hide: "explode",
        buttons: {
            "Ok": function () {
                $(this).dialog("close");
            }
        }
    });

});