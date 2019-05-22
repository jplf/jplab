//__________________________________________________________________________

// Svom sandbox javascript library - Jean-Paul Le Fèvre - July 2013

// Called from the plain mongo client.
// User: svompal roles: [ "readWrite", "dbAdmin" ]

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

// mongo -u lefevre -p 'pw' localhost/admin

use svdb;
db.addUser({ user: "svompal",
             pwd: "gamma_ray",
             roles: ["readWrite", "dbAdmin"]
           });

db.addUser({ user: "jp",
             pwd:  "lf",
             roles: ["readWrite"]
            });
show users

db.counters.insert({
    _id: "userid",
    seq: 0
});


var id = function getNextId() {
   var ret = db.counters.findAndModify({
            query: { _id: 'userid'},
            update: { $inc: { seq: 1 } },
            new: true
          });

   return ret.seq;
};
 
db.users.remove();

db.users.insert ({_id: id(),
                  login: 'lefevre',
                  password: 'l',
                  role:   'admin',
                  access: {
                      created: new Date('19 July 2013'),
                      last: new Date('19 July 2013'),
                      count: 0
                  },
                  personnal: {
                      firstName: 'Jean-Paul',
                      lastName: 'Le Fèvre',
                      email: 'jean-paul.lefevre@cea.fr'
                  },
                  location: {
                      city: 'Saclay',
                      country: 'France'
                  }
                 });

db.users.insert ({_id: id(),
                  login: 'jp',
                  password: 'j',
                  role:   'member',
                  access: {
                      created: new Date('19 July 2013'),
                      last: new Date('19 July 2013'),
                      count: 0
                  },
                  personnal: {
                      firstName: 'Jean-Paul',
                      lastName: 'Le Fèvre',
                      email: 'lefevre@fonteny.org'
                  },
                  location: {
                      city: 'Saclay',
                      country: 'France'
                  }
                 });

db.users.insert ({_id: id(),
                  login: 'paul',
                  password: 'p',
                  role:   'member',
                  access: {
                      created: new Date('22 July 2013'),
                      last: new Date('19 July 2013'),
                      count: 0
                  },
                  personnal: {
                      firstName: 'Jacques',
                      lastName: 'Paul',
                      email: 'j-p.lefevre@cea.fr'
                  },
                  location: {
                      city: 'Saclay',
                      country: 'France'
                  }
                 });

db.users.insert ({_id: id(),
                  login: 'martine',
                  password: 'm',
                  role:   'member',
                  access: {
                      created: new Date('22 July 2013'),
                      last: new Date('19 July 2013'),
                      count: 0
                  },
                  personnal: {
                      firstName: 'Martine',
                      lastName: 'Jouret',
                      email: 'mj@cnes.fr'
                  },
                  location: {
                      city: 'Toulouse',
                      country: 'France'
                  }
                 });

db.users.insert ({_id: id(),
                  login: 'vero',
                  password: 'v',
                  role:   'guest',
                  access: {
                      created: new Date('22 July 2013'),
                      last: new Date('19 July 2013'),
                      count: 0
                  },
                  personnal: {
                      firstName: 'Véronique',
                      lastName: 'Chiche',
                      email: 'vero@free.fr'
                  },
                  location: {
                      city: 'Zinguinchor',
                      country: 'Sénégal'
                  }
                 });

db.users.find();

//__________________________________________________________________________
