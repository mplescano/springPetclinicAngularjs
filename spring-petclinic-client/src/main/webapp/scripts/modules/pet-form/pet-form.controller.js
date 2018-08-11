'use strict';

angular.module('petForm')
    .controller('PetFormController', ['$http', '$state', '$stateParams', function ($http, $state, $stateParams) {
        var self = this;
        var ownerId = $stateParams.ownerId || 0;

        $http.get(GLB_URL_API + 'rest/petTypes').then(function (resp) {
            self.types = resp.data;
        }).then(function () {

            var petId = $stateParams.petId || 0;

            if (petId) { // edit
                $http.get(GLB_URL_API + "rest/owner/" + ownerId + "/pet/" + petId).then(function (resp) {
                    self.pet = resp.data;
                    self.pet.birthDate = new Date(self.pet.birthDate);
                    self.petTypeId = "" + self.pet.type.id;
                });
            } else {
                $http.get(GLB_URL_API + 'rest/owner/' + ownerId).then(function (resp) {
                    self.pet = {
                        owner: resp.data.firstName + " " + resp.data.lastName
                    };
                    self.petTypeId = "1";
                })

            }
        });

        self.submit = function () {
            var id = self.pet.id || 0;

            var data = {
                id: id,
                name: self.pet.name,
                birthDate: self.pet.birthDate,
                typeId: self.petTypeId
            };

            var req;
            if (id) {
                req = $http.put(GLB_URL_API + "rest/owners/" + ownerId + "/pets/" + id, data);
            } else {
                req = $http.post(GLB_URL_API + "rest/owners/" + ownerId + "/pets", data);
            }

            req.then(function () {
                $state.go("session.owners", {ownerId: ownerId});
            }, function (response) {
                var error = response.data;
                error.errors = error.errors || [];
                alert(error.message + "\r\n" + error.data.map(function (e) {
                        return e.field + ": " + e.defaultMessage;
                    }).join("\r\n"));
            });
        };
    }]);
