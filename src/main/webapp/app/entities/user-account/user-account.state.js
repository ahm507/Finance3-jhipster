(function() {
    'use strict';

    angular
        .module('financeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-account', {
            parent: 'entity',
            url: '/user-account',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'financeApp.userAccount.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-account/user-accounts.html',
                    controller: 'UserAccountController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userAccount');
                    $translatePartialLoader.addPart('accountType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-account-detail', {
            parent: 'user-account',
            url: '/user-account/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'financeApp.userAccount.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-account/user-account-detail.html',
                    controller: 'UserAccountDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userAccount');
                    $translatePartialLoader.addPart('accountType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserAccount', function($stateParams, UserAccount) {
                    return UserAccount.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-account',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-account-detail.edit', {
            parent: 'user-account-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-account/user-account-dialog.html',
                    controller: 'UserAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserAccount', function(UserAccount) {
                            return UserAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-account.new', {
            parent: 'user-account',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-account/user-account-dialog.html',
                    controller: 'UserAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                text: null,
                                description: null,
                                type: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-account', null, { reload: 'user-account' });
                }, function() {
                    $state.go('user-account');
                });
            }]
        })
        .state('user-account.edit', {
            parent: 'user-account',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-account/user-account-dialog.html',
                    controller: 'UserAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserAccount', function(UserAccount) {
                            return UserAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-account', null, { reload: 'user-account' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-account.delete', {
            parent: 'user-account',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-account/user-account-delete-dialog.html',
                    controller: 'UserAccountDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserAccount', function(UserAccount) {
                            return UserAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-account', null, { reload: 'user-account' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
