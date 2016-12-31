'use strict';

angular.module('petForm', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.petNew', {
            	parent: 'session',
                url: '/owners/:ownerId/new-pet',
                template: '<pet-form></pet-form>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
            .state('session.petEdit', {
            	parent: 'session',
                url: '/owners/:ownerId/pets/:petId',
                template: '<pet-form></pet-form>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);
