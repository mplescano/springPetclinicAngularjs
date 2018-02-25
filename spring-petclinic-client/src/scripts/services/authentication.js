(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('AuthenticationService', AuthenticationService);

    AuthenticationService.$inject = ['$http', '$rootScope', '$timeout'];
    function AuthenticationService($http, $rootScope, $timeout) {
        var service = {};

        service.Login = Login;
        service.Logout = Logout;

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
            	var responseCallback = response.data;
                callback(responseCallback);
            });
        }

        function Logout() {
            return $http({method: 'GET', url: 'logout'});
        }
    };

})();