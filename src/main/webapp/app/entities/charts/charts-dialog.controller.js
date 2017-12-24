(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('ChartsDialogController', ChartsDialogController);

    ChartsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Charts'];

    function ChartsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Charts) {
        var vm = this;

        vm.charts = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.charts.id !== null) {
                Charts.update(vm.charts, onSaveSuccess, onSaveError);
            } else {
                Charts.save(vm.charts, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('financeApp:chartsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
