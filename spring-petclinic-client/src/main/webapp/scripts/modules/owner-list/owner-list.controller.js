'use strict';

angular.module('ownerList')
    .controller('OwnerListController', ['$http', function ($http) {
        var self = this;

        $http.get(GLB_URL_API + 'rest/owner/list').then(function (resp) {
            self.owners = resp.data;
        });
    }]);
