'use strict';

describe('Controller Tests', function() {

    describe('UserAccount Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockUserAccount, MockUser, MockCurrency;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockUserAccount = jasmine.createSpy('MockUserAccount');
            MockUser = jasmine.createSpy('MockUser');
            MockCurrency = jasmine.createSpy('MockCurrency');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'UserAccount': MockUserAccount,
                'User': MockUser,
                'Currency': MockCurrency
            };
            createController = function() {
                $injector.get('$controller')("UserAccountDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'financeApp:userAccountUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
