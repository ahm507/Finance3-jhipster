(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('UserSettingsDeleteController',UserSettingsDeleteController);

    UserSettingsDeleteController.$inject = ['$uibModalInstance', 'entity', 'UserSettings'];

    function UserSettingsDeleteController($uibModalInstance, entity, UserSettings) {
        var vm = this;

        vm.userSettings = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            UserSettings.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
