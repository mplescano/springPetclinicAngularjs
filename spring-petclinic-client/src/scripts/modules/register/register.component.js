(function () {
    'use strict';
    
    angular.module("register")
    	.component("register", {
        templateUrl: "scripts/modules/register/register.template.html",
        controller: 'RegisterController',
        controllerAs:'vm'
    });

})();