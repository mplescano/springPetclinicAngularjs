'use strict';

angular.module('ownerForm')
    .controller('OwnerFormController', ["$http", '$state', '$stateParams', function ($http, $state, $stateParams) {
        var self = this;

        var ownerId = $stateParams.ownerId || 0;

        if (!ownerId) {
            self.owner = {};
        } else {
            $http.get("rest/owner/" + ownerId).then(function (resp) {
                self.owner = resp.data;
            });
        }

        self.submitOwnerForm = function () {
            var id = self.owner.id;
            var req;
            if (id) {
                req = $http.put("rest/owner/" + id, self.owner);
            } else {
                req = $http.post("rest/owner", self.owner);
            }

            req.then(function () {
                $state.go('session.owners');
            }, function (response) {
                var error = response.data;
                alert(error.message + "\r\n" + error.data.map(function (e) {
                        return e.field + ": " + e.defaultMessage;
                    }).join("\r\n"));
            });
        };
    }]);
