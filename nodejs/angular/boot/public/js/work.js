//__________________________________________________________________________

"use strict";

var workModule = angular.module('work', ['ngRoute', 'ngCookies']);

workModule.value('forgotten-status', {
    tag:  undefined,
    count: -1
});

workModule.run(function() {
    console.log('Work module running ...');
});

workModule.controller('UsersCtrl',
['$rootScope', '$scope', '$cookieStore',
function($rootScope, $scope, $cookieStore) {

    console.log('UsersCtrl');
    $scope.header = 'List of users';

    if ($rootScope.status === undefined) {
        var s = $cookieStore.get('data');

        if (s === undefined) {
            $rootScope.status.tag   = 'Initialized in Users';
            $rootScope.status.count = 0;
        }
        else {
            $rootScope.status = s;
        }
    }
    else {
        $rootScope.status.tag = 'Updated in Users';
        $rootScope.status.count++;
        $cookieStore.put('data', $rootScope.status);
    }

    $scope.tag   = $rootScope.status.tag;
    $scope.count = $rootScope.status.count;

    console.log('U status : ' + $scope.tag + ' count : ' + $scope.count);
}]);

workModule.controller('SitesCtrl', ['$rootScope', '$scope', '$cookieStore',
function($rootScope, $scope, $cookieStore) {
    console.log('SitesCtrl');
    $scope.header  = 'List of sites';
    $scope.navType = 'pills';

    $rootScope.status.tag   = 'Changed in Sites';

    $scope.tag   = $rootScope.status.tag;
    $scope.count = $rootScope.status.count;

    console.log('S Tag : ' + $scope.tag + ' count : ' + $scope.count);
}]);

//__________________________________________________________________________
