/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function($){
        $.cookie('dcjq-accordion', null, {path:'/'});    
        $('#accordion-blue').dcAccordion({
            eventType: 'click',
            autoClose: true,
            saveState: true,
            disableLink: true,
            speed: 'fast',
            showCount: false,
            autoExpand: true,
            classExpand	 : 'dcjq-current-parent'
        });
    });