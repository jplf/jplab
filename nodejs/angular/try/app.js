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

var app = express();
var favicon = require('serve-favicon');
var morgan  = require('morgan');

var bodyParser     = require('body-parser');
var methodOverride = require('method-override');
var serveStatic    = require('serve-static');
var errorHandler   = require('errorhandler');

// all environments
app.set('port', process.env.PORT || 8000);
app.set('views', __dirname + '/views');

app.use(favicon(__dirname + '/public/triskell.ico'));
app.use(morgan('short'));
app.use(bodyParser.json());
app.use(methodOverride());

app.use(serveStatic(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
    app.use(errorHandler());
}

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});

//__________________________________________________________________________
