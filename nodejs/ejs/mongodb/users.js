//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// Called from the plain mongo client.

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

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
                  name: 'jaypee', org: 'irfu', nbr: 0,
                  place: {city: 'Saclay', country: 'France'}
                 });

db.users.insert ({_id: id(),
                  name: 'antoine', org: 'woap', nbr: 0,
                  place: {city: 'Paris', country: 'France'}
                 });

db.users.insert ({_id: id(),
                  name: 'sylvie', org: 'mcc', nbr: 0,
                  place: {city: 'Paris', country: 'France'}
                 });

db.users.insert ({_id: id(),
                  name: 'jacques', org: 'svom', nbr: 0,
                  place: {city: 'Beijing', country: 'China'}
                 });

db.users.insert ({_id: id(),
                  name: 'paul', org: 'svom', nbr: 0,
                  place: {city: 'Roma', country: 'Italy'}
                 });

db.users.insert ({_id: id(),
                  name: 'martine', org: 'svom', nbr: 0,
                  place: {city: 'Toulouse', country: 'France'}
                 });

db.users.find();

//__________________________________________________________________________
