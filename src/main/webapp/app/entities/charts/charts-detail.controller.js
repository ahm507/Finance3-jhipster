(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('ChartsDetailController', ChartsDetailController);

    ChartsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Charts'];

    function ChartsDetailController($scope, $rootScope, $stateParams, previousState, entity, Charts) {
        var vm = this;

        vm.charts = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('financeApp:chartsUpdate', function(event, result) {
            vm.charts = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
