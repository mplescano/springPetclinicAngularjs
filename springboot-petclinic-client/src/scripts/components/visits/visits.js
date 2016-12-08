'use strict';

angular.module('visits', [
    'ngRoute'
]);

angular.module("visits").component("visits", {
    templateUrl: "views/visits/visits.template.html",
    controller: ["$http", '$routeParams', '$location', '$filter', function ($http, $routeParams, $location, $filter) {
        var self = this;
        var petId = $routeParams.petId || 0;
        var url = "rest/owners/" + ($routeParams.ownerId || 0) + "/pets/" + petId + "/visits";
        self.date = new Date();
        self.desc = "";

        $http.get(url).then(function(resp) {
            self.visits = resp.data;
        });

        self.submit = function() {

            var data = {
                date : $filter('date')(self.date, "yyyy-MM-dd"),
                description : self.desc
            };
            console.log(data);
            $http.post(url, data).then(function() {
                $location.url("rest/owners/" + $routeParams.ownerId);
            }, function (response) {
                var error = response.data;
                alert(error.message + "\r\n" + error.data.map(function (e) {
                        return e.field + ": " + e.defaultMessage;
                    }).join("\r\n"));
            });
        };
    }]
});