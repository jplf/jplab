/**
 * Javascript code used to display cac40 data.
 * Jean-Paul Le Fèvre - jean-paul.lefevre at cea . fr - February 2012.
 * Licence : http://www.cecill.info/licences/Licence_CeCILL_V2-en.html
 */
"use strict";

/**
 * The Cac40 global object.
 * Used to share variables between scripts.
 * See : http://www.javascripttoolbox.com/bestpractices/
 */

var cac = {};

var xhrFbk = new XMLHttpRequest();
var xhrTwt = new XMLHttpRequest();

var minDate = new Date(2012, 1, 14);
var maxDate = new Date();

/**
 * Prepare data acquisition.
 */
function initialize(companyName) {
    
    console.log('Initializing for', companyName, ' !');

    // Ask the server the input file for a given company and a source.
    // Facebook
    xhrFbk.open("GET",
                "/select/?company=" + companyName + "&amp;src=fb", true);
    xhrFbk.send(null);

    // And Twitter
    xhrTwt.open("GET",
                "/select/?company=" + companyName + "&amp;src=tw", true);
    xhrTwt.send(null);
};

/**
 * Convert a sqlite date to a iso date.
 * '2012-02-10 10:20:00' -> '2012-02-10T10:20:00'
 */
function parseDate(string) {

    var isoDate = string.replace(/ /, 'T')
    return new Date(isoDate);
};
/**
 * Return the array of values from the array of couples [time, value].
 */
function getValues(s) {

    var values = [];

    for (var i = 0; i < s.length; i++) {
        values.push(s[i][1]);
    }

    return values;
};
/**
 * Return minimum value.
 * The value is rounded using k.
 */
function getMinValue(s, k) {

    var min = Math.min.apply(null, getValues(s));

    return k * Math.floor(min/k);
};
/**
 * Return maximum value.
 * The value is rounded using k.
 */
function getMaxValue(s, k) {

    var max = Math.max.apply(null, getValues(s));

    return k * Math.ceil(max/k);
};

/**
 * Build the series to display.
 * Input : the buffer provided by the http request
 * Output : the array of scores s1, s2, s3 and the array of variations.
 */
function parseRows(buffer, s1, ds1, s2, ds2, s3, ds3) {

    if (buffer.length < 1) {
        console.log("No buffer read from the server !");
        return;
    }
   // console.log("Buffer :\n", buffer)

    // Split in lines.
    var re = /\n/;
    var chunks = buffer.split(re);

    // Origin : 10 February 2012
    var d0 = parseDate('2012-02-10 00:00:00');
    var t0 = d0.getTime();

    // Split each line
    re = /, /;
    var vp = 0;

    for (var i = 0; i < chunks.length; i++) {

        var data = chunks[i].split(re);

        if (data.length < 2) {
            // A possible white space at the end.
            continue;
        }
        else if (data.length < 6) {
            console.log('Invalid row :', i, chunks[i], '.', data.length);
            continue;
        }
        var d1 = parseDate(data[5]);

        // Get the number of fb like.
        // Sometimes the number was wrong. In this case the previous
        // value is used.
        var v1 = parseInt(data[2]);
        if (v1 < vp) {
            v1 = vp;
        }
        else {
            vp = v1;
        }

        //console.log(d1, v1);

        s1.push([d1, v1]);

        // Get the number of fb talk.
        var v2 = parseInt(data[3]);
        s2.push([d1, v2]);

   }

    // Unit : 1 day
    var unit = 3600000 * 24;

    // Now compute the variations
    for (var i = 1; i < s1.length; i++) {

        // The like values
        var v2 = s1[i];
        var v1 = s1[i - 1];

        // Then the time, first in milliseconds then in units.
        var t2 = v2[0].getTime();
        var t1 = v1[0].getTime();
        var dt = (t2 - t1) / unit;

        var dv = (v2[1] - v1[1]) / dt;

        ds1.push([v2[0], dv]);

        // The talk values
        v2 = s2[i];
        v1 = s2[i - 1];
        dv = (v2[1] - v1[1]) / dt;

        ds2.push([v2[0], dv]);
    }
};

/**
 * Facebook plots
 *
 */
