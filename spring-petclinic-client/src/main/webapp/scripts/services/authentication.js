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

            $http({
            	method: 'POST',
            	url: GLB_URL_OAUTH + 'oauth/token?grant_type=password', 
            	data: { username: username, password: password },
            	headers: {
            	    'Cache-Control': undefined,
            	    'Content-Type': 'application/x-www-form-urlencoded',
            	    'Authorization': 'Basic ' + window.btoa(GLB_CLIENT_ID1 + ':' + GLB_CLIENT_ID2)
            	},
                transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                }
            })
            .then(function (response) {
            	/**
            	 * The response object has these properties:
            	 * data – {string|Object} – The response body transformed with the transform functions.
            	 * status – {number} – HTTP status code of the response.
            	 * headers – {function([headerName])} – Header getter function.
            	 * config – {Object} – The configuration object that was used to generate the request.
            	 * statusText – {string} – HTTP status text of the response.
            	 * */
                var responseCallback = {success: true, message: 'Invalid token or missing, verify it.'};
                
                var dataJson = response.data;
                
                /*var rawToken = response.headers('Authorization');
                var sizeBearer = "Bearer ".length;
                if (rawToken != '' && rawToken.length > sizeBearer) {
                    var token = rawToken.substring(sizeBearer, rawToken.length);
                    responseCallback = {success: true, message: response.statusText, data: response.data.data, token: token};
                }*/
                callback(responseCallback);
            }, function (response) {
                var responseCallback = {success: false, message: 'Failed conexion to ' + GLB_URL_OAUTH + ', verify it.'};
                if (response.status > -1) {
                    responseCallback = {success: false, message: response.data.message};
                }
                callback(responseCallback);
            });
        }

        function Logout() {
            return $http({method: 'GET', url: GLB_URL_OAUTH + 'logout'});
        }
        
    };
})();