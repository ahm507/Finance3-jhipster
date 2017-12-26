(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('ChartsController', ChartsController);

    ChartsController.$inject = ['Charts', 'ChartsSearch', '$sce', '$http'];

    function ChartsController(Charts, ChartsSearch, $sce, $http) {

        var vm = this;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {

             $http({
                  method: 'GET',
                  url: 'api/chartsHtml',
                  transformResponse: function (value) {
                    return value; //no transformation
                  }
                }).then(function successCallback(response) {
                    vm.chartHtml = $sce.trustAsHtml(response.data);
                    // when the response is available
                  }, function errorCallback(response) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                    alert(response);
                  });




        }


            }
})();
