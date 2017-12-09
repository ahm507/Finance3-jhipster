(function() {
    'use strict';

    angular
        .module('financeApp')
        .factory('CurrencySearch', CurrencySearch);

    CurrencySearch.$inject = ['$resource'];

    function CurrencySearch($resource) {
        var resourceUrl =  'api/_search/currencies/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
