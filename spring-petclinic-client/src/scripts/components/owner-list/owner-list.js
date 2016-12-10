'use strict';

angular.module('ownerList', [
    'ngRoute'
]);

angular.module("ownerList").component("ownerList", {
    templateUrl: "views/owner-list/owner-list.template.html",
    controller: ["$http", function ($http) {
        var self = this;
        $http.get('rest/owner/list').then(function(resp) {
            self.owners = resp.data;
        });
    }]
});