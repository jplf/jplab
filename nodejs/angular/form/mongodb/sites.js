//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// Called from the plain mongo client.
// mongo -u jp -p lf localhost/svdb sites.js

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

//db.counters.insert({_id: "siteid", seq: 0 });

var id = function getNextId() {
   var ret = db.counters.findAndModify({
            query: { _id: 'siteid'},
            update: { $inc: { seq: 1 } },
            new: true
          });

   return ret.seq;
};
 
db.sites.remove();

db.sites.insert ({_id: id(),

                  name: 'Saclay',

                  geolocation: {
                      longitude: 1.235,
                      latitude: 40.45,
                      altitude: 124,
                      remark:  'ras',
                      picture:
                      'http://www-centre-saclay.cea.fr/en/CEA-Saclay2',
                      city:    'Massy',
                      country: 'France'
                  },

                  telecom: {
                      connectivity: true,
                      isp: 'renater',
                      information: 'Good connectivity'
                  },

                  admin: {
                      complete: true,
                      status: 'pending'
                  }
                 });

db.sites.insert ({_id: id(),

                  name: 'Toulouse',

                  geolocation: {
                      longitude: 2.235,
                      latitude: 46.25,
                      altitude: 24,
                      remark:  'first site',
                      picture: '',
                      city:    'Toulouse',
                      country: 'France'
                  },

                  telecom: {
                      connectivity: true,
                      isp: undefined,
                      information: 'Good connectivity'
                  },

                  admin: {
                      complete: false,
                      status: 'operational'
                  }
                 });

db.sites.insert ({_id: id(),

                  name: 'Zig',

                  geolocation: {
                      longitude: 2.235,
                      latitude: -26.25,
                      altitude: 12,
                      remark:  '',
                      picture: '',
                      city:    'Ziguinchor',
                      country: 'Senegal'
                  },

                  telecom: {
                      connectivity: true,
                      isp: '',
                      information: ''
                  },


                  admin: {
                      complete: false,
                      status: 'registered'
                  }
                 });

db.sites.insert ({_id: id(),

                  name: 'Sada',

                  geolocation: {
                      longitude: -45.13,
                      latitude: -12.8,
                      altitude: 0,
                      remark:  '',
                      picture: '',
                      city:    'Mamoudzou',
                      country: 'Comorre'
                  },

                  telecom: {
                      connectivity: false,
                      isp: '',
                      information: undefined
                  },

                  admin: {
                      complete: false,
                      status: 'registered'
                  }
                 });

db.sites.find();

//__________________________________________________________________________
