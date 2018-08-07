(function () {
    'use strict';

    angular
        .module('petClinicApp')
        .factory('CredentialStorageService', CredentialStorageService);

    CredentialStorageService.$inject = ['$localStorage'];
    function CredentialStorageService($localStorage) {
        var service = {};

        service.SetCredentials = SetCredentials;
        service.SetNewToken = SetNewToken;
        service.ClearCredentials = ClearCredentials;
        service.IsLogged = IsLogged;
        service.GetCurrentUser = GetCurrentUser;

        return service;

        function SetCredentials(username, roles, permissions, token) {
            $localStorage.currentUser = {
                username: username,
                roles: roles,
                permissions: permissions,
                token: token
            };
        }
        
        function SetNewToken(token) {
            $localStorage.currentUser.token = token;
        }
        
        function ClearCredentials() {
            delete $localStorage.currentUser;
        }
        
        function IsLogged() {
            return !!$localStorage.currentUser;
        }
        
        function GetCurrentUser() {
            return $localStorage.currentUser;
        }
    };

})();