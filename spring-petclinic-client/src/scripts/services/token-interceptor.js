(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('TokenInterceptorService', TokenInterceptorService);

    TokenInterceptorService.$inject = ['$localStorage'];
    function TokenInterceptorService($localStorage) {
        var interceptorService = {};

        interceptorService.request = Request;
        interceptorService.response = Response;
        interceptorService.responseError = ResponseError;

        return interceptorService;

        function Request(config) {
            if ($localStorage.currentUser && $localStorage.currentUser.token != '') {
                config.headers['Authorization'] = 'Bearer ' + $localStorage.currentUser.token;
            }
        }
        
        function Response(response) {
            if ($localStorage.currentUser) {
                
            }
        }
        
        function ResponseError(response) {
            if ($localStorage.currentUser) {
                //TODO detect expired token...
            }
        }
    };

})();