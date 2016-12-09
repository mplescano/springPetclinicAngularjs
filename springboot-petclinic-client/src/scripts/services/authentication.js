(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('AuthenticationService', AuthenticationService);

    AuthenticationService.$inject = ['$http', '$cookieStore', '$rootScope', '$timeout', 'UserService'];
    function AuthenticationService($http, $cookieStore, $rootScope, $timeout, UserService) {
        var service = {};

        service.Login = Login;
        service.SetCredentials = SetCredentials;
        service.ClearCredentials = ClearCredentials;

        return service;

        function Login(username, password, callback) {

            /* Dummy authentication for testing, uses $timeout to simulate api call
             ----------------------------------------------*/
//            $timeout(function () {
//                var response;
//                UserService.GetByUsername(username)
//                    .then(function (user) {
//                        if (user !== null && user.password === password) {
//                            response = { success: true };
//                        } else {
//                            response = { success: false, message: 'Username or password is incorrect' };
//                        }
//                        callback(response);
//                    });
//            }, 1000);

            /* Use this for real authentication
             ----------------------------------------------*/
//            $http.post('login', { username: username, password: password })
//                .success(function (response) {
//                    callback(response);
//                });
            $http({
            	method: 'POST',
            	url: 'login', 
            	data: { username: username, password: password },
            	headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                }
            })
            .then(function (response) {
            	/**
            	 * data – {string|Object} – The response body transformed with the transform functions.
            	 * status – {number} – HTTP status code of the response.
            	 * headers – {function([headerName])} – Header getter function.
            	 * config – {Object} – The configuration object that was used to generate the request.
            	 * statusText – {string} – HTTP status text of the response.
            	 * */
            	var responseCallback = {success: true, message: response.statusText, data: response.data.data};
                callback(responseCallback);
            }, function (response) {
            	var responseCallback = {success: false, message: response.statusText};
                callback(responseCallback);
            });
        }

        function SetCredentials(username, roles, permissions) {
            $rootScope.globals = {
                currentUser: {
                    username: username,
                    roles: roles,
                    permissions: permissions
                }
            };

            //$http.defaults.headers.common['Authorization'] = 'Basic ' + authdata; // jshint ignore:line
            $cookieStore.put('globals', $rootScope.globals);// keep user data after page refresh
        }

        function ClearCredentials() {
            $cookieStore.remove('globals');
            //$http.defaults.headers.common.Authorization = 'Basic';
            //TODO call logout in server
          $http({method: 'GET', url: 'logout'})
          .then(function (response) {
              $rootScope.globals = {};
          }, function (response) {
              
          });
        }
    };

})();