'use strict';

/**
 * 
 * */
angular.module('userForm')
    .controller('UserFormController', ['UserService', '$state', '$stateParams', '$scope', 'FlashService', 
                                       'clearObjectFilter', 'convertDateToStringFilter', '$timeout',
                                       function(UserService, $state, $stateParams, $scope, FlashService, 
                                    		   clearObjectFilter, convertDateToStringFilter, $timeout) {
        var self = this;
        
        (function initController() {
            self.requiredPassword = false;
            self.dataLoading = false;
            var userId = $stateParams.userId;
            if (userId > 0) {
                self.dataLoading = true;
                self.title = 'Modify';
                UserService.GetById(userId).then(function(response) {
                    self.user = response;
                    self.dataLoading = false;
                }, function(response) {
                    FlashService.Error(response.message);
                    self.user = {};
                    self.dataLoading = false;
                });
            }
            else {
                self.title = 'Register';
                self.user = {};
            }
            self.submit = submit;
            self.cancel = cancel;
        })();
        
        function submit() {
            self.dataLoading = true;
            var userId = $stateParams.userId;
            if (userId > 0) {
                UserService.Update(self.user)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Successful modification', true);
                        $state.go('session.users');
                    } else {
                        FlashService.Error(response.message);
                    }
                    self.dataLoading = false;
                });
            }
            else {
                UserService.Create(self.user)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Successful registration ', true);
                        $state.go('session.users');
                    } else {
                        FlashService.Error(response.message);
                    }
                    self.dataLoading = false;
                });
            }
        }
        
        function cancel() {
            $state.go('session.users'); 
        }
        
    }]);