xhrFbk.onreadystatechange = function(event) {

    if (xhrFbk.readyState  == 4) {
        if (xhrFbk.status  == 200) {

            console.log("Ready on xhrFbk");

            var buffer = xhrFbk.responseText;

            if (buffer.length < 1) {
                console.log("Empty response by the server !");
                return;
            }

            // The series of facebook scores.
            var fbkLike  = [];
            var fbkTalk  = [];

            // The series of variations.
            var dFbkLike  = [];
            var dFbkTalk  = [];

            parseRows(buffer, fbkLike, dFbkLike, fbkTalk, dFbkTalk);

            var k = 500; // The rounding factor.
            var minFbkLike = getMinValue(fbkLike, k);
            var maxFbkLike = getMaxValue(fbkLike, k);
            var minFbkTalk = getMinValue(fbkTalk, k);
            var maxFbkTalk = getMaxValue(fbkTalk, k);

            k = 100;
            var minDlike = getMinValue(dFbkLike, k);
            var maxDlike = getMaxValue(dFbkLike, k);
            var minDtalk = getMinValue(dFbkTalk, k);
            var maxDtalk = getMaxValue(dFbkTalk, k);

            cac.fbkLikePlot = $.jqplot('fb_like',  [fbkLike, dFbkLike],
                     { title: 'Facebook Like counts',
                       axes:   {
                                xaxis: {
                                    renderer: $.jqplot.DateAxisRenderer,
                                    tickOptions:
                                    {formatString:'%#d %b %y'},
                                    min: minDate, 
                                    max: maxDate, 
                                    tickInterval: '1 month'
                                },
                                yaxis:  {min: minFbkLike,  max: maxFbkLike},
                                y2axis: {min: minDlike, max: maxDlike}
                       },
                       series: [
                           {
                               color: '#4bb2c5',
                               lineWidth: 1,
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               xaxis: 'xaxis', yaxis: 'yaxis'
                           },
                           {
                               color: '#bfbc87',
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               lineWidth: 1,
                               xaxis: 'xaxis', yaxis: 'y2axis'
                           }
                       ],
                       grid: {
                           background: '#f0d896',
                           shadow: false
                       }
                     });

            cac.fbkTalkPlot = $.jqplot('fb_talk',  [fbkTalk, dFbkTalk],
                     { title: 'Facebook Talk counts',
                       axes:   {
                                xaxis: {
                                    renderer: $.jqplot.DateAxisRenderer,
                                    tickOptions:
                                    {formatString:'%#d %b %y'},
                                    min: minDate, 
                                    max: maxDate, 
                                    tickInterval: '1 month'
                                },
                                yaxis:  {min: minFbkTalk,  max: maxFbkTalk},
                                y2axis: {min: minDtalk, max: maxDtalk}
                       },

                       series: [
                           {
                               color: '#4bb2c5',
                               lineWidth: 1,
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               xaxis: 'xaxis', yaxis: 'yaxis'
                           },
                           {
                               color: '#bfbc87',
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               lineWidth: 1,
                               xaxis: 'xaxis', yaxis: 'y2axis'
                           }
                       ],
                       grid: {
                           background: '#f0d896',
                           shadow: false
                       }
                     });

        } else {
            console.log("Error", xhrFbk.statusText);
        }
    }
};

/**
 * Twitters plots
 */

xhrTwt.onreadystatechange = function(event) {

    if (xhrTwt.readyState  == 4) {
        if (xhrTwt.status  == 200) {

            console.log("Ready on xhrTwt");
            var buffer = xhrTwt.responseText;

            if (buffer.length < 1) {
                console.log("Empty response by the server !");
                return;
            }

            // The series of facebook scores.
            var twtTweets  = [];
            var twtFlowers = [];

            // The series of variations.
            var dTwtTweets  = [];
            var dTwtFlowers = [];

            parseRows(buffer, twtTweets, dTwtTweets, twtFlowers, dTwtFlowers);

            var k = 500; // The rounding factor.
            var minTwtTweets  = getMinValue(twtTweets, k);
            var maxTwtTweets  = getMaxValue(twtTweets, k);
            var minTwtFlowers = getMinValue(twtFlowers, k);
            var maxTwtFlowers = getMaxValue(twtFlowers, k);

            k = 100;
            var minDtweets  = getMinValue(dTwtTweets, k);
            var maxDtweets  = getMaxValue(dTwtTweets, k);
            var minDflowers = getMinValue(dTwtFlowers, k);
            var maxDflowers = getMaxValue(dTwtFlowers, k);

           cac.twtTweetPlot =  $.jqplot('tw_tweets',  [twtTweets, dTwtTweets],
                     { title: 'Twitter Tweets counts',
                       axes:   {
                                xaxis: {
                                    renderer: $.jqplot.DateAxisRenderer,
                                    tickOptions:
                                    {formatString:'%#d %b %y'},
                                    min: minDate, 
                                    max: maxDate, 
                                    tickInterval: '1 month'
                                },
                                yaxis:  {min: minTwtTweets,  max: maxTwtTweets},
                                y2axis: {min: minDtweets, max: maxDtweets}
                       },
                       series: [
                           {
                               color: '#4bb2c5',
                               lineWidth: 1,
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               xaxis: 'xaxis', yaxis: 'yaxis'
                           },
                           {
                               color: '#bfbc87',
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               lineWidth: 1,
                               xaxis: 'xaxis', yaxis: 'y2axis'
                           }
                       ],
                       grid: {
                           background: '#f0d896',
                           shadow: false
                       }
                     });

            cac.twtFlowerPlot = $.jqplot('tw_followers', [twtFlowers, dTwtFlowers],
                     { title: 'Twitter Followers counts',
                       axes:   {
                                xaxis: {
                                    renderer: $.jqplot.DateAxisRenderer,
                                    tickOptions:
                                    {formatString:'%#d %b %y'},
                                    min: minDate, 
                                    max: maxDate, 
                                    tickInterval: '1 month'
                                },
                                yaxis: {min: minTwtFlowers,  max: maxTwtFlowers},
                                y2axis: {min: minDflowers, max: maxDflowers}
                       },

                       series: [
                           {
                               color: '#4bb2c5',
                               lineWidth: 1,
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               xaxis: 'xaxis', yaxis: 'yaxis'
                           },
                           {
                               color: '#bfbc87',
                               shadow: false,
                               markerOptions: {size: 1, shadow: false},
                               lineWidth: 1,
                               xaxis: 'xaxis', yaxis: 'y2axis'
                           }
                       ],
                       grid: {
                           background: '#f0d896',
                           shadow: false
                       }
                     });

        } else {
            console.log("Error", xhrTwt.statusText);
        }
    }
};

//_______________________________________________________________________________
