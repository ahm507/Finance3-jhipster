(function() {
    'use strict';
    angular
        .module('financeApp')
        .factory('UserAccount', UserAccount);

    UserAccount.$inject = ['$resource'];

    function UserAccount ($resource) {
        var resourceUrl =  'api/user-accounts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
