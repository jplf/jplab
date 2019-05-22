//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// Called from the plain mongo client.

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

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

var db = connect("localhost/jpdb");

show collections

db.users.remove();

db.users.insert ({_id: id(),
                  name: 'jaypee', password: 'j',
                  role: 'admin',
                  org: 'irfu', nbr: 0,
                  place: {city: 'Saclay', country: 'France'}
                 });

db.users.insert ({_id: id(),
                  name: 'antoine', password: 'a',
                  role: 'guest',
                  org: 'woap', nbr: 0,
                  place: {city: 'Paris', country: 'France'}
                 });

db.users.insert ({_id: id(),
                  name: 'sylvie', password: 's',
                  role: 'guest',
                  org: 'mcc', nbr: 0,
                  place: {city: 'Paris', country: 'France'}
                 });

db.users.insert ({_id: id(),
                  name: 'jacques', password: 'j',
                  role: 'member',
                  org: 'svom', nbr: 0,
                  place: {city: 'Beijing', country: 'China'}
                 });

db.users.insert ({_id: id(),
                  name: 'paul', password: 'p',
                  role: 'member',
                  org: 'svom', nbr: 0,
                  place: {city: 'Roma', country: 'Italy'}
                 });

db.users.insert ({_id: id(),
                  name: 'martine', password: 'm',
                  role: 'member',
                  org: 'svom', nbr: 0,
                  place: {city: 'Toulouse', country: 'France'}
                 });

db.users.find();

//__________________________________________________________________________
