(function () {
'use strict';

//@see https://github.com/johnpapa/angular-styleguide/blob/master/a1/README.md#services

angular
	.module('petClinicApp')
    .factory('UserService', UserService);

UserService.$inject = ['$http', '$httpParamSerializer', '$q'];
function UserService($http, $httpParamSerializer, $q) {
    var service = {};

    service.GetAll = GetAll;
    service.GetById = GetById;
    service.GetByUsername = GetByUsername;
    service.Register = Register;
    service.Create = Create;
    service.Update = Update;
    service.Delete = Delete;
    service.DeleteUserList = DeleteUserList;
    service.GetFiltered = GetFiltered;

    return service;

    function GetAll(pageNumber, pageSize, arrSort/*["name,direction",]*/) {
    	 var paramUrlSort = '';
    	if (arrSort != null && arrSort.length > 0) {
    		paramUrlSort = '&' + $httpParamSerializer({'sort': arrSort});
    	}
        return $http.get('rest/users?page=' + pageNumber + '&size=' + pageSize + paramUrlSort)
        	.then(handleSuccess, handleError('Error getting all users'));
    }
    
    function GetFiltered(objDataModel, pageNumber, pageSize, arrSort/*["name,direction",]*/) {
    	var paramUrlSort = '';
    	if (arrSort != null && arrSort.length > 0) {
    		paramUrlSort = '&' + $httpParamSerializer({'sort': arrSort});
    	}
    	return $http.post(
    			'rest/users/filter?page=' + pageNumber + '&size=' + pageSize + paramUrlSort,
    			objDataModel
    			).then(handleSuccess, handleError('Error getting the users'));
   }

    function GetById(id) {
        return $http.get('rest/users/' + id).then(handleSuccess, handleError('Error getting user by id'));
    }

    function GetByUsername(username) {
        return $http.get('api/users/' + username).then(handleSuccess, handleError('Error getting user by username'));
    }

    function Create(user) {
        return $http.post('rest/users', user).then(handleSuccess, handleError('Error creating user'));
    }
    
    function Register(user) {
        return $http.post('rest/users/register', user).then(handleSuccess, handleError('Error creating user'));
    }

    function Update(user) {
        return $http.put('rest/users/' + user.id, user).then(handleSuccess, handleError('Error updating user'));
    }

    function Delete(userId) {
        return $http.delete('api/users/' + userId).then(handleSuccess, handleError('Error deleting user'));
    }
    
    function DeleteUserList(arrUserIds) {
    	var paramUrl = '';
    	if (arrUserIds != null && arrUserIds.length > 0) {
    		paramUrl = $httpParamSerializer({'userIds': arrUserIds});
    	}
        return $http.delete('rest/users?' + paramUrl).then(handleSuccess, handleError('Error deleting user'));
    }

    // private functions

    function handleSuccess(res) {
        return res.data;
    }

    //@see http://stackoverflow.com/questions/26186851/how-does-one-chain-successive-consecutive-http-posts-in-angular
    function handleError(error) {
        return function () {
            return $q.reject({ success: false, message: error });
        };
    }
}

})();