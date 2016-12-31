'use strict';

angular.module('ownerList', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.owners', {
            	parent: 'session',
                url: '/owners',
                template: '<owner-list></owner-list>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);