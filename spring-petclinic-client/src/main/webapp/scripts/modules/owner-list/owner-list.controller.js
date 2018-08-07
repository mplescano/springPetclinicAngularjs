'use strict';

angular.module('ownerList')
    .controller('OwnerListController', ['$http', function ($http) {
        var self = this;

        $http.get('rest/owner/list').then(function (resp) {
            self.owners = resp.data;
        });
    }]);
