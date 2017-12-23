(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('UserSettingsDialogController', UserSettingsDialogController);

    UserSettingsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'UserSettings'];

    function UserSettingsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, UserSettings) {
        var vm = this;

        vm.userSettings = entity;
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
            if (vm.userSettings.id !== null) {
                UserSettings.update(vm.userSettings, onSaveSuccess, onSaveError);
            } else {
                UserSettings.save(vm.userSettings, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('financeApp:userSettingsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
