'use strict';
/* App Module */
var petClinicApp = angular.module('petClinicApp', ['ngCookies', 
    'ui.router', 'permission', 'permission.ui', 'layoutNav', 'layoutFooter',
    'ownerList', 'ownerDetails', 'ownerForm', 'petForm', 'visits', 'vetList',
    'login', 'register', 'userList']);

petClinicApp.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', '$httpProvider', function(
    $stateProvider, $urlRouterProvider, $locationProvider, $httpProvider) {

    // safari turns to be lazy sending the Cache-Control header
    $httpProvider.defaults.headers.common["Cache-Control"] = 'no-cache';

    //Cookie-based Authentication
    //@see http://blog.ionic.io/angularjs-authentication/
    //@see http://stackoverflow.com/questions/32990836/get-jsessionid-value-and-create-cookie-in-angularjs
    //@see http://stackoverflow.com/questions/15026016/set-cookie-in-http-header-is-ignored-with-angularjs 
    $httpProvider.defaults.withCredentials = true;
    
    $locationProvider.hashPrefix('!');
    
    // Normal usage (creates INFDG error)
    $urlRouterProvider.otherwise('/login');
	// Use instead
	$urlRouterProvider.otherwise( function($injector) {
	    var $state = $injector.get("$state");
	    $state.go('nosession.login');
	});
    $stateProvider
        .state('app', {
            abstract: true,
            url: '',
            template: '<ui-view></ui-view>'
        })
        .state('session', {
            parent: 'app',
            templateUrl: 'scripts/fragments/session.html'
        })
        .state('session.welcome', {
            parent: 'session',
            url: '/welcome',
            templateUrl: 'scripts/fragments/welcome.html',
            data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
        })
        .state('nosession', {
            parent: 'app',
            templateUrl: 'scripts/fragments/nosession.html'
        });
}]);

petClinicApp.run(['$rootScope', '$location', '$cookieStore', '$http', 'PermRoleStore', 
            function($rootScope, $location, $cookieStore, $http, PermRoleStore) {
    // keep user logged in after page refresh
    $rootScope.globals = $cookieStore.get('globals') || {};
    
    PermRoleStore.defineRole('AUTHORIZED', ['$rootScope', function ($rootScope) {
        return !!$rootScope.globals.currentUser;
    }]);
}]);

//@see http://stackoverflow.com/questions/4994201/is-object-empty
//@see http://stackoverflow.com/questions/679915/how-do-i-test-for-an-empty-javascript-object
//@see http://stackoverflow.com/questions/17839141/checking-if-object-is-empty-works-with-ng-show-but-not-from-controller
//@see http://stackoverflow.com/questions/16907723/check-if-a-js-objects-properties-are-all-null-except-for-3
petClinicApp.filter('isEmpty', function() {
	
    var bar;

	// Speed up calls to hasOwnProperty
    var hasOwnProperty = Object.prototype.hasOwnProperty;
    
    return function (obj) {

        // null and undefined are "empty"
        if (obj == null) return true;
        
        if (typeof obj !== "object") throw "Illegal argument, only objects";

        for (bar in obj) {
            if (hasOwnProperty.call(obj, bar) && obj[bar] !== null && obj[bar] !== '') {
                return false;
            }
        }
        return true;
    };
})
.filter('clearObject', function() {
	
    var bar;

	// Speed up calls to hasOwnProperty
    var hasOwnProperty = Object.prototype.hasOwnProperty;
    
    return function (obj) {

        // null and undefined are "empty"
        if (obj == null) return true;
        
        if (typeof obj !== "object") throw "Illegal argument, only objects";

        for (bar in obj) {
            if (hasOwnProperty.call(obj, bar) && obj[bar] !== null && obj[bar] !== '') {
            	obj[bar] = null;
            }
        }
        return obj;
    };
});

['nav', 'footer'].forEach(function(c) {
    var mod = 'layout' + c.toUpperCase().substring(0, 1) + c.substring(1);
    angular.module(mod, []);
    angular.module(mod).component(mod, {
        templateUrl: "scripts/fragments/" + c + ".html"
    });
});