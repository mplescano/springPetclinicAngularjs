'use strict';

angular.module('login', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('nosession.login', {
                parent: 'nosession',
                url: '/login',
                template: '<login></login>'
            })
    }]);