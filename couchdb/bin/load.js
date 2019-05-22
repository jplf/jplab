//__________________________________________________________________________
/**
 * CouchDB javascript library - December 2013

 * Usage: node load.js [-u user] [-p password] data.json
 * 
 * It loads data from a json formatted file into the couchdb database.
 * User and password can be retrieved from the environment.
 * The name of the database is hardcoded in this script.

 * To get a json dataset one may use fro instance mp3tag.pl
 * See also: https://github.com/mikeal/request

 * @author Jean-Paul Le Fèvre

 * This software is governed by the
 * CeCILL license (http://www.cecill.info/index.en.html)
 */
//__________________________________________________________________________

"use strict";

var fs = require('fs');
var request = require('request');

var password = process.env.MY_PW;
var user = process.env.USER;
var filename;

var db  ='zikmu';
var url = 'http://127.0.0.1:5984/' + db;
var j   = 2;

process.argv.forEach(function (val, i, array) {
    if (val === '-u') {
        user = array[i+1];
        j = i + 2;
    }
    else if (val === '-p') {
        password = array[i+1];
        j = i + 2;
    }

});

if (j < process.argv.length) {
    filename = process.argv[j];
}

if (user === undefined || password === undefined || filename === undefined) {
    console.log('Usage:', 'node load.js [-u user] [-p password] filename');
    process.exit(1);
}

var credential = {'user': user, 'pass': password};

console.log('Loading', filename, 'by',  user, 'to', url);

var buffer  = fs.readFileSync(filename, {encoding: 'utf8'});
var records = JSON.parse(buffer);

console.log('Number of records found:', records.length);

var nbr = 0;

var load = function(data) {

    request.post({url: url, json: data, auth: credential}, function(err, response, body) {
        if (!err) {
            console.log(response.statusCode, body);
        }
        else {
            console.log(err, 'wtf ?');
        }
    })
};

records.forEach(function(record, i, array) {
    load(record);
    nbr++;
});

console.log('Number of records loaded:', nbr, 'to', url);

//__________________________________________________________________________
