//__________________________________________________________________________

// Svom javascript library - Jean-Paul Le Fèvre - July 2013

// This software is governed by the
// CeCILL license (http://www.cecill.info/index.en.html)
//__________________________________________________________________________

"use strict";

angular.module('site-module')

    .config(function($routeProvider, $locationProvider) {

        console.log('Site module configuration');
    })
    .service('Params', function() {

        var params = {};

        var data = {
            currentSite: 'Toulouse'
        };

        params.get = function() {
            return data.currentSite;
        };

        params.set = function (site) {
            data.currentSite = site;
        };

        params.sample = function() {

            return {
                name:      'Sample',
                geolocation: {
                    longitude:  2.15160,
                    latitude:  48.72336,
                    altitude:  258,
                    remark:    'ras',
                    picture:   'http://www.some-domain.fr',
                    city:      'Somewhere',
                    country:   'France'
                },
                admin: {
                    status:    'experimental'
                }
            };
        };

        params.select = function(current, $http, callback) {

            var site = {};

            $http.get('/sites/select/' + current).
                success(callback).
                error(function(data) {
                    console.log('Error cause:', data);
                });

            return site;
        };

        return params;
    })
    .controller('SitesCtrl',
        ['$scope', '$http', function($scope, $http) {
            $scope.header = 'List of sites';

            $http.get('/sites/list')
                .success(function(data) {
                    $scope.siteList = data;
                })
                .error(function(data) {
                    console.log('Error cause:', data);
                    $scope.message = "Can't get the list of sites !";
                })
        }])
    .controller('OrgCtrl',
        ['$scope', '$http', function($scope, $http) {
            $scope.header = 'Organization';
        }])
    .controller('HostCtrl',
        ['$scope', '$routeParams', '$http', 'Params', 'Status',
         function($scope, $routeParams, $http, Params, status) {

            $scope.countries = countriesList;
            $scope.header = 'Site parameters';
            $scope.isAdmin = (status.rank === 'admin');
             console.log('Status:', status.name);

            var current = $routeParams.name;

            $scope.data = Params.select(current, $http, function(data) {

                $scope.data = data;

                if ($scope.data.geolocation.longitude > 0.) {
                    $scope.longitudeWE = 'West';
                }
                else {
                    $scope.data.geolocation.longitude =
                        Math.abs(data.geolocation.longitude);
                    $scope.longitudeWE = 'East';
                }

                if ($scope.data.geolocation.latitude > 0.) {
                    $scope.latitudeNS = 'North';
                }
                else {
                    $scope.data.geolocation.latitude =
                        Math.abs(data.geolocation.latitude);
                    $scope.latitudeNS = 'South';
                }

            });

        }])
    .controller('NetworkCtrl',
        ['$scope', '$routeParams', '$http',
         function($scope, $routeParams, $http) {

            $scope.save = function() {

                $http.post('/sites/update', $scope.data)
                    .success(function(data) {
                        $scope.message = 'Network successfully saved !';
                    })
                    .error(function(data) {
                        console.log('Error cause:', data);
                        $scope.message = 'Network update failed !';
                    });
            };

        }])
    .controller('GeolocationCtrl',
        ['$scope', '$routeParams', '$http', 'Params',
         function ($scope, $routeParams, $http, Params) {


            $scope.reset = function() {
                $scope.message = 'Changes cleared !';
                $scope.data = Params.sample();
            };

            $scope.save = function() {

                $http.post('/sites/update', $scope.data)
                    .success(function(data) {
                        $scope.message = 'Geolocation successfully saved !';
                    })
                    .error(function(data) {
                        console.log('Error cause:', data);
                        $scope.message = 'Geolocation update failed !';
                    });
            };
        }]);

//__________________________________________________________________________
