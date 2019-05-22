//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// Try: curl -d 'username=antoine&password=a&button=OK' http://localhost:8000
// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

module.exports = function(app, passport, bodyParser) {

    var count = 0;
    var mongoose = require('mongoose');
    var identity = process.env.MONGODB_PW;

    if (identity === undefined) {
        console.log('Please export MONGODB_PW=lefevre:pw');
        process.kill();
    }

    var database = process.env.MONGODB_DB;
    if (database === undefined) {
        database = 'localhost/jpdb';
    }

    mongoose.connect('mongodb://' + identity + '@' + database);
    var db = mongoose.connection;
    db.on('error', console.error.bind(console, 'connection error:'));
    db.on('connected',
          console.log.bind(console, 'Connection to ' + database + ' open !'));


    var userSchema = new mongoose.Schema({
        _id:  Number,
        name: String,
        password: String,
        role: String,
        org:  String,
        nbr: Number,
        place: {
            city: String,
            country: String
        }
    });

    var userModel = db.model('users', userSchema);
    var checkPw = function(user, string) {
        return user.password === string;
    };

    var Strategy = require('passport-local').Strategy;

    passport.use(new Strategy(

        function(name, password, done) {

            userModel.findOne({name: name},
            function (err, user) {
                console.log('Finding ' + name + ' done:');
                if (err) {
                    console.log('Fetching ' + name + ' failed');
                    return done(err);
                }
                else if (!user) {
                    return done(null, false, {
                        message: 'User not found !'
                    });
                }
                else if (!checkPw(user, password)) {
                    return done(null, false, {
                        message: 'Wrong password !'
                    });
                }
 
                console.log('Checking ' + name + ' successful');
                return done(null, user);
            });
        }
    ));

    passport.serializeUser(function(user, done) {
        done(null, {id:user._id, name:user.name, role: user.role});
    });

    passport.deserializeUser(function(u, done) {
        userModel.findById(u.id, function (err, user) {
            done(err, user);
        });
    });

    app.get('/', function(req, res){

        var message = req.flash('error')[0];
        if (message === undefined) {
            message = req.flash('info');
            if (message === undefined) {
                message = '';
            }
        }

        res.render('index', {
            title: 'Experimental Passport Web Application',
            message: message
        });
    });


    app.post('/login',
             passport.authenticate('local', {
                 successRedirect: '/welcome',
                 failureRedirect: '/',
                 failureFlash: true
             }));

    var hasStatus = function(session, status) {
        if (session.passport.user === undefined) {
            return false;
        }
        var role = session.passport.user.role;
        return role === status;
    };


    function ensureAuthenticated(req, res, next) {

        return function(req, res, next) {
            if (req.isAuthenticated()) {
                console.log('Successful authenticated access !');
                next();
            }
            else {
                console.log('Unauthenticated access !');
                req.flash('info', 'You must login first !');
                res.redirect('/');
            }
        };
    };

    app.get('/welcome', ensureAuthenticated(), function(req, res) {

        var username = req.session.passport.user.name
        console.log('User', username, 'connected !');
        req.flash('info', 'Login successful !');

        res.render('welcome', {
            title: 'Hi ' + username + ' !',
            message: req.flash('info'),
            isAdmin:  hasStatus(req.session, 'admin'),
            isMember: hasStatus(req.session, 'member'),
            isGuest:  hasStatus(req.session, 'guest'),
            isLogged: req.isAuthenticated()
        });

        req.session.data = {count: count++};
    });

    app.get('/logout', function(req, res) {

        var username = req.session.passport.user.name;

        req.logout();
        console.log('User', username, 'disconnected !');

        req.flash('info', 'See you later ' + username + ' !');
        res.redirect('/');
    });
};
//__________________________________________________________________________
