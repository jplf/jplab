//__________________________________________________________________________

// Javascript library - Jean-Paul Le Fèvre - August 2016

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
                  name: 'jaypee', password: 'A',
                  role: 'admin',
                  org: 'irfu'
                 });

db.users.find();

//__________________________________________________________________________
