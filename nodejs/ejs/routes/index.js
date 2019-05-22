//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - June 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

module.exports = function(app) {

    app.get('/', function(req, res) {
        res.render('index', {title: 'First Express EJS WebApp'});
    });
};

//__________________________________________________________________________
