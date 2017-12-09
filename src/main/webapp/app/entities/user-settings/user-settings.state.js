(function() {
    'use strict';

    angular
        .module('financeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-settings', {
            parent: 'entity',
            url: '/user-settings',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'financeApp.userSettings.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-settings/user-settings.html',
                    controller: 'UserSettingsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userSettings');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-settings-detail', {
            parent: 'user-settings',
            url: '/user-settings/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'financeApp.userSettings.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-settings/user-settings-detail.html',
                    controller: 'UserSettingsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userSettings');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserSettings', function($stateParams, UserSettings) {
                    return UserSettings.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-settings',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-settings-detail.edit', {
            parent: 'user-settings-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-settings/user-settings-dialog.html',
                    controller: 'UserSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserSettings', function(UserSettings) {
                            return UserSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-settings.new', {
            parent: 'user-settings',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-settings/user-settings-dialog.html',
                    controller: 'UserSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                usdRate: null,
                                sarRate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-settings', null, { reload: 'user-settings' });
                }, function() {
                    $state.go('user-settings');
                });
            }]
        })
        .state('user-settings.edit', {
            parent: 'user-settings',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-settings/user-settings-dialog.html',
                    controller: 'UserSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserSettings', function(UserSettings) {
                            return UserSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-settings', null, { reload: 'user-settings' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-settings.delete', {
            parent: 'user-settings',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-settings/user-settings-delete-dialog.html',
                    controller: 'UserSettingsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserSettings', function(UserSettings) {
                            return UserSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-settings', null, { reload: 'user-settings' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
