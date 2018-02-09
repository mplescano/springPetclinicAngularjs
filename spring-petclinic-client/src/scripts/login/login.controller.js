'use strict';

angular.module('login')
    .controller('LoginController', ["$http", '$state', 'AuthenticationService', 'CredentialStorageService',
                                    'FlashService', 'PermPermissionStore', '$location',
                                    function ($http, $state, AuthenticationService, CredentialStorageService,
                                    		FlashService, PermPermissionStore, $location) {
        var self = this;

        self.login = login;

        (function initController() {
            // reset login status
            var fromLink = ($location.search()).from;
            if (fromLink == 'logout' && CredentialStorageService.IsLogged()) {
                AuthenticationService.Logout();
                CredentialStorageService.ClearCredentials();
                PermPermissionStore.clearStore();
            }
            else if (CredentialStorageService.IsLogged()) {
                $state.go('session.welcome');
            }
        })();

        function login() {
        	self.dataLoading = true;
            AuthenticationService.Login(self.username, self.password, function (response) {
                if (response.success) {
                    CredentialStorageService.SetCredentials(self.username, response.data.roles, response.data.permissions, response.token);
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
