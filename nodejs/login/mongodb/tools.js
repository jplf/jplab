//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

function getNextId(name) {
   var ret = db.counters.findAndModify({
            query: { _id: name },
            update: { $inc: { seq: 1 } },
            new: true
          });

   return ret.seq;
};

module.exports.getNextId = getNextId;

//__________________________________________________________________________
