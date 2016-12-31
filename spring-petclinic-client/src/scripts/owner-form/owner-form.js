'use strict';

angular.module('ownerForm', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('session.ownerNew', {
            	parent: 'session',
                url: '/owners/new',
                template: '<owner-form></owner-form>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
            .state('session.ownerEdit', {
            	parent: 'session',
                url: '/owners/:ownerId/edit',
                template: '<owner-form></owner-form>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
    }]);
