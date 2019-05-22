//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var fs      = require('fs');
var winston = require('winston');

winston.add(winston.transports.File, {filename: 'app.log~'});
winston.remove(winston.transports.Console);

module.exports = function(app) {

    console.log('Processing users');
    winston.info('Starting webapp server');

    var mongoose = require('mongoose');
    var identity = process.env.MONGODB_PW;

    if (identity === undefined) {
        console.log('Please export MONGODB_PW=lefevre:');
        process.kill();
    }

    mongoose.connect('mongodb://' + identity + '@localhost/jpdb');

    var db = mongoose.connection;
    db.on('error', console.error.bind(console, 'connection error:'));

    var userList;

    db.once('open', function hi() {

        // Get the list of users from the database.
        var userSchema = new mongoose.Schema({
            _id:  Number,
            name: String,
            org:  String
        });

        var userModel = db.model('Users', userSchema);

        userModel.find({}, function(err, list) {

            if (err) {
                console.log('You got a problem:', err);
                return;
            }
 
           winston.info('List successfully loaded');

            // Save the list into the application parameters.
            app.set('userList', list);
            db.close();
        });
    });


    app.get('/users', function(req, res) {
        res.render('users', {title: 'Hi JP !'});
        req.session.data = 'something';
        console.log('up', req.session);
    });

    app.get('/users/list', function(req, res) {

        console.log(req.url);
        req.session.text= 'what a crap !';
        console.log('down', req.session);
        winston.info(req.session);

        res.render('user-list', {

            title: 'List of registed users',
            list: app.get('userList'),
            my_string: '' 
        });
    });

    // Process the action triggered by the form.
    app.post('/users/list/:op', function(req, res) {

        var op = req.params.op;
        console.log(req.body);

        if (!op) {
            console.log('No op defined !');
            return;
        }
        else if (op != 'try') {
            console.log('Unknown op:', op);
            res.send('what  ?\n' );
        }

        // Toggle case.
        var my_string = '';
        if (req.body.what.charAt(0)
            == req.body.what.charAt(0).toUpperCase()) {
            my_string = req.body.what.toLowerCase();
        }
        else {
            my_string = req.body.what.toUpperCase();
        }

        res.render('user-list', {

            title: 'List of registed users',
            list: app.get('userList'),
            my_string: my_string 
        });
    });

};

//__________________________________________________________________________
