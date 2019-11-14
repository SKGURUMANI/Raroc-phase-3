/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(function () {

    $(".smultiple").multiselect({
        buttonWidth: '100%'
    });
    
    $('#datepicker').datetimepicker({
        format: 'MM/DD/YYYY'
    });

    $("#formBatch").validationEngine('attach', {promptPosition : "bottomLeft", scroll: false});

    $("#formBatch").submit(function (e) {
        e.preventDefault();
        var check = $.ajax({type: "GET",url: "operations/duplicate?batchname=" + $("#col1").val(),async: false}).responseText;
        if (check === "duplicate") {
            BootstrapDialog.show({
                title: 'Error',
                message: 'Batch Name already exists. Please enter a new name.'
            });
        }
        if ($(this).validationEngine('validate') && check === "new") {
            $.ajax({
                type: 'POST',
                url: 'operations',
                data: $(this).serialize(),
                global: false,
                async: false,
                success: function (data) {
                    //$("#page-wrapper").html(data);
                    //$("#formBatch").trigger('reset');
                    $("option:selected").removeAttr("selected");//refresh the multiSelectCombo, which otherwise shows the previous selection
                    $("#col4").multiselect('refresh');
                    $("#formBatch")[0].reset();
                    //alert("child info: "+document.getElementsByTagName('button')[1].children.length);
                    //alert("child info: "+document.getElementsByTagName('button')[1].firstChild.innerText);
                    //document.getElementsByTagName('button')[1].firstChild.innerText = "Select RFAs";
                    BootstrapDialog.show({
                        title: 'Success',
                        message: 'Batch saved successfully'
                    });
                }
            });
            return true;
        }
        return false;
    });
});

