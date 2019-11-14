/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ajaxError(function(event, jqxhr, settings, exception) {
    if (jqxhr && jqxhr.status === 619) {
        window.location = "login/sessionExpired";
    }
});

