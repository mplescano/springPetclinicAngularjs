'use strict';

angular.module('userList')
    .controller('UserListController', ['UserService', '$state', 
                                       '$rootScope', 'FlashService', 
                                       function(UserService, $state, 
                                    		   $rootScope, FlashService) {
        var self = this;
        self.pageNumber = 0;
        self.pageSize = 5;

        self.search = search;
        
        (function initController() {
        	UserService.GetAll(self.pageNumber, self.pageSize)
        		.then(function (response) {
        			self.users = response.content;
        		});
        })();

        function search() {
        	self.dataLoading = true;
            /*UserService.Create(self.user)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Registration successful', true);
                        $state.go('nosession.login');
                    } else {
                        FlashService.Error(response.message);
                        self.dataLoading = false;
                    }
                });*/
        }

    }]);