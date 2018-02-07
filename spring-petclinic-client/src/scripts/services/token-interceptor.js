(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('TokenInterceptorService', TokenInterceptorService);

    TokenInterceptorService.$inject = ['$q', 'CredentialStorageService', 'PermPermissionStore'];
    function TokenInterceptorService($q, CredentialStorageService, PermPermissionStore) {
        var interceptorService = {};

        interceptorService.request = Request;
        interceptorService.response = Response;
        interceptorService.responseError = ResponseError;

        return interceptorService;

        function Request(config) {
            if (CredentialStorageService.IsLogged() && CredentialStorageService.GetCurrentUser().token != '') {
                config.headers['Authorization'] = 'Bearer ' + CredentialStorageService.GetCurrentUser().token;
            }
            return config;
        }
        
        function Response(response) {
            if (CredentialStorageService.IsLogged()) {
                var rawToken = response.headers('Authorization');
                var sizeBearer = "Bearer ".length;
                if (rawToken != '' && rawToken.length > sizeBearer) {
                    var token = rawToken.substring(sizeBearer, rawToken.length);
                    CredentialStorageService.SetNewToken(token);
                }
            }
            return response;
        }
        
        function ResponseError(response) {
            if (CredentialStorageService.IsLogged()) {
                if (response.status == 401 || response.status == 440) {//401 Unauthorized, //440 Login Time-out
                    CredentialStorageService.ClearCredentials();
                    PermPermissionStore.clearStore();
                }
            }
            return $q.reject(rejection);
        }
    };

})();