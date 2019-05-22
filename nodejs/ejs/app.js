//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// Usage : node app.js

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

/**
 * Module dependencies.
 */
var express = require('express');
var http    = require('http');
var path    = require('path');

var app = express();

// all environments
app.set('port', process.env.PORT || 8000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');

var favicon        = require('serve-favicon');
var bodyParser     = require('body-parser');
var methodOverride = require('method-override');
var cookieParser   = require('cookie-parser');
var session        = require('express-session');
var logger         = require('morgan');
var errorHandler   = require('errorhandler');

app.use(favicon(__dirname + '/public/triskell.ico'));
app.use(logger('dev'));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(methodOverride());


app.use(cookieParser('mot2passe'));
app.use(session({
    secret:'mot2passe',
    resave: true,
    saveUninitialized: true,
    cookie: {secure: true},
    maxAge: 3600000}));

var routes = require('./routes/index')(app);
var users  = require('./routes/users')(app);

app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
    app.use(errorHandler());
}

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});

//__________________________________________________________________________
