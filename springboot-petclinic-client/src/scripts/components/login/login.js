'use strict';

angular.module('login', [
    'ngRoute'
]);

angular.module("login").component("login", {
    templateUrl: "views/fragments/login.html",
    controller: ["$http", '$location', '$routeParams', 'AuthenticationService', 'FlashService', function(
    		$http, $location, $routeParams, AuthenticationService, FlashService) {
        var self = this;

        self.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();

        function login() {
        	self.dataLoading = true;
            AuthenticationService.Login(self.username, self.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(self.username, response.data.roles, response.data.permissions);
                    $location.path('/welcome');
                } else {
                    FlashService.Error(response.message);
                    self.dataLoading = false;
                }
            });
        };
    }],
    controllerAs:'vm'
});