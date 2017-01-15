'use strict';

angular.module('userList', ['ui.router', 'ngTouch', 'ui.grid', 'ui.grid.pagination'])
    .config(['$stateProvider', '$qProvider', function ($stateProvider, $qProvider) {
    	
        //@see https://github.com/angular-ui/ui-grid/issues/5890
        $qProvider.errorOnUnhandledRejections(false);
        
        $stateProvider
            .state('session.users', {
                parent: 'session',
                url: '/users',
                template: '<user-list></user-list>',
                data:{permissions:{only:['AUTHORIZED'], redirectTo:'nosession.login'}}
            })
}]);