//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013
// Angular experimentations.

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

var passport       = require('passport');
var flash          = require('connect-flash');
var favicon        = require('serve-favicon');
var cookieParser   = require('cookie-parser');
var bodyParser     = require('body-parser');
var methodOverride = require('method-override');
var errorHandler   = require('errorhandler');
var serveStatic    = require('serve-static');
var session        = require('express-session');

var app = express();
app.mongoose = require('mongoose');

var tools = require('./routes/tools');

// all environments
app.set('port', process.env.NODE_PORT || 8000);
app.set('views', __dirname + '/public/ejs');
app.set('view engine', 'ejs');

app.use(favicon(__dirname + '/public/triskell.ico'));
app.use(bodyParser.json());
app.use(methodOverride());

app.use(cookieParser('mot2passe'));
app.use(session({
    secret:'mot2passe',
    resave: false,
    saveUninitialized: false,
    cookie: {secure: false},
    maxAge: 3600000}));

app.use(flash());
app.use(passport.initialize());
app.use(passport.session());

var users  = require('./routes/users').init(app, passport, tools);
var sites  = require('./routes/sites')(app, tools);

app.use(serveStatic(path.join(__dirname, 'public')));

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});

//__________________________________________________________________________
