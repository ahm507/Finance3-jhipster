(function() {
    'use strict';

    angular
        .module('financeApp')
        .factory('TransactionSearch', TransactionSearch);

    TransactionSearch.$inject = ['$resource'];

    function TransactionSearch($resource) {
        var resourceUrl =  'api/_search/transactions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
