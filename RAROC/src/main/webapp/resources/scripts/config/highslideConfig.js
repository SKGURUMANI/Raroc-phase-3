/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
hs.graphicsDir = 'resources/css/graphics/';
hs.outlineType = 'rounded-white';
hs.showCredits = false;
hs.align = 'center';
hs.wrapperClassName = 'draggable-header';
hs.headingEval = 'this.a.title';
hs.preserveContent = false;
hs.registerOverlay({
		html: '<div class="closebutton" onclick="return hs.close(this)" title="Close"></div>',
		position: 'top right',
		fade: 2, // fading the semi-transparent overlay looks bad in IE
		useOnHtml: true
	       });
var filterConfig = {
    objectType:'ajax',
    height: 400,
    width: 800,
    align: 'center',
    src: 'reports/filter'
}               