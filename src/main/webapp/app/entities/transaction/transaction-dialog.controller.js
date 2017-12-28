(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('TransactionDialogController', TransactionDialogController);

    TransactionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Transaction', 'User', 'UserAccount'];

    function TransactionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Transaction, User, UserAccount) {
        var vm = this;

        vm.transaction = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.useraccounts = UserAccount.queryAsList();

        //initiate date control to today now.
        if( vm.transaction.date == null) {
            vm.transaction.date = new Date();
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {

            //validate amount
//            if(vm.transaction.amount < 0) {
//                alert("Invalid amount");
//                return;
//            }
//            //validate currencies are the same
//            var depositAccount  = vm.useraccounts.filter(item => item.id == vm.transaction.depositAccountId);
//            var withdrawAccount = vm.useraccounts.filter(item => item.id == vm.transaction.withdrawAccountId);
//            if(depositAccount[0].currencyId != withdrawAccount[0].currencyId) {
//                alert("Transaction is not allowed to be cross currencies");
//                return;
//            }

            vm.isSaving = true;

            if (vm.transaction.id !== null) {
                Transaction.update(vm.transaction, onSaveSuccess, onSaveError);
            } else {
                Transaction.save(vm.transaction, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('financeApp:transactionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
