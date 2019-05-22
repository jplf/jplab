//__________________________________________________________________________

// Svom VHF hosting javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var validate = require('mongoose-validator');

var nameCheck = [
    validate({
        validator: 'isLength',
        arguments: [3, 80],
        message: 'Invalid string length'
    })
];


module.exports = nameCheck;

//__________________________________________________________________________
