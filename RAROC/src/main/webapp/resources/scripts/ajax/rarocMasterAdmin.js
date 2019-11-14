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
$(function () {
    $("#form-butn").click(function () {
        if ($("#form-body").is(":visible")) {
            $("#form-butn").removeClass("tabVisible");
            $("#form-butn").addClass("tabHidden");
            $("#form-body").hide("blind", {}, 500);
            $("#form-header").addClass("ui-corner-bottom");
        } else {
            $("#form-butn").removeClass("tabHidden");
            $("#form-butn").addClass("tabVisible");
            $("#form-header").removeClass("ui-corner-bottom");
            $("#form-body").show("blind", {}, 500);
        }
    });

    $("#rarocMaster").validationEngine('attach');

    $("#rarocMaster").submit(function (e) {
        e.preventDefault();
        if ($(this).validationEngine('validate')) {
            $.ajax({
                type: 'POST',
                url: $(this).attr('action'),
                data: $(this).serialize(),
                dataType: 'text',
                global: false,
                async: false,
                success: function (data) {
                    $("#container-fluid").html("<div class='loading'>\n\
                                    <i class='fa fa-refresh fa-spin fa-5x fa-fw'></i>\n\
                                    <div>Loading...</div>\n\
                                </div>");
                    $('#container-fluid').html(data);
                }
            });
            return true;
        }
        return false;
    });

    $("input[name=cname]").autocomplete({
        source: "raroc/cname/options",
        minLength: 3
    });

    $("select[name=rtool]").change(function () {
        if (this.value === "Templated") {
            $("select[name=intRat]").empty();
            $("select[name=intRat]").prop('disabled', true);
        } else {
            $("select[name=intRat]").prop('disabled', false);
            var path = "raroc/intrating/options?model=" + this.value;
            $("select[name=intRat]").empty();
            $.getJSON(path, function (data) {
                var len = data.length;
                var html = '<option value= "" >Select...</option>';
                for (var i = 0; i < len; i++) {
                    html += '<option value="' + data[i].key + '">' + data[i].value + '</option>';
                }
                $("select[name=intRat]").append(html);
            });
        }
    });

    $("select[name=bussunit]").change(function () {
        if (this.value === "FIG") {
            $("select[name=ind]").val("FINANCIAL INTERMEDIARIES BANKS");
        }
    });

});