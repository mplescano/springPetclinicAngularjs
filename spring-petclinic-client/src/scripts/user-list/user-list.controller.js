'use strict';

/**
 * @see http://ui-grid.info/docs/#/tutorial/314_external_pagination
 * */
angular.module('userList')
    .controller('UserListController', ['UserService', '$state', 
                                       '$rootScope', 'FlashService', 'uiGridConstants',
                                       function(UserService, $state, 
                                    		   $rootScope, FlashService, uiGridConstants) {
        var self = this;

        var paginationOptions = {
    	    pageNumber: 0,
    	    pageSize: 25,
    	    sort: null
        };
        
        self.gridOptions = {
    	    paginationPageSizes: [paginationOptions.pageSize],
    	    paginationPageSize: paginationOptions.pageSize,
    	    useExternalPagination: true,
    	    useExternalSorting: true,
    	    data: [],
    	    columnDefs: [
    	      { name: 'username', enableSorting: false },
    	      { name: 'firstName', enableSorting: false },
    	      { name: 'lastName', enableSorting: false },
    	      { name: 'createdAt', enableSorting: false },
    	      { name: 'enabled', enableSorting: false }
    	    ],
    	    onRegisterApi: function(gridApi) {
    	        self.gridApi = gridApi;
    	        self.gridApi.core.on.sortChanged(self, function(grid, sortColumns) {
    	        	if (sortColumns.length == 0) {
    	        		paginationOptions.sort = null;
    	        	}
    	        	else {
    	        		paginationOptions.sort = sortColumns[0].sort.direction;
    	        	}
    	        	getPage();
    	        });
    	        
    	        gridApi.pagination.on.paginationChanged(self, function (newPage, pageSize) {
    	        	paginationOptions.pageNumber = newPage;
    	        	paginationOptions.pageSize = pageSize;
    	        	getPage();
    	        });
    	    }
        };
        

        var getPage = function() {
            /*var url;
            switch(paginationOptions.sort) {
              case uiGridConstants.ASC:
                url = '/data/100_ASC.json';
                break;
              case uiGridConstants.DESC:
                url = '/data/100_DESC.json';
                break;
              default:
                url = '/data/100.json';
                break;
            }*/
         
            /*$http.get(url)
            .success(function (data) {
              $scope.gridOptions.totalItems = 100;
              var firstRow = (paginationOptions.pageNumber - 1) * paginationOptions.pageSize;
              $scope.gridOptions.data = data.slice(firstRow, firstRow + paginationOptions.pageSize);
            });*/
        	self.dataLoading = true;
        	UserService.GetAll(paginationOptions.pageNumber, paginationOptions.pageSize)
    		.then(function (response) {
    			self.gridOptions.totalItems = response.totalElements;
    			self.gridOptions.data = response.content;
    			self.dataLoading = false;
    		});
            
        };
        
        /*(function initController() {
        	getPage();
        })();*/
        getPage();
    }]);