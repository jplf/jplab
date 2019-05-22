//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// Usage : node app.js
// See mongodb/users.js to select data.

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

/**
 * Module dependencies.
 */
var express  = require('express');
var http     = require('http');
var path     = require('path');
var passport = require('passport');
var flash    = require('connect-flash');

var app = express();

// all environments
app.set('port', process.env.PORT || 8000);
app.set('views', __dirname + '/public/views');
app.set('view engine', 'ejs');

var favicon        = require('serve-favicon');
var bodyParser     = require('body-parser');
var methodOverride = require('method-override');
var cookieParser   = require('cookie-parser');
var session        = require('express-session');

app.use(favicon(__dirname + '/public/views/triskell.ico'));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(methodOverride());

app.use(cookieParser('mot2passe'));
app.use(session({
    secret:'mot2passe',
    resave: true,
    saveUninitialized: true,
    cookie: {secure: false},
    maxAge: 3600000}));

app.use(flash());
app.use(passport.initialize());
app.use(passport.session());
app.set('view engine', 'ejs');

var users  = require('./server/users')(app, passport, bodyParser);

app.use(express.static(path.join(__dirname, 'public')));

// If using a mongodb from a container, find out the ip number.
// DB is the alias for the name of the db server.
var db_addr = process.env.DB_PORT_27017_TCP_ADDR;
console.log('DB_PORT_27017_TCP_ADDR: ' + db_addr);

http.createServer(app).listen(app.get('port'), function(){
    console.log('Server listening on port ' + app.get('port'));
});

//__________________________________________________________________________
