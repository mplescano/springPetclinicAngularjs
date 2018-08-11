(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('TokenInterceptorService', TokenInterceptorService);

    TokenInterceptorService.$inject = ['$injector', '$q', 'CredentialStorageService', 'PermPermissionStore'];
    function TokenInterceptorService($injector, $q, CredentialStorageService, PermPermissionStore) {
        var interceptorService = {};

        interceptorService.request = Request;
        /*interceptorService.response = Response;*/
        interceptorService.responseError = ResponseError;

        return interceptorService;

        function Request(config) {
            if (config.url.startsWith(GLB_URL_API + 'rest/') &&
                    CredentialStorageService.IsLogged() && 
                    CredentialStorageService.GetCurrentUser().token.accessToken != '') {
                config.headers['Cache-Control'] = undefined;
                var nowDate = (new Date()).getTime();
                var lastTokenDate = CredentialStorageService.GetCurrentUser().token.lastTokenDate;
                var expiresIn = CredentialStorageService.GetCurrentUser().token.expiresIn * 1000;
                if (nowDate - lastTokenDate > expiresIn) {
                    var deferredConfig = $q.defer();
                    var refreshToken = CredentialStorageService.GetCurrentUser().token.refreshToken;
                    var AuthenticationService = $injector.get('AuthenticationService');
                    //refresh access token
                    AuthenticationService.RefreshToken(refreshToken)
                    .then(function(response) {
                        var dataJson = response.data;
                        if (dataJson != null) {
                            var oldToken = CredentialStorageService.GetCurrentUser().token;
                            var newToken = {
                                    accessToken: dataJson.access_token, 
                                    refreshToken: dataJson.refresh_token, 
                                    expiresIn: dataJson.expires_in,
                                    scope: dataJson.scope,
                                    tokenType: oldToken.tokenType,
                                    loginDate: oldToken.loginDate,
                                    lastTokenDate: (new Date()).getTime()
                            };
                            CredentialStorageService.SetNewToken(newToken);
                            config.headers['Authorization'] = 'Bearer ' + CredentialStorageService.GetCurrentUser().token.accessToken;
                        }
                        deferredConfig.resolve(config);
                    }, function(response) {
                        if (response.status == 401 || response.status == 440) {//401 Unauthorized, //440 Login Time-out
                            CredentialStorageService.ClearCredentials();
                            PermPermissionStore.clearStore();
                        }
                        deferredConfig.resolve(config);
                    });
                    return deferredConfig.promise;
                }
                config.headers['Authorization'] = 'Bearer ' + CredentialStorageService.GetCurrentUser().token.accessToken;
            }
            return config;
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