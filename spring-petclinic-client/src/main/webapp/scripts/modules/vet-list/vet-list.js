'use strict';

angular.module('vetList', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.vets', {
            	parent: 'session',
                url: '/vets',
                template: '<vet-list></vet-list>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);