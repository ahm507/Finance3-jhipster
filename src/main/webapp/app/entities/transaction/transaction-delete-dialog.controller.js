(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('TransactionDeleteController',TransactionDeleteController);

    TransactionDeleteController.$inject = ['$uibModalInstance', 'entity', 'Transaction'];

    function TransactionDeleteController($uibModalInstance, entity, Transaction) {
        var vm = this;

        vm.transaction = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Transaction.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
