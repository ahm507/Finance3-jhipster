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

        vm.yearSelected = localStorage.getItem("charts_yearSelected");
        vm.accountTypeSelected = localStorage.getItem("charts_accountTypeSelected");
        if(vm.accountTypeSelected == "undefined") {
            vm.accountTypeSelected = "";
        }


        function changedSelection() {
            localStorage.setItem("charts_yearSelected", vm.yearSelected);
            localStorage.setItem("charts_accountTypeSelected", vm.accountTypeSelected);
            
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
