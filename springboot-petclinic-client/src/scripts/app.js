'use strict';
/* App Module */
var petClinicApp = angular.module('petClinicApp', [
    'ngRoute', 'ngCookies', 'route-segment', 'view-segment', 'layoutNav', 'layoutFooter',
    'ownerList', 'ownerDetails', 'ownerForm', 'petForm', 'visits', 'vetList', 'login', 'register']);

petClinicApp.config(config).run(run);

config.$inject = ['$locationProvider', '$routeProvider', '$httpProvider', '$routeSegmentProvider'];
function config($locationProvider, $routeProvider, $httpProvider, $routeSegmentProvider) {

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
    	.when('/login',          						'nosession')
    	.when('/register',       						'nosession.register')
    	
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
            .up()
        .segment('nosession', {
        	templateUrl: 'views/fragments/nosession.html'
        })
        .within()
            .segment('login', {
                'default': true,
                template: '<login></login>'
            })
            .segment('register', {
            	template : '<register></register>',
            })
        ;
    
    $routeProvider.otherwise({redirectTo: '/login'});
};

run.$inject = ['$rootScope', '$location', '$cookieStore', '$http'];
function run($rootScope, $location, $cookieStore, $http) {
    // keep user logged in after page refresh
    $rootScope.globals = $cookieStore.get('globals') || {};
    if ($rootScope.globals.currentUser) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
    }

    $rootScope.$on('$locationChangeStart', function (event, next, current) {
        // redirect to login page if not logged in and trying to access a restricted page
    	var restrictedPage = ['/login', '/register'].indexOf($location.path()) === -1;
        var loggedIn = $rootScope.globals.currentUser;
        if (restrictedPage && !loggedIn) {
            $location.path('/login');
        }
    });
}

['nav', 'footer'].forEach(function(c) {
    var mod = 'layout' + c.toUpperCase().substring(0, 1) + c.substring(1);
    angular.module(mod, []);
    angular.module(mod).component(mod, {
        templateUrl: "views/fragments/" + c + ".html"
    });
});