'use strict';

angular.module('login')
    .controller('LoginController', ["$http", '$state', '$stateParams', 'AuthenticationService', 'CredentialStorageService',
                                    'FlashService', 'PermPermissionStore', 
                                    function ($http, $state, $stateParams, AuthenticationService, CredentialStorageService,
                                    		FlashService, PermPermissionStore) {
        var self = this;

        self.login = login;

        (function initController() {
            // reset login status
            var fromLink = $stateParams.from;
            if (fromLink == 'logout' && CredentialStorageService.IsLogged()) {
                AuthenticationService.Logout().then(function() {
                    CredentialStorageService.ClearCredentials();
                    PermPermissionStore.clearStore();
                });
            }
            else if (CredentialStorageService.IsLogged()) {
                $state.go('session.welcome');
            }
        })();

        function login() {
        	self.dataLoading = true;
            AuthenticationService.Login(self.username, self.password, function (response) {
                if (response.success) {
                    CredentialStorageService.SetCredentials(self.username, response.data.roles, response.data.permissions);
                    PermPermissionStore.defineManyPermissions(response.data.permissions, /*@ngInject*/ function (permissionName) {
                    	  return true;
                    });
                    $state.go('session.welcome');
                    self.dataLoading = false;
                } else {
                    FlashService.Error(response);
                    self.dataLoading = false;
                }
            });
        };

    }]);
