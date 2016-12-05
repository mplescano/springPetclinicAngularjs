'use strict';

angular.module('ownerDetails', [
    'ngRoute'
]);

angular.module("ownerDetails").component("ownerDetails", {
    templateUrl: "views/owner-details/owner-details.template.html",
    controller: ["$http", '$routeParams', function($http, $routeParams) {
        var self = this;

        $http.get('rest/owner/' + $routeParams.ownerId).then(function(resp) {
            self.owner = resp.data;
        });
    }]
});