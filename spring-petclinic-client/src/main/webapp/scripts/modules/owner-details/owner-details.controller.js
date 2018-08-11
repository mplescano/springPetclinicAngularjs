'use strict';

angular.module('ownerDetails')
    .controller('OwnerDetailsController', ['$http', '$stateParams', function ($http, $stateParams) {
        var self = this;

        $http.get(GLB_URL_API + 'rest/owner/' + $stateParams.ownerId).then(function (resp) {
            self.owner = resp.data;
        });
    }]);
