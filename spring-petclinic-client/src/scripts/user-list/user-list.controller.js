'use strict';

/**
 * @see http://ui-grid.info/docs/#/tutorial/314_external_pagination
 * @see http://codetunnel.io/angularjs-controller-as-or-scope/
 * @see https://toddmotto.com/digging-into-angulars-controller-as-syntax/
 * @see http://stackoverflow.com/questions/11605917/this-vs-scope-in-angularjs-controllers
 * @see http://stackoverflow.com/questions/14302267/how-to-use-a-filter-in-a-controller
 * */
angular.module('userList')
    .controller('UserListController', ['UserService', '$state', 
                                       '$scope', 'FlashService', 'uiGridConstants', 'clearObjectFilter',
                                       function(UserService, $state, 
                                    		   $scope, FlashService, uiGridConstants, clearObjectFilter) {
        var self = this;

        var getPage = function() {
        	self.dataLoading = true;
        	if (varUserQueryForm == null) {
            	UserService.GetAll(paginationOptions.pageNumber, paginationOptions.pageSize, paginationOptions.sort)
        		.then(function (response) {
        			self.gridOptions.totalItems = response.totalElements;
        			self.gridOptions.data = response.content;
        			self.dataLoading = false;
        		});
        	}
        	else {
            	UserService.GetFiltered(varUserQueryForm, paginationOptions.pageNumber, paginationOptions.pageSize, paginationOptions.sort)
        		.then(function (response) {
        			self.gridOptions.totalItems = response.totalElements;
        			self.gridOptions.data = response.content;
        			self.dataLoading = false;
        		});
        	}
        };
        
        var paginationOptions = {
    	    pageNumber: 0,
    	    pageSize: 25,
    	    sort: [/*"username,asc|desc"*/]
        };
        
        var varUserQueryForm = null; 
        
        self.userQueryForm = {
        		usernameSearch: null,
        		firstNameSearch: null,
        		lastNameSearch: null,
        		dateCreatedIni: null,
        		dateCreatedEnd: null,
        		enabled: null
        };
        
        var copyUserQueryForm = angular.copy(self.userQueryForm);
        
        self.gridOptions = {
        	gridMenuShowHideColumns: false,
        	paginationPageSizes: [paginationOptions.pageSize],
    	    paginationPageSize: paginationOptions.pageSize,
    	    useExternalPagination: true,
    	    useExternalSorting: true,
    	    data: [],
    	    columnDefs: [
    	      { name: 'username', enableSorting: true, enableHiding: false },
    	      { name: 'firstName', enableSorting: true, enableHiding: false },
    	      { name: 'lastName', enableSorting: true, enableHiding: false },
    	      { name: 'createdAt', enableSorting: false, enableHiding: false },
    	      { name: 'enabled', enableSorting: false, enableHiding: false }
    	    ],
    	    onRegisterApi: function(gridApi) {
    	        gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
    	        	paginationOptions.sort = [];
    	        	if (sortColumns.length > 0) {
    	        		sortColumns.forEach(function(itemSortColumn, index) {
        	        		paginationOptions.sort.push(itemSortColumn.name + "," + itemSortColumn.sort.direction);
    	        		});
    	        	}
    	        	getPage();
    	        });
    	        
    	        gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
    	        	paginationOptions.pageNumber = newPage - 1;
    	        	paginationOptions.pageSize = pageSize;
    	        	getPage();
    	        });
    	        
    	        self.gridApi = gridApi;
    	    }
        };
        
        self.submitUserQueryForm = function() {
        	varUserQueryForm = angular.copy(self.userQueryForm);//Clone copying
        	getPage();
        };
        
        self.resetUserQueryForm = function() {
        	clearObjectFilter(self.userQueryForm);
        	varUserQueryForm = null;
        	getPage();
		};
        
        (function initController() {
        	getPage();
        })();
    }]);