(function() {
    'use strict';
    angular
        .module('financeApp')
        .factory('Charts', Charts);

    Charts.$inject = ['$resource'];

    function Charts ($resource) {
        var resourceUrl =  'api/charts/:id';

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
