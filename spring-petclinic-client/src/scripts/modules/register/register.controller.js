
angular.module('register')
    .controller('RegisterController', ['UserService', '$state', 
                                       '$rootScope', 'FlashService', 
                                       function(UserService, $state, 
                                    		   $rootScope, FlashService) {
        var self = this;
        
        (function initController() {
            self.dataLoading = false;
            self.user = {};
            self.title = 'Register';
            self.submit = register;
            self.cancel = cancel;
        })();

        function register() {
        	self.dataLoading = true;
            UserService.Register(self.user)
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
        
        function cancel() {
            $state.go('nosession.login'); 
        }

    }]);