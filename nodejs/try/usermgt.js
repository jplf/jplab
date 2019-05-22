//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var connect = require('connect');
var logger = require('morgan');

var app = connect();

app.use(logger());

var users = require('./users');

users.warn('Module users available');

console.log(users.number());
console.log(users.number());
console.log(users.number());

users.warn('Trop fort, JP');

//__________________________________________________________________________
