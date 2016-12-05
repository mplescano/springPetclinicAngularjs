'use strict';

angular.module('vetList', [
    'ngRoute'
]);

angular.module("vetList").component("vetList", {
    templateUrl: "views/vet-list/vet-list.template.html",
    controller: ["$http", '$routeParams', function($http) {
        var self = this;

        $http.get('rest/vets').then(function(resp) {
            self.vetList = resp.data;
        });
    }]
});