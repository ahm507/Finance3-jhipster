(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('UserAccountDetailController', UserAccountDetailController);

    UserAccountDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserAccount', 'User', 'Currency'];

    function UserAccountDetailController($scope, $rootScope, $stateParams, previousState, entity, UserAccount, User, Currency) {
        var vm = this;

        vm.userAccount = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('financeApp:userAccountUpdate', function(event, result) {
            vm.userAccount = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
