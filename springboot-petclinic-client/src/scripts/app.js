'use strict';
/* App Module */
var petClinicApp = angular.module('petClinicApp', [
    'ngRoute', 'ngCookies', 'route-segment', 'view-segment', 'permission', 'permission.ng',
    'layoutNav', 'layoutFooter', 'ownerList', 'ownerDetails', 'ownerForm', 'petForm', 'visits', 
    'vetList', 'login', 'register']);

petClinicApp.config(config).run(run);

config.$inject = ['$locationProvider', '$routeProvider', '$httpProvider', '$routeSegmentProvider'];
function config($locationProvider, $routeProvider, $httpProvider, $routeSegmentProvider) {

    // safari turns to be lazy sending the Cache-Control header
    $httpProvider.defaults.headers.common["Cache-Control"] = 'no-cache';
    
    //Cookie-based Authentication
    //@see http://blog.ionic.io/angularjs-authentication/
    //@see http://stackoverflow.com/questions/32990836/get-jsessionid-value-and-create-cookie-in-angularjs
    //@see http://stackoverflow.com/questions/15026016/set-cookie-in-http-header-is-ignored-with-angularjs 
    $httpProvider.defaults.withCredentials = true;

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
    	.when('/welcome',        						'session', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/owners/:ownerId',   					'session.ownersView', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/owners',   								'session.ownersList', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/owners/:ownerId/edit',  				'session.ownersEdit', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/new-owner',  							'session.ownersNew', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/owners/:ownerId/new-pet',  				'session.petsNew', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/owners/:ownerId/pets/:petId',  			'session.petsEdit', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/owners/:ownerId/pets/:petId/visits',  	'session.visitsList', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
    	.when('/vets',   								'session.vetsList', {data:{permissions:{only:['AUTHORIZED'], redirectTo:'/login'}}})
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

run.$inject = ['$rootScope', '$location', '$cookieStore', '$http', 'PermRoleStore'];//
function run($rootScope, $location, $cookieStore, $http, PermRoleStore) {//
    // keep user logged in after page refresh
    $rootScope.globals = $cookieStore.get('globals') || {};
    //if ($rootScope.globals.currentUser) {
    //    $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
    //}
    /*$rootScope.$on('$routeChangeStart', function (event, next, current) {
    	!!next.$$route.data.permissions;
    });*/

    /*$rootScope.$on('$locationChangeStart', function (event, next, current) {
        // redirect to login page if not logged in and trying to access a restricted page
    	var restrictedPage = ['/login', '/register'].indexOf($location.path()) === -1;
        var loggedIn = $rootScope.globals.currentUser;
        if (restrictedPage && !loggedIn) {
            $location.path('/login');
        }
    });*/
    
    PermRoleStore.defineRole('AUTHORIZED', ['$rootScope', function ($rootScope) {
        return !!$rootScope.globals.currentUser;
    }]);
}

['nav', 'footer'].forEach(function(c) {
    var mod = 'layout' + c.toUpperCase().substring(0, 1) + c.substring(1);
    angular.module(mod, []);
    angular.module(mod).component(mod, {
        templateUrl: "views/fragments/" + c + ".html"
    });
});