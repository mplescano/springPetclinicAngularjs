'use strict';

angular.module('login')
    .controller('LoginController', ["$http", '$state', 'AuthenticationService', 
                                    'FlashService', 'PermPermissionStore', 
                                    function ($http, $state, AuthenticationService, 
                                    		FlashService, PermPermissionStore) {
        var self = this;

        self.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
            PermPermissionStore.clearStore();
        })();

        function login() {
        	self.dataLoading = true;
            AuthenticationService.Login(self.username, self.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(self.username, response.data.roles, response.data.permissions);
                    PermPermissionStore.defineManyPermissions(response.data.permissions, /*@ngInject*/ function (permissionName) {
                    	  return true;
                    });
                    
                    $state.go('session.welcome');
                } else {
                    FlashService.Error(response.message);
                    self.dataLoading = false;
                }
            });
        };

    }]);
