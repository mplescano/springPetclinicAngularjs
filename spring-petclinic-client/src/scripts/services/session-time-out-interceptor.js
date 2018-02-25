(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('SessionTimeOutInterceptorService', SessionTimeOutInterceptorService);

    SessionTimeOutInterceptorService.$inject = ['$q', 'CredentialStorageService', 'PermPermissionStore'];
    function SessionTimeOutInterceptorService($q, CredentialStorageService, PermPermissionStore) {
        var interceptorService = {};

        interceptorService.responseError = ResponseError;

        return interceptorService;

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