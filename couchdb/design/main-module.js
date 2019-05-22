//__________________________________________________________________________
/**
 * CouchDB experimental library - January 2014

 * @author Jean-Paul Le Fèvre

 * This software is governed by the
 * CeCILL license (http://www.cecill.info/index.en.html)
 */
//__________________________________________________________________________

"use strict";

/**
 * This an AngularJS module
 * @see http://angularjs.org/
 */
var mainModule = angular.module('main-module', ['ui.bootstrap', 'CornerCouch']);

angular.module('main-module')
    .constant('version',
              {'release': '1.0',
               'date':    '10 January 2014'
              })
/**
 * The routing configuration
 */
    .config(function ($routeProvider, $locationProvider) {

        console.log('Main module configuration');
        $locationProvider.html5Mode(true);

        $routeProvider
            .when('/home', {
                templateUrl: '/zikmu/_design/webapp//home.html'
            })
            .when('/discs', {
                templateUrl: '/zikmu/_design/webapp/discs.html'
            })
            .when('/discs/:folder', {
                templateUrl: '/zikmu/_design/webapp/folder.html'
            })
            .when('/artists', {
                templateUrl: '/zikmu/_design/webapp/artists.html'
            })
            .when('/artists/:artist', {
                templateUrl: '/zikmu/_design/webapp/songs.html'
            })
            .when('/files/:id', {
                templateUrl: '/zikmu/_design/webapp/file.html'
            })
            .otherwise({
                redirectTo: '/index.html'
            });
    })
    .run(function() {
        console.log('Main module started ...');
    })

    .controller('MainCtrl',
     ['$rootScope', '$scope', 'version', 'cornercouch', 
      function($rootScope, $scope, version, cornercouch) {

          $scope.header = 'Ma collection de MP trois';
          console.log('MainCtrl version:', version.release, version.date);
          $rootScope.version = 'Version ' + version.release
                         + ' - ' + version.date;

          $rootScope.server = cornercouch("http://irfupce211.extra.cea.fr:5984",
                                          "GET");
          $rootScope.zikdb  = $rootScope.server.getDB('zikmu');

          $rootScope.tag    = {
              filename: undefined,
              disc: undefined,
              artist: undefined,
              album: undefined,
              songname: undefined
          };
      }])

    .controller('DiscsCtrl',
     ['$rootScope', '$scope', function($rootScope, $scope) {

          var query = $rootScope.zikdb.query('mp3', 'discs',
                                             {group: true, limit: 100});
      }])

    .controller('HomeCtrl',
     ['$rootScope', '$scope', function($rootScope, $scope) {

          $scope.header = 'Ma liste complète de fichiers MP3';
      }])

    .controller('FolderCtrl',
     ['$rootScope', '$scope', '$routeParams',
      function($rootScope, $scope, $routeParams) {

          $scope.header = 'Disc ' + $routeParams.folder;
          var query = $rootScope.zikdb.query('mp3', 'files',
                     {key: $routeParams.folder, limit: 200, include_docs: true});
      }])

    .controller('TagCtrl',
     ['$rootScope', '$scope', '$routeParams',
      function($rootScope, $scope, $routeParams) {

          $rootScope.message = null;

          $scope.header = 'Id: ' + $routeParams.id;

          var doc = $rootScope.zikdb.newDoc();

          doc.load($routeParams.id)
              .error( function(data, status) {
                  $rootScope.message = "Can't load doc !";
                  console.log("status", status, "data", data);
              })
              .success(function() {

                  $rootScope.tag.filename = doc.filename;
                  $rootScope.tag.disc     = doc.disc;

                  if (doc.artist) {
                      $rootScope.tag.artist = doc.artist;
                  }
                  if (doc.album) {
                      $rootScope.tag.album = doc.album;
                  }
                  if (doc.songname) {
                      $rootScope.tag.songname = doc.songname;
                  }
              });

          $scope.saveTag = function() {
              doc.artist   = $rootScope.tag.artist;
              doc.album    = $rootScope.tag.album;
              doc.songname = $rootScope.tag.songname;

              doc.save()
                  .error( function(data, status) {
                      $rootScope.message = "Can't save data !";
                      console.log("status", status, "data", data);
                  })
                  .success(function(data) {
                      $rootScope.message = "Data saved !";
                      console.log("Saved:", data);
                  });
          };

          $scope.clearTag = function() {
              $rootScope.tag.artist   = undefined;
              $rootScope.tag.album    = undefined;
              $rootScope.tag.songname = undefined;
         };

      }])

    .controller('ArtistsCtrl',
     ['$rootScope', '$scope', function($rootScope, $scope) {

          var query = $rootScope.zikdb.query('mp3', 'artists',
                                             {group: true, limit: 200});
         $scope.nextClick = function() {
             $rootScope.zikdb.queryNext();
             delete $scope.detail;
         };

         $scope.prevClick = function() {
              $rootScope.zikdb.queryPrev();
             delete $scope.detail;
         };

      }])

    .controller('SongsCtrl',
     ['$rootScope', '$scope', '$routeParams',
      function($rootScope, $scope, $routeParams) {

          $scope.header = 'Artist: ' + $routeParams.artist;
          var query = $rootScope.zikdb.query('mp3', 'songs',
                      {key: $routeParams.artist, limit: 200, include_docs: true});
      }])
;
//__________________________________________________________________________
