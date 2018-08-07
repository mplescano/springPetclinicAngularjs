'use strict';

angular.module('userForm', ['ui.router', 'ngTouch', 'ui.grid', 'ui.grid.pagination', 'ui.grid.selection'])
    .config(['$stateProvider', '$qProvider', function ($stateProvider, $qProvider) {
        
        $stateProvider
            .state('session.userForm', {
                parent: 'session',
                url: '/user/?userId',
                template: '<user-form></user-form>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
}]);