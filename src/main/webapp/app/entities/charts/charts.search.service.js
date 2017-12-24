(function() {
    'use strict';

    angular
        .module('financeApp')
        .factory('ChartsSearch', ChartsSearch);

    ChartsSearch.$inject = ['$resource'];

    function ChartsSearch($resource) {
        var resourceUrl =  'api/_search/charts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
