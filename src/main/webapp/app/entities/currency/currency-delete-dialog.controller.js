(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('CurrencyDeleteController',CurrencyDeleteController);

    CurrencyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Currency'];

    function CurrencyDeleteController($uibModalInstance, entity, Currency) {
        var vm = this;

        vm.currency = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Currency.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
