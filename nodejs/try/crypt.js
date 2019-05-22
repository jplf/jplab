//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var bcrypt = require('bcrypt');

bcrypt.hash('Antoine', 16, function(err, hash) {
    console.log(hash);
});


//__________________________________________________________________________
