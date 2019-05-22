//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var connect = require('connect');
var users   = require('./users');
var fs      = require('fs');

var morgan      = require('morgan');
var serveStatic = require('serve-static');
var qs = require('qs');

var winston = require('winston');
winston.add(winston.transports.File, {filename: 'liste~'});
winston.remove(winston.transports.Console);

winston.log('info', 'Logging messages');
winston.info('Starting server');

console.log('Directory:', __dirname);

var app = connect();

app.use(morgan('short'));
app.use(serveStatic(__dirname + '/html'));
app.use(qs);
app.use(users.process);
app.listen(8000);

users.warn('The server is running');

//__________________________________________________________________________
