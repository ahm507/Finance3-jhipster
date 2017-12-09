(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('CurrencyDetailController', CurrencyDetailController);

    CurrencyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Currency', 'User'];

    function CurrencyDetailController($scope, $rootScope, $stateParams, previousState, entity, Currency, User) {
        var vm = this;

        vm.currency = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('financeApp:currencyUpdate', function(event, result) {
            vm.currency = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
