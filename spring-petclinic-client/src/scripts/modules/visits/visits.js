'use strict';

angular.module('visits', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.visits', {
            	parent: 'session',
                url: '/owners/:ownerId/pets/:petId/visits',
                template: '<visits></visits>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);
