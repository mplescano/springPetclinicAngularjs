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
                
                /**
                 * access_token
                 * token_type
                 * refresh_token
                 * expires_in
                 * scope
                 * rti
                 * roles
                 * permissions
                 * 
                 */
                var dataJson = response.data;
                if (dataJson != null) {
                	responseCallback = {success: true, message: response.statusText, 
                	        data: {roles: dataJson.roles, permissions: dataJson.permissions}, 
                	        token: {
                	            accessToken: dataJson.access_token, 
                	            refreshToken: dataJson.refresh_token, 
                	            expiresIn: dataJson.expires_in,
                	            scope: dataJson.scope,
                	            tokenType: dataJson.token_type,
                	            loginDate: (new Date()).getTime()
                	        }
                	};
                	
                }
                callback(responseCallback);
            }, function (response) {
                var responseCallback = {success: false, message: 'Failed conexion to ' + GLB_URL_OAUTH + ', verify it.'};
                if (response.status > -1) {
                    responseCallback = {success: false, message: response.data.message};
                }
                callback(responseCallback);
            });
        }

        function Logout(accessToken) {
            return $http({
                method: 'DELETE', 
                url: GLB_URL_OAUTH + 'oauth/token',
                headers: {
                    'Cache-Control': undefined,
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': 'Basic ' + window.btoa(GLB_CLIENT_ID1 + ':' + GLB_CLIENT_ID2)
                },
                params: { 'token': accessToken, 'type': 'all' },
                transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                }
            });
            
        }
        
    };
})();