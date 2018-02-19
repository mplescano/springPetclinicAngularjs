(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('FlashService', FlashService);

    FlashService.$inject = ['$rootScope'];
    function FlashService($rootScope) {
        var service = {};

        service.Success = Success;
        service.Error = Error;

        initService();

        return service;

        function initService() {
            $rootScope.$on('$locationChangeStart', function () {
                clearFlashMessage();
            });

            function clearFlashMessage() {
                var flash = $rootScope.flash;
                if (flash) {
                    if (!flash.keepAfterLocationChange) {
                        delete $rootScope.flash;
                    } else {
                        // only keep for a single location change
                        flash.keepAfterLocationChange = false;
                    }
                }
            }
        }

        function Success(message, keepAfterLocationChange) {
            $rootScope.flash = {
                message: message,
                type: 'success', 
                keepAfterLocationChange: keepAfterLocationChange
            };
        }

        function Error(response, keepAfterLocationChange) {
            if (angular.isObject(response) && response.hasOwnProperty('message')) {
                if (response.hasOwnProperty('data') && angular.isArray(response.data) && 
                        response.data.length > 0) {
                    $rootScope.flash = {
                        message: response.message,
                        details: response.data,
                        type: 'error',
                        keepAfterLocationChange: keepAfterLocationChange
                    };
                }
                else {
                    $rootScope.flash = {
                        message: response.message,
                        type: 'error',
                        keepAfterLocationChange: keepAfterLocationChange
                    };
                }
            }
            else {
                $rootScope.flash = {
                    message: response,
                    type: 'error',
                    keepAfterLocationChange: keepAfterLocationChange
                };
            }
        }
    }

})();