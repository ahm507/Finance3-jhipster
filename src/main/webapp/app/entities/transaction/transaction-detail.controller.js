(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('TransactionDetailController', TransactionDetailController);

    TransactionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Transaction', 'User', 'UserAccount'];

    function TransactionDetailController($scope, $rootScope, $stateParams, previousState, entity, Transaction, User, UserAccount) {
        var vm = this;

        vm.transaction = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('financeApp:transactionUpdate', function(event, result) {
            vm.transaction = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
