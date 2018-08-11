(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('TokenInterceptorService', TokenInterceptorService);

    TokenInterceptorService.$inject = ['$q', 'CredentialStorageService', 'PermPermissionStore'];
    function TokenInterceptorService($q, CredentialStorageService, PermPermissionStore) {
        var interceptorService = {};

        interceptorService.request = Request;
        /*interceptorService.response = Response;*/
        interceptorService.responseError = ResponseError;

        return interceptorService;

        function Request(config) {
            if (config.url.startsWith(GLB_URL_API + 'rest/') &&
                    CredentialStorageService.IsLogged() && 
                    CredentialStorageService.GetCurrentUser().token.accessToken != '') {
                var nowDate = (new Date()).getTime();
                var loginDate = CredentialStorageService.GetCurrentUser().token.loginDate;
                var expiresIn = CredentialStorageService.GetCurrentUser().token.expiresIn;
                if (nowDate - loginDate > expiresIn) {
                    var deferredConfig = $q.defer();
                    var refreshToken = CredentialStorageService.GetCurrentUser().token.refreshToken;
                    //refresh access token
                    $http({
                        method: 'POST', 
                        url: GLB_URL_OAUTH + 'oauth/token?grant_type=refresh_token',
                        headers: {
                            'Cache-Control': undefined,
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'Authorization': 'Basic ' + window.btoa(GLB_CLIENT_ID1 + ':' + GLB_CLIENT_ID2)
                        },
                        data: { client_id: GLB_CLIENT_ID1, refresh_token: refreshToken },
                        transformRequest: function(obj) {
                            var str = [];
                            for(var p in obj)
                                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                            return str.join("&");
                        }
                    })
                    .then(function(response) {
                        var dataJson = response.data;
                        if (dataJson != null) {
                            
                        }
                        deferredConfig.resolve(config);
                    }, function(response) {
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