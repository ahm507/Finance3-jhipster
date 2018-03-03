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


        //Get default selections
        if (vm.transaction.id === null) { //New Transaction and not ediing an existing one
            vm.transaction.withdrawAccountId = parseInt(localStorage.getItem("selected_withdrawAccountId"));
            vm.transaction.depositAccountId = parseInt(localStorage.getItem("selected_depositAccountId"));
        } else { //Edit Transaction
            //Store selection
            storeDefaults();
        }

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
            //Store selection
            storeDefaults();
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

        function storeDefaults() {
            localStorage.setItem("selected_withdrawAccountId", vm.transaction.withdrawAccountId);
            localStorage.setItem("selected_depositAccountId", vm.transaction.depositAccountId);
        }
    }
})();
