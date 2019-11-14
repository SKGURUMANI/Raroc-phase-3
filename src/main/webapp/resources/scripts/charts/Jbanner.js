$(function () {        
   
    //Function to draw the banner
    function drawBanner(renderer) {
          
        var rectBig = renderer.rect(140, 130, 130, 130, 0)
        .attr({
            'stroke-width': 2,
            stroke: '#ef7f1b',                
            zIndex: 1
        })
        .add();
		
        var rectMed = renderer.rect(100, 100, 200, 200, 0)
        .attr({
            'stroke-width': 2,
            stroke: '#ef7f1b',                
            zIndex: 1
        })
        .add();	
	
        rectBig.animate({
            x: 100,
            y: 100,
            width: 200,
            height: 200,
            'stroke-width': 2,
            duration: 3000
        });

        rectMed.animate({
            x: 110,
            y: 110,
            width: 180,
            height: 160,
           'stroke-width': 2,
            duration: 3000
        });							
    
        renderer.rect(140, 125, 130, 130, 0)
        .attr({
            'stroke-width': 2,
            stroke: '#ef7f1b',                
            zIndex: 1,
            duration: 3000
        })
        .add();

        var vertical = renderer.path(['M', 180, 30, 'L', 180, 50])
        .attr({
            'stroke-width': 2,
            stroke: '#898989',
            zIndex: 2
        })
        .add();	   

        vertical.animate({
            d: ['M', 180, 80, 'L', 180, 330],
            duration: 3000		
        });		
	
        renderer.text('RAROC Calculator', 320, 200).css({
            color: '#898989',
            fontSize: '26px',
            fontFamily: 'Lucida Sans Unicode'
        })
        .add();                
    
    } 
   
    var renderer = new Highcharts.Renderer($('#bContainer')[0], 800, 400);
    drawBanner(renderer);
    $("#bContainer").click(function() {
        drawBanner(renderer);
    });    
    
});