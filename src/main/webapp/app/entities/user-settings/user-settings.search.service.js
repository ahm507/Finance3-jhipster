(function() {
    'use strict';

    angular
        .module('financeApp')
        .factory('UserSettingsSearch', UserSettingsSearch);

    UserSettingsSearch.$inject = ['$resource'];

    function UserSettingsSearch($resource) {
        var resourceUrl =  'api/_search/user-settings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
