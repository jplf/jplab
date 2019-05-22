//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

module.exports = {

    init: function(app, passport, tools) {

        console.log('Processing users ...');

        app.mongoose.connect('mongodb://jp:lf@localhost/svdb');
        var db = app.mongoose.connection;

        db.on('error', console.error.bind(console, 'Connection error:'));
        db.on('connected', console.log.bind(console, 'Connection open !'));

        var userSchema = new app.mongoose.Schema({
            _id:  Number,

            login: {type: String, required: true, unique: true},
            password: {type: String, required: true},
            role: {type: String},

            access: {
                created: {type: Date},
                last:    {type: Date},
                count:   {type: Number}
            },
            personnal: {
                firstName: {type: String},
                lastName:  {type: String},
                email:     {type: String}
            },
            location: {
                city:    {type: String},
                country: {type: String}
            }
        });

        var userModel = db.model('users', userSchema);

//__________________________________________________________________________

        var checkPw = function(user, string) {
            return user.password === string;
        };

        var Strategy = require('passport-local').Strategy;

        passport.use(new Strategy(
            function(login, password, done) {

                userModel.findOne({login: login},
                          function (err, user) {
                              if (err) {
                                  return done(err);
                              }
                              else if (!user) {
                                  return done(null, false, {
                                      message: 'User ' + login + ' not found !'
                                  });
                              }

                              var flag = checkPw(user, password);

                              if (!flag) {
                                  return done(null, false, {
                                      message: 'Incorrect password !'
                                  });
                              }
                              
                              return done(null, user);
                          });
            }
        ));

        passport.serializeUser(function(user, done) {
            done(null, {id:user._id, login: user.login, role: user.role});
        });

        passport.deserializeUser(function(user, done) {
            userModel.findById(user.id, function (err, user) {
                done(err, user);
            });
        });

 //__________________________________________________________________________

        app.post('/users/login', function(req, res, next) {
            passport.authenticate('local', function(err, user, info) {

                if (err) {
                    return next(err);
                }
                else if (!user) {
                    console.log('Connection failed', info);
                    res.sendStatus(401).send(info);
                }
                else {
                    req.logIn(user, function(err) {

                        if (err) {
                            console.log('Bad login');
                            return next(err);
                        }
                        console.log('User', user.login, 'connected.');

                        res.send({
                            login: user.login,
                            role:  user.role
                        });
                    })}
            })(req, res, next);
        });

        app.get('/users/list', this.checkAuth(), function(req, res) {

            userModel.find(function(err, users) {
                if (err) {
                    console.log("Can't find the list of users");
                    res.send(500, {message: err});
                }
                else {
                    console.log('Number of users:', users.length);
                    res.send(users);
                }
            })
        });

        app.get('/users/logout', function(req, res) {

            var login = req.session.passport.user.login;

            req.logout();
            console.log('User', login, 'disconnected.');
 
            req.flash('info', 'See you later ' + login + ' !');
            res.redirect('/index.html');
        });
    },

    checkAuth: function(req, res, next) {

        return function(req, res, next) {
            if (req.isAuthenticated()) {
                next();
            }
            else {
                console.log('Unauthenticated access !');
                res.send(401, {message: 'Unauthenticated access !'});
            }
        };
    },

    hasStatus: function(req, status) {

        if (req.session.passport.user === undefined) {
            return false;
        }

        var role = req.session.passport.user.role;
        return role === status;
    }
};
//__________________________________________________________________________
