'use strict';

/**
 * @see http://ui-grid.info/docs/#/tutorial/314_external_pagination
 * @see http://codetunnel.io/angularjs-controller-as-or-scope/
 * @see https://toddmotto.com/digging-into-angulars-controller-as-syntax/
 * @see http://stackoverflow.com/questions/11605917/this-vs-scope-in-angularjs-controllers
 * @see http://stackoverflow.com/questions/14302267/how-to-use-a-filter-in-a-controller
 * @see http://ui-grid.info/docs/#/tutorial/210_selection
 * */
angular.module('userList')
    .controller('UserListController', ['UserService', '$state', '$scope', 'FlashService', 'uiGridConstants', 
                                       'clearObjectFilter', 'convertDateToStringFilter', '$timeout',
                                       function(UserService, $state,  $scope, FlashService, uiGridConstants, 
                                    		   clearObjectFilter, convertDateToStringFilter, $timeout) {
        var self = this;

        var getPage = function() {
        	self.dataLoading = true;
        	if (varUserQueryForm == null) {
            	UserService.GetAll(paginationOptions.pageNumber, paginationOptions.pageSize, paginationOptions.sort)
        		.then(function (response) {
        			self.gridOptions.totalItems = response.totalElements;
        			self.gridOptions.data = response.content;
        			self.dataLoading = false;
        		}, function(response) {
        			alert(response.message);
        		});
        	}
        	else {
            	UserService.GetFiltered(varUserQueryForm, paginationOptions.pageNumber, paginationOptions.pageSize, paginationOptions.sort)
        		.then(function (response) {
        			self.gridOptions.totalItems = response.totalElements;
        			self.gridOptions.data = response.content;
        			self.dataLoading = false;
        		}, function(response) {
        			alert(response.message);
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
        
		self.deleteUsers = function() {
			//gridApi.selection.getSelectedRows();
		    var arrRows = self.gridApi.selection.getSelectedRows();
		    //arrRows.forEach(function(item, index){
		    //    alert(item.id);
		    //});
		    UserService.DeleteUserList(arrRows.map(function(item){return item.id;}))
		    .then(function (response) {
		    	$timeout(function(){getPage();}, 1000);//setTimeout because the DB in java is delaying in propagate the changes in the table's data  
        	}, function(response) {
        		alert(response.message);
        	});
		}
        
        self.gridOptions = {
        	gridMenuShowHideColumns: false,
        	paginationPageSizes: [paginationOptions.pageSize],
    	    paginationPageSize: paginationOptions.pageSize,
    	    useExternalPagination: true,
    	    useExternalSorting: true,
    	    
    	    enableRowSelection: true,
    	    enableSelectAll: true,
    	    multiSelect: true,
    	    
    	    data: [],
    	    columnDefs: [
    	      { name: 'username', enableSorting: true, enableHiding: false },
    	      { name: 'firstName', enableSorting: true, enableHiding: false },
    	      { name: 'lastName', enableSorting: true, enableHiding: false },
    	      { name: 'createdAt', enableSorting: false, enableHiding: false },
    	      { name: 'enabled', enableSorting: false, enableHiding: false },
    	      { name: 'operations', displayName:'Operations', enableSorting: false, enableHiding: false,
    	    	  cellTemplate: '<button class="btn btn-xs btn-primary" >Edit</button>&nbsp;<button class="btn btn-xs btn-primary">Delete</button>'  
    	      }
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
        	varUserQueryForm = convertDateToStringFilter(angular.copy(self.userQueryForm), 'yyyy-MM-dd');//Clone copying
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