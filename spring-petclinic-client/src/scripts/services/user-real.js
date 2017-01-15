(function () {
'use strict';

//@see https://github.com/johnpapa/angular-styleguide/blob/master/a1/README.md#services

angular
	.module('petClinicApp')
    .factory('UserService', UserService);

UserService.$inject = ['$http', '$httpParamSerializer'];
function UserService($http, $httpParamSerializer) {
    var service = {};

    service.GetAll = GetAll;
    service.GetById = GetById;
    service.GetByUsername = GetByUsername;
    service.Create = Create;
    service.Update = Update;
    service.Delete = Delete;
    service.GetFiltered = GetFiltered;

    return service;

    function GetAll(pageNumber, pageSize, arrSort/*["name,direction",]*/) {
    	 var paramUrlSort = '';
    	if (arrSort != null && arrSort.length > 0) {
    		paramUrlSort = '&' + $httpParamSerializer({'sort': arrSort});
    	}
        return $http.get('/rest/users?page=' + pageNumber + '&size=' + pageSize + paramUrlSort)
        	.then(handleSuccess, handleError('Error getting all users'));
    }
    
    function GetFiltered(objDataModel, pageNumber, pageSize, arrSort/*["name,direction",]*/) {
    	var paramUrlSort = '';
    	if (arrSort != null && arrSort.length > 0) {
    		paramUrlSort = '&' + $httpParamSerializer({'sort': arrSort});
    	}
    	return $http.post(
    			'/rest/users/filter?page=' + pageNumber + '&size=' + pageSize + paramUrlSort,
    			objDataModel
    			).then(handleSuccess, handleError('Error getting the users'));
   }

    function GetById(id) {
        return $http.get('/api/users/' + id).then(handleSuccess, handleError('Error getting user by id'));
    }

    function GetByUsername(username) {
        return $http.get('/api/users/' + username).then(handleSuccess, handleError('Error getting user by username'));
    }

    function Create(user) {
        return $http.post('rest/users', user).then(handleSuccess, handleError('Error creating user'));
    }

    function Update(user) {
        return $http.put('/api/users/' + user.id, user).then(handleSuccess, handleError('Error updating user'));
    }

    function Delete(id) {
        return $http.delete('/api/users/' + id).then(handleSuccess, handleError('Error deleting user'));
    }

    // private functions

    function handleSuccess(res) {
        return res.data;
    }

    function handleError(error) {
        return function () {
            return { success: false, message: error };
        };
    }
}

})();