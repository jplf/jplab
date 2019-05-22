//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// Usage : node app.js

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

/**
 * Module dependencies.
 */
var express = require('express');
var http = require('http');
var path = require('path');

var cookieParser   = require('cookie-parser');
var bodyParser     = require('body-parser');
var methodOverride = require('method-override');
var serveStatic    = require('serve-static');
var errorHandler   = require('errorhandler');
var favicon = require('serve-favicon');

var app = express();

// all environments
app.set('port', process.env.PORT || 9800);
app.set('views', __dirname + '/views');

app.use(favicon(__dirname + '/public/triskell.ico'));
app.use(bodyParser.json());
app.use(methodOverride());
app.use(cookieParser());
app.use(serveStatic(path.join(__dirname, 'public')));

var wtf = function() {
    console.log('WTF ?');
};

app.get("/home", function(req, res) {
    console.log('Data 1');
    res.sendfile(__dirname + '/public/index.html', function(err) {
        console.log('Data is now in home ' + req.cookies);
    });
});

app.get("/users", function(req, res) {

    res.sendfile(__dirname + '/public/index.html', function(err) {
        console.log('Data is now in users ' + req.cookies);
    });
});

app.get("/sites", function(req, res) {

    res.sendfile(__dirname + '/public/index.html', function(err) {
         console.log('Data is now in sites ' + req.cookies);
   });
});

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});

//__________________________________________________________________________
