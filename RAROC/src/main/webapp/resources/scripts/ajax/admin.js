/*
 * Document    : admin
 * Purpose     : Ajax calls and jQuery widget configs for the admin module
 * Created on  : 19 Apr, 2013, 12:28:26 AM
 * Author      : Amol
 */
$(function () {
    
    $("#roles").multiselect({
        buttonWidth: '100%'
    });
    
    $("select[name='department']").multiselect({
        buttonWidth: '100%',
        maxHeight: 200,
        enableCaseInsensitiveFiltering: true
    });
    
    $("#deptType").change(function () {
        var path = "admin/departments?type=" + $(this).val();
        $.getJSON(path, function (data) {
            var html, len = data.length;
            for (var i = 0; i < len; i++) {
                html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
            }
            $("#department").empty();
            $("#department").append(html);
            $('#department').multiselect('rebuild');
        });
    });

    // Bind the validation engine to the form
    $("#formAdmin").validationEngine('attach', {
        promptPosition: "topLeft", scroll: false
    });

    $("#formAdmin").submit(function (e) {
        e.preventDefault();        
        var action = $(this).attr('action');
        if ($(this).validationEngine('validate')) {
            $('button[type="submit"]').button('loading');
            $.ajax({
                type: 'POST',
                url: action,
                data: $(this).serialize(),
                dataType: 'json',
                global: false,
                async: false,
                success: function (data) {
                    $('button[type="submit"]').button('reset');
                    if (data.mesgType === "duplicate") {
                        $("input[name=userId]").validationEngine(
                                'showPrompt', data.mesgValue, 'error', 'centerRight:0,-6', true);
                    } else {
                        BootstrapDialog.show({
                            title: 'Server Message',
                            message: data.mesgValue,
                            onhide: function () {
                                $("#page-wrapper").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
                                $.get(action, function (page) {
                                    $('#page-wrapper').html(page);
                                });
                            }
                        });
                    }
                },
                error: function (data) {
                    $('button[type="submit"]').button('reset');
                    BootstrapDialog.show({
                        title: 'Server Message',
                        message: data.mesgValue
                    });
                }
            });
            return true;
        }
        return false;
    });
    
    $(".childMenu").click(function(e){
    	 e.preventDefault(); 
    });
    
});