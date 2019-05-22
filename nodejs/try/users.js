//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

    var nbr = 0;

    function number() {
        return nbr++;
    }

    function warn(msg) {
        console.log('Users:', msg, '!');
    }

    function process(req, res) {
        res.end(JSON.stringify(req.query));
    }

module.exports.number = number;
module.exports.warn   = warn;
module.exports.process = process;

//__________________________________________________________________________
