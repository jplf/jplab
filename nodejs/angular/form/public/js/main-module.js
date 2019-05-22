//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

var mainModule = angular.module('main-module',
                                ['google-maps', 'user-module', 'site-module']);

var userModule = angular.module('user-module', []);
var siteModule = angular.module('site-module', ['user-module']);

angular.module('main-module')
    .constant('version',
              {'release': '1.0',
               'date':    '23 Jul 2013'
              })
    .config(function($routeProvider, $locationProvider) {

        console.log('Main module configuration');
    })
    .config(function ($routeProvider, $locationProvider) {

        $locationProvider.html5Mode(true);

        $routeProvider
            .when('/users', {
                templateUrl: '/users/users.html'
            })
            .when('/sites', {
                templateUrl: '/sites/sites.html'
            })
            .when('/map', {
                templateUrl: '/sites/map.html'
            })
            .when('/sites/:name', {
                controller: siteModule.HostCtrl,
                templateUrl: 'host.html'
            })
            .otherwise({
                controller: mainModule.MainCtrl,
                templateUrl: 'home.html'
            });
    })
    .controller('MapCtrl', ['$scope', function($scope) {

        $scope.header = 'World Map';

        $scope.center = {
            latitude:  50,
            longitude: 0
        };

        $scope.markers = [
            {latitude: 50, longitude: -40},
            {latitude: 50, longitude: +40}
        ];

        $scope.zoom = 0;

    }])
    .controller('MainCtrl',
                ['$scope', 'version', function($scope, version) {
                     $scope.header = 'Angular Site Management';
                     console.log('MainCtrl:', version.release);
                 }]);

//__________________________________________________________________________
