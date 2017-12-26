(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('ChartsController', ChartsController);

    ChartsController.$inject = ['Charts', 'ChartsSearch', '$sce', '$http', 'Transaction'];

    function ChartsController(Charts, ChartsSearch, $sce, $http, Transaction) {

        var vm = this;
        vm.loadAll = loadAll;
        vm.changedSelection = changedSelection;

        function changedSelection() {
            loadAll();
        }

        vm.accountTypes = ['', 'ASSET', 'INCOME', 'EXPENSE', 'LIABILITY', 'OTHER'];

        //Load list of years in transactions
        vm.yearList = Transaction.queryYears({});

        loadAll();

        function loadAll() {

            var pars = {
                        'year': vm.yearSelected,
                        'type': vm.accountTypeSelected
                    };

             $http({
                  method: 'GET',
                  url: 'api/chartsHtml',
                  params: pars,
                  transformResponse: function (value) {
                    return value; //no transformation
                  }
                }).then(function successCallback(response) {
                    vm.chartHtml = $sce.trustAsHtml(response.data);
                    // when the response is available
                  });

        }


            }
})();
