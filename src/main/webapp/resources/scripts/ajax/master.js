/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(function () {        
   
    var customers = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: 'masters/search?term=%QUERY',
            wildcard: '%QUERY'
        }
    });

    $('#remote .typeahead').typeahead({
        hint: true,
        highlight: true,
        minLength: 3
    }, {
        name: 'customers',
        displayKey: 'value',        
        limit: 30,
        source: customers
    }).on('typeahead:selected', function(event, data){            
        $('#custId').val(data.key);
    });
    
    $('[data-toggle="tooltip"]').tooltip();
    
    $("#form").submit(function(e) {
       $('form').validationEngine('hideAll'); 
       e.preventDefault(); 
       var path = "masters/search/" + $('#custId').val();
       $("#formsDiv").html("<div class='loading'>\n\
                                <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                <div>Loading...</div>\n\
                            </div>");
       $.get(path, function(page) {
           $("#formsDiv").html(page);
       }); 
    });        
    
    $('body').on('click', '#cbsfetch', function() {
        $("#formsDiv").html("<div class='loading'>\n\
                                <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                <div>Loading...</div>\n\
                            </div>");
        var path = "masters/cbsfetch/" + $("#cust_id").val();        
        $.get(path, function(page) {
            $("#formsDiv").html(page);
        });
    });
    
});

