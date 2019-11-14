/* � 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 * 
 * Asymmetrix Solutions Private Limited
 * 115, Bldg 2, Sector 3, Millennium Business Park,
 * Navi Mumbai, India, 410701
 
 if (top.location !== location) {
 top.location.href = document.location.href;
 }
 $(document).ajaxError(function (event, jqxhr, settings, exception) {
 if (jqxhr && jqxhr.status === 619) {
 window.location = "login/sessionExpired";
 }
 });
 function escapeHtml(unsafe) {
 return unsafe.replace(/&/g, "&amp;")
 .replace(/</g, "&lt;")
 .replace(/>/g, "&gt;")
 .replace(/"/g, "&quot;")
 .replace(/'/g, "&#039;");
 }
 */

if (top.location !== location) {
    top.location.href = document.location.href;
}

$.ajaxSetup({
    error: function (jqXHR, textStatus, errorThrown) {
        if (jqXHR && jqXHR.status === 619) {
            window.location = "login/sessionExpired";
        }
        if (jqXHR.status === 200) {
            BootstrapDialog.show({
                title: 'Server Message',
                message: jqXHR.responseText + "<br /> Rating Id is not valid."
            });
        } else {
            BootstrapDialog.show({
                title: 'Server Message',
                message: jqXHR.responseText + "<br /> Kindly Check Your Input."
            });
        }


    }
});
