(function() {
    'use strict';

    angular
        .module('financeApp')
        .factory('UserAccountSearch', UserAccountSearch);

    UserAccountSearch.$inject = ['$resource'];

    function UserAccountSearch($resource) {
        var resourceUrl =  'api/_search/user-accounts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
