(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('ChartsDeleteController',ChartsDeleteController);

    ChartsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Charts'];

    function ChartsDeleteController($uibModalInstance, entity, Charts) {
        var vm = this;

        vm.charts = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Charts.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
