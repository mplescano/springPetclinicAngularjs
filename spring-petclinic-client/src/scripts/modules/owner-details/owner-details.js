'use strict';

angular.module('ownerDetails', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.ownerDetails', {
                parent: 'session',
                url: '/owners/details/:ownerId',
                template: '<owner-details></owner-details>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);