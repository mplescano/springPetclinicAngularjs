'use strict';

angular.module('register', ['ui.router'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
	        .state('nosession.register', {
	        	parent: 'nosession',
	            url: '/register',
	            template: '<register></register>'
	        })
    }]);