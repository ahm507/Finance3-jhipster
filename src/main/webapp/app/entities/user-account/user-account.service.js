(function() {
    'use strict';
    angular
        .module('financeApp')
        .factory('UserAccount', UserAccount);

    UserAccount.$inject = ['$resource'];

    function formatElement(element, count) {
        var spaces = ". . . . . . .";
        element.path = (count<1?element.type  + ': ' : spaces) + element.text + '(' + element.currencyName + ')';
    }

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
                        var assetsCount = 0;
                        var expensesCount = 0;
                        var incomeCount = 0;
                        var liabilityCount = 0;
                        var otherCount = 0;
                        data.forEach(function (element) {
                            if(element.type == "ASSET"){
//                                element.path = (assetsCount<1?element.type + ': ':spaces)  + element.text + '(' + element.currencyName + ')';
                                formatElement(element, assetsCount);
                                assetsCount++;
                            } else if(element.type == "EXPENSE") {
                                //element.path = (expensesCount<1?element.type:spaces) + ': ' + element.text + '(' + element.currencyName + ')';
                                formatElement(element, expensesCount);
                                expensesCount++;
                            } else if(element.type == "INCOME") {
//                                  element.path = (expensesCount<1?element.type:spaces) + ': ' + element.text + '(' + element.currencyName + ')';
                                    formatElement(element, incomeCount);
                                  incomeCount++;
                            } else if(element.type == "LIABILITY") {
//                                  element.path = (liabilityCount<1?element.type:spaces) + ': ' + element.text + '(' + element.currencyName + ')';
                                    formatElement(element, liabilityCount);
                                  liabilityCount++;
                            } else if(element.type == "OTHER") {
                                  //element.path = (otherCount<1?element.type:spaces) + ': ' + element.text + '(' + element.currencyName + ')';
                                  formatElement(element, otherCount);
                                  otherCount++;
                              }

                        });


//                       data.sort(function(a, b) {
//                             var nameA=a.path.toLowerCase(), nameB=b.path.toLowerCase()
//                                if (nameA < nameB) //sort string ascending
//                                    return -1
//                                if (nameA > nameB)
//                                    return 1
//                                return 0 //default return value (no sorting)
//                           });


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
