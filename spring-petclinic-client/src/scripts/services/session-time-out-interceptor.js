(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('SessionTimeOutInterceptorService', SessionTimeOutInterceptorService);

    SessionTimeOutInterceptorService.$inject = ['$q', 'CredentialStorageService', 'PermPermissionStore'];
    function SessionTimeOutInterceptorService($q, CredentialStorageService, PermPermissionStore) {
        var interceptorService = {};

        interceptorService.response = Response;
        interceptorService.responseError = ResponseError;

        return interceptorService;

        function Response(response) {
            if (CredentialStorageService.IsLogged()) {
                var rawToken = response.headers('Authorization');
                var sizeBearer = "Bearer ".length;
                if (rawToken != null && rawToken.length > sizeBearer) {
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
            return $q.reject(response);
        }
    };

})();