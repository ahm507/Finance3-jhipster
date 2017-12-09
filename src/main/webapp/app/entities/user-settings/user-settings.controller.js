(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('UserSettingsController', UserSettingsController);

    UserSettingsController.$inject = ['UserSettings', 'UserSettingsSearch'];

    function UserSettingsController(UserSettings, UserSettingsSearch) {

        var vm = this;

        vm.userSettings = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            UserSettings.query(function(result) {
                vm.userSettings = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            UserSettingsSearch.query({query: vm.searchQuery}, function(result) {
                vm.userSettings = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
