'use strict';
/* App Module */
var petClinicApp = angular.module('petClinicApp', [
    'ngRoute', 'route-segment', 'view-segment', 'layoutNav', 'layoutFooter',
    'ownerList', 'ownerDetails', 'ownerForm', 'petForm', 'visits', 'vetList']);

petClinicApp.config(['$locationProvider', '$routeProvider', '$httpProvider', '$routeSegmentProvider', function(
    $locationProvider, $routeProvider, $httpProvider, $routeSegmentProvider) {

    // safari turns to be lazy sending the Cache-Control header
    $httpProvider.defaults.headers.common["Cache-Control"] = 'no-cache';

    $locationProvider.hashPrefix('!');

    /*$routeProvider
    	.when('/welcome', {
    		template: '<layout-welcome></layout-welcome>'
    	})
    	.when('/owners/:ownerId', {
    		template: '<owner-details></owner-details>'
    	})
    	.when('/owners', {
    		template: '<owner-list></owner-list>'
    	})
    	.when('/owners/:ownerId/edit', {
    		template: '<owner-form></owner-form>'
    	})
    	.when('/new-owner', {
    		template: '<owner-form></owner-form>'
    	})
    	.when('/owners/:ownerId/new-pet', {
    		template: '<pet-form></pet-form>'
    	})
    	.when('/owners/:ownerId/pets/:petId', {
    		template: '<pet-form></pet-form>'
    	})
    	.when('/owners/:ownerId/pets/:petId/visits', {
    		template: '<visits></visits>'
    	})
    	.when('/vets', {
    		template: '<vet-list></vet-list>'
    	})
    	.otherwise('/welcome');
    */
    
    // Configuring provider options
    $routeSegmentProvider.options.autoLoadTemplates = true;
    $routeSegmentProvider
    	.when('/welcome',        						'session')
    	.when('/owners/:ownerId',   					'session.ownersView')
    	.when('/owners',   								'session.ownersList')
    	.when('/owners/:ownerId/edit',  				'session.ownersEdit')
    	.when('/new-owner',  							'session.ownersNew')
    	.when('/owners/:ownerId/new-pet',  				'session.petsNew')
    	.when('/owners/:ownerId/pets/:petId',  			'session.petsEdit')
    	.when('/owners/:ownerId/pets/:petId/visits',  	'session.visitsList')
    	.when('/vets',   								'session.vetsList')
    	//.when('/login',          						'login')
    	//.when('/register',       						'register')
    	
        .segment('session', {
        	templateUrl: 'views/fragments/session.html'
        })
        .within()
            .segment('home', {
                'default': true,
                templateUrl: 'views/fragments/home.html'
            })
            .segment('ownersView', {
            	template: '<owner-details></owner-details>'
            })
            .segment('ownersList', {
            	template: '<owner-list></owner-list>'
            })
            .segment('ownersEdit', {
            	template: '<owner-form></owner-form>'
            })
            .segment('ownersNew', {
            	template: '<owner-form></owner-form>'
            })
            .segment('petsNew', {
            	template: '<pet-form></pet-form>'
            })
            .segment('petsEdit', {
            	template: '<pet-form></pet-form>'
            })
            .segment('visitsList', {
            	template: '<visits></visits>'
            })
            .segment('vetsList', {
            	template: '<vet-list></vet-list>'
            })
        /*.up()
        .segment('login', {
        	template : '<login></login>',
        })
        .segment('register', {
        	template : '<register></register>',
        })*/;
    
    $routeProvider.otherwise({redirectTo: '/welcome'});
}]);

/*
['welcome', 'nav', 'footer'].forEach(function(c) {
    var mod = 'layout' + c.toUpperCase().substring(0, 1) + c.substring(1);
    angular.module(mod, []);
    angular.module(mod).component(mod, {
        templateUrl: "views/fragments/" + c + ".html"
    });
});*/

['nav', 'footer'].forEach(function(c) {
    var mod = 'layout' + c.toUpperCase().substring(0, 1) + c.substring(1);
    angular.module(mod, []);
    angular.module(mod).component(mod, {
        templateUrl: "views/fragments/" + c + ".html"
    });
});