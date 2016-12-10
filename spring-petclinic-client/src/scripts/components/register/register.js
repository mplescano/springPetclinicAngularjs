(function () {
'use strict';

angular.module('register', ['ngRoute']);

angular.module("register").component("register", {
    templateUrl: "views/fragments/register.html",
    controller: ['UserService', '$location', '$rootScope', 'FlashService', function(UserService, $location, $rootScope, FlashService) {
        var self = this;

        self.register = register;

        function register() {
        	self.dataLoading = true;
            UserService.Create(self.user)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Registration successful', true);
                        $location.path('/login');
                    } else {
                        FlashService.Error(response.message);
                        self.dataLoading = false;
                    }
                });
        }

    }],
    controllerAs:'vm'
});

})();