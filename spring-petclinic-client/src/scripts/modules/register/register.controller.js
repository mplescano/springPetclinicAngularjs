
angular.module('register')
    .controller('RegisterController', ['UserService', '$state', 
                                       '$rootScope', 'FlashService', 
                                       function(UserService, $state, 
                                    		   $rootScope, FlashService) {
        var self = this;

        self.register = register;

        function register() {
        	self.dataLoading = true;
            UserService.Create(self.user)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Registration successful', true);
                        $state.go('nosession.login');
                    } else {
                        FlashService.Error(response.message);
                        self.dataLoading = false;
                    }
                });
        }

    }]);