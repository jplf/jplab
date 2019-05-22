//__________________________________________________________________________

"use strict";

var workModule = angular.module('work', ['ngRoute', 'ngCookies']);
var bootModule = angular.module('boots',
                 ['ngRoute', 'ngCookies', 'ui.bootstrap', 'work']);

bootModule.config(function ($routeProvider, $locationProvider) {

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });

    $routeProvider.
        when('/users', {
            templateUrl: 'users.html'
        }).
        when('/sites', {
            templateUrl: 'sites.html'
        }).
        when('/home', {
            templateUrl: 'home.html'
        });

    console.log('Routes configured');
});

bootModule.run(function() {
    console.log('Boot module running ...');
});

bootModule.controller('HomeCtrl',
['$rootScope', '$scope', '$cookieStore',
function($rootScope, $scope, $cookieStore) {

    console.log('HomeCtrl');
    $scope.header = 'Home page';

    $scope.tag   = $rootScope.status.tag;
    $scope.count = $rootScope.status.count;

    console.log('H param : ' + $scope.tag + ' count : ' + $scope.count);

    $scope.clear = function() {
        console.log('Data : ' + 'will be erased');
        $cookieStore.remove('data');
    };
}]);

bootModule.controller('MainCtrl',
['$rootScope', '$scope', '$cookieStore',
function($rootScope, $scope, $cookieStore) {

    console.log('MainCtrl');
    $scope.header = 'Angular experiments';

    if ($rootScope.status === undefined) {
        var s = $cookieStore.get('data');

        if (s != undefined) {
            $rootScope.status = s;
        }
        else {
            s = {
                tag: 'Created in MainCtrl',
                count: 0
            };

           $rootScope.status = s; 
        }
    }

    $scope.tag   = $rootScope.status.tag;
    $scope.count = $rootScope.status.count;
    $scope.wtf   = 'What the fuck ?';

    console.log('B param : ' + $scope.tag + ' count : ' + $scope.count);
}]);
//__________________________________________________________________________
