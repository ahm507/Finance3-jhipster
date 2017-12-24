(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('ChartsController', ChartsController);

    ChartsController.$inject = ['Charts', 'ChartsSearch'];

    function ChartsController(Charts, ChartsSearch) {

        var vm = this;

        vm.charts = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Charts.query(function(result) {
                vm.charts = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ChartsSearch.query({query: vm.searchQuery}, function(result) {
                vm.charts = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
