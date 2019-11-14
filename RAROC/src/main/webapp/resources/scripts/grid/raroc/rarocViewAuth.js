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


    $("input[type=submit]").click(function (e) {
        e.preventDefault();
        var path = $("#rarocAuthorize").attr('action');
        if ($(this).val() === "Reject") {
            path = path + "&action=R";
        } else {
            path = path + "&action=A";
        }
        var data = $("#rarocAuthorize").serialize();
        $.ajax({
            type: 'POST',
            url: path,
            data: data,
            global: false,
            async: false,
            success: function () {
                location.reload();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                BootstrapDialog.show({
                    title: 'Server Message',
                    message: "HTML Charcters are not allowed"
                });
            }
        });
    });

});