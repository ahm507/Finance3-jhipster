(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('CurrencyController', CurrencyController);

    CurrencyController.$inject = ['Currency', 'CurrencySearch'];

    function CurrencyController(Currency, CurrencySearch) {

        var vm = this;

        vm.currencies = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Currency.query(function(result) {
                vm.currencies = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CurrencySearch.query({query: vm.searchQuery}, function(result) {
                vm.currencies = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
