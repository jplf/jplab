//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

module.exports = function(app, tools) {

    console.log('Processing sites ...');

    var db = app.mongoose.connection;

    var siteStatus = ['registered', 'accepted', 'rejected', 'pending',
                      'operational'];

    var siteSchema = new app.mongoose.Schema({
        _id:  Number,
        name: {type: String, required: true, unique: true},

        geolocation: {
            longitude: {type: Number, required: true,
                        min: -180, max: +180},
            latitude:  {type: Number, required: true,
                        min: -90, max: +90},
            altitude:  {type: Number, required: true,
                       min: 0, max: 4000},
            remark:    {type: String},
            picture:   {type: String},
            city:      {type: String},
            country:   {type: String}
        },

        telecom: {

            connectivity: {type: Boolean, default: false},
            isp: {type: String},
            information: {type: String}
        },

        admin: {
            complete: {type: Boolean, default: false},
            status:  {type: String, enum: siteStatus}
        }
    });

//__________________________________________________________________________

    var siteModel = db.model('Sites', siteSchema);

    var usr = require('./users');
    app.get('/sites/list', usr.checkAuth(), function(req, res) {

        siteModel.find(function(err, sites) {
            if (err) {
                console.log("Can't find the list of sites");
                res.send(500, {message: err});
            }
            else {
                res.send(sites);
            }
        })
    });

    // Return a site content knowning its name
    app.get('/sites/select/:name', usr.checkAuth(), function(req, res) {

        var name  = req.params.name;
        console.log('GET request:', req.method, req.params);

        siteModel.findOne({name: name}, function(err, site) {
            if (err) {
                console.log('Site', name, 'not found');
                res.send(500, {message: err});
            }
            else {
                if (! usr.hasStatus(req, 'admin')) {
                    site.admin.status = 'hidden';
                }
                res.send(site);
            }
        });
    });

    app.post('/sites/update', usr.checkAuth(), function(req, res) {

        var name  = req.body.name;
        console.log('POST request:', req.method, req.params);

        siteModel.findOne({name: name}, function(err, site) {
            if (err) {
                console.log("Can't find " + name, err);
                res.send(500, {message: err});
                return;
            }

            site.geolocation = req.body.geolocation;

            site.save(function(err) {
                if (err) {
                    console.log("Can't save " + name, err);
                    res.send(500, {message: err.errors});
                }
                else {
                    console.log('Site', site.name, 'saved');
                    res.send('ok');
                }
            });
        });
    });
};
//__________________________________________________________________________
