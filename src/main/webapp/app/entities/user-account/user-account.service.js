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
            'queryAsList': { //All items with no paging
                method: 'GET', 
                url:'api/user-accountsAsList', 
                isArray: true,
                transformResponse: function(data) {
                    if(data) {
                        data = angular.fromJson(data);
                        data.forEach(function (element) {
                            element.path = element.type + ': ' + element.text + '(' + element.currencyName + ')';
                       });           
                    }
                    return data;
                }
            },
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
