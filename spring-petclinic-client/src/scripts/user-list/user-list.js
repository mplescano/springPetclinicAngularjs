'use strict';

angular.module('userList', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.users', {
                parent: 'session',
                url: '/users',
                template: '<user-list></user-list>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);