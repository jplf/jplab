//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

angular.module('user-module')

    .config(function() {
        console.log('User module configuration');
    })
    .constant('version',
              {'release': '1.0',
               'date':    '14 Jul 2013'
              })
    .value('Status', {
        isLogged: false,
        name:    'unknown',
        rank:    'guest'
    })
    .factory('Probe', ['Status', function(status) {

        return function(status) {
            return status;
        }
    }])
    .controller('LoginCtrl',
        ['$scope', '$http', '$timeout', 'Status',
         function($scope, $http, $timeout, status) {
 
            $scope.submit = function() {

                console.log('Logging in ...');

                $http.post('/users/login', $scope.account)
                    .success(function(data) {
                        status.isLogged = true;
                        status.name = data.login;
                        status.rank = data.role;

                        $scope.style = 'msg alert alert-success'
                        $scope.message = 'Login successful for ' + status.name;
                        $timeout(function() {
                            $scope.message = undefined;
                            console.log('Message removed !');
                        }, 5000);
                    })
                    .error(function(data) {
                        console.log('Error cause:', data);
                        $scope.style = 'msg alert alert-error';
                        $scope.message = data.message;
                        status.isLogged = false;
                   });
            };

            $scope.logout = function() {
                console.log('Logging out ...');

                $http.get('/users/logout')
                    .success(function(data) {
                        console.log('Logout OK');
                        $scope.message = 'Logout successful !';
                    })
                    .error(function(data) {
                        console.log('Error cause:', data);
                        $scope.message = 'Logout failed !';
                    });
            };
    }])
    .controller('UsersCtrl',
        ['$scope', '$http', function($scope, $http) {
            $scope.header = 'List of users';

            $http.get('/users/list')
                .success(function(data) {
                    $scope.userList = data;
                })
                .error(function(data) {
                    console.log('Error cause:', data);
                    $scope.message = "Can't get the list of users !";
                })
        }]);
//__________________________________________________________________________
