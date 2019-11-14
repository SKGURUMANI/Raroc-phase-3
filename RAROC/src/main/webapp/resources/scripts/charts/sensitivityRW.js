/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var url1;
$(function() {
   //$(".butn").button(); 
   $("#renderChart1").click(function() {
       drawRWChart(url1);
   });
});
function drawRWChart(callType) {
    url1 = callType;
    var path = url1+"/view/chart/rw?ref="+$("#ref").val()+"&fac="+$("#fac1").val();
    $.getJSON(path,function(data) {
        var len, i;
        var rw=new Array(), raroc=new Array();
        len = data.length;
        for(i = 0;i < len; i++){
            rw[i] = data[i].outputName;
            raroc[i] = parseFloat(data[i].total);            
        }
        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'chart1',
                defaultSeriesType: 'line',
                backgroundColor:'rgba(255, 255, 255, 0.1)',                
                plotShadow: false
            },
            title: {
                text: 'Sensitivity Analysis (Risk Weight)',
                style : {
                    fontFamily: 'Lucida Sans Unicode',
                    fontWeight: 'normal',
                    fontSize: '12px'
                }
            },
            subtitle: {
                text: 'Facility '+$("#fac1").val(),
                style : {
                    fontFamily: 'Lucida Sans Unicode',
                    fontWeight: 'normal',
                    fontSize: '11px'
                }
            },
            xAxis: {
                title: {
                    text: 'Risk Weight %'
                },
                categories: rw
            },
            yAxis: {
                title: {
                    text: 'RAROC %'
                },                
                maxPadding: 0.02
            },
            exporting: {
                url: 'exportChart.exp',
                filename: 'Sensitivity_RW',
                buttons: {
                    printButton: {
                        enabled: false
                    }
                }
            },
            legend: {
                enabled: false
            },
            credits: {
                enabled: false
            },
            tooltip: {
                enabled: true,
                formatter: function() {
                    return this.series.name +': '+ Highcharts.numberFormat(this.y,2) +'%<br/>' +
                    'RW: ' + this.x +'%';
                }
            },
            series: [{
                name: 'RAROC',
                color: '#4572A7',
                data: raroc
            }]
        });
    });
}