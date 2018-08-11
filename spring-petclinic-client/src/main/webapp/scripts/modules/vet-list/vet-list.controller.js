'use strict';

angular.module('vetList')
    .controller('VetListController', ['$http', function ($http) {
        var self = this;

        $http.get(GLB_URL_API + 'rest/vets').then(function (resp) {
            self.vetList = resp.data;
        });
    }]);
