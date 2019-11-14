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
var url2;
$(function() {
   $("#renderChart2").click(function() {
       drawUtilChart(url2);
   });
});
function drawUtilChart(callType) {
    url2 = callType;
    var path = url2+"/view/chart/util?ref="+$("#ref").val()+"&fac="+$("#fac2").val();
    //var path = url2+"/view/chart/util?ref=Ref-1041&fac=1";
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
                renderTo: 'chart2',
                defaultSeriesType: 'line',
                backgroundColor:'rgba(255, 255, 255, 0.1)',                
                plotShadow: false
            },
            title: {
                text: 'Sensitivity Analysis (Utilization)',
                style : {
                    fontFamily: 'Lucida Sans Unicode',
                    fontWeight: 'normal',
                    fontSize: '12px'
                }
            },
            subtitle: {
                text: 'Facility '+$("#fac2").val(),
                style : {
                    fontFamily: 'Lucida Sans Unicode',
                    fontWeight: 'normal',
                    fontSize: '11px'
                }
            },
            xAxis: {
                title: {
                    text: 'Utilization %'
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
                filename: 'Sensitivity_Utilization',
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
                    'Utilization: ' + this.x +'%';
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