'use strict';

angular.module('user', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.user', {
                parent: 'session',
                url: '/users',
                template: '<user></user>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);