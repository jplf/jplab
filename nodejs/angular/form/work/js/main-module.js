//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var mainModule = angular.module('main-module', ['google-maps']);

angular.module('main-module')
    .controller('MapCtrl',
        ['$scope', function($scope) {

            $scope.header = 'World Map';

	    $scope.center = {
		    latitude:  50,
		    longitude: 0
	        };

	    $scope.position = {
	      coords: {
	        latitude: 45,
	        longitude: 0
	      }
	    };


            $scope.markers = [{latitude:0, longitude: -10}];

            $scope.zoom = 0;

        }])
    .controller('MainCtrl',
                ['$scope', 'version', function($scope, version) {
                     $scope.header = 'Angular Site Management';
                     console.log('MainCtrl:', version.release);
                 }]);

//__________________________________________________________________________
