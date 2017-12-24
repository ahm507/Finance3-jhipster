(function() {
    'use strict';

    angular
        .module('financeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('charts', {
            parent: 'entity',
            url: '/charts',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'financeApp.charts.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/charts/charts.html',
                    controller: 'ChartsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('charts');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('charts-detail', {
            parent: 'charts',
            url: '/charts/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'financeApp.charts.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/charts/charts-detail.html',
                    controller: 'ChartsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('charts');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Charts', function($stateParams, Charts) {
                    return Charts.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'charts',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('charts-detail.edit', {
            parent: 'charts-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/charts/charts-dialog.html',
                    controller: 'ChartsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Charts', function(Charts) {
                            return Charts.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('charts.new', {
            parent: 'charts',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/charts/charts-dialog.html',
                    controller: 'ChartsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('charts', null, { reload: 'charts' });
                }, function() {
                    $state.go('charts');
                });
            }]
        })
        .state('charts.edit', {
            parent: 'charts',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/charts/charts-dialog.html',
                    controller: 'ChartsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Charts', function(Charts) {
                            return Charts.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('charts', null, { reload: 'charts' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('charts.delete', {
            parent: 'charts',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/charts/charts-delete-dialog.html',
                    controller: 'ChartsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Charts', function(Charts) {
                            return Charts.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('charts', null, { reload: 'charts' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
