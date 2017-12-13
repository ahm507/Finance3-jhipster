(function() {
    'use strict';

    angular
        .module('financeApp')
        .controller('TransactionController', TransactionController);

    TransactionController.$inject = ['Transaction', 'TransactionSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'UserAccount'];

    function TransactionController(Transaction, TransactionSearch, ParseLinks, AlertService, paginationConstants, UserAccount) {

        var vm = this;

        vm.transactions = [];
        vm.loadPage = loadPage;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;

        vm.selectedUserAccountId = parseInt(localStorage.getItem("selectedUserAccountId"));
        vm.yearSelected          = localStorage.getItem("yearSelected");

        vm.changeUserAccount = function() {
            localStorage.setItem("selectedUserAccountId", vm.selectedUserAccountId);
            loadAll();
        };

        //Load year list
        vm.changedYear = function() {
            localStorage.setItem("yearSelected", vm.yearSelected);
            loadAll();
        }

        //Load User Accounts and decorate a new "path" property
        vm.useraccounts = UserAccount.query({}, onSuccess);
        function onSuccess(data) {
            data.forEach(function (element) {
                element.path = element.type + ' > ' + element.text + ' - ' + element.currencyName;
            });
        }

        //Load list of years in transactions
        vm.yearList = Transaction.queryYears({});

        loadAll();

        function loadAll () {
            

            if (vm.currentSearch) {
                TransactionSearch.query({
                    query: vm.currentSearch,
                    page: vm.page,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                var params = {
                    page: vm.page,
                    size: vm.itemsPerPage,
                    sort: sort()};
                if(vm.selectedUserAccountId != undefined) {
                    params.userAccountId = vm.selectedUserAccountId;
                } else { //Do not try to show all transactions - WRONG Semantics
                    vm.transactions = []; //empty the array
                    return;
                }
                if(vm.yearSelected != undefined && vm.yearSelected != ' ') {
                    params.year = vm.yearSelected;
                }
                Transaction.query(params, onSuccess, onError);
            
                function sort() {
                    var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                    if (vm.predicate !== 'id') {
                        result.push('id');
                    }
                    return result;
                }

                function onSuccess(data, headers) {
                    vm.links = ParseLinks.parse(headers('link'));
                    vm.totalItems = headers('X-Total-Count');
                    vm.transactions = []; //empty the array
                    for (var i = 0; i < data.length; i++) {
                        vm.transactions.push(data[i]);
                    }
                }

                function onError(error) {
                    AlertService.error(error.data.message);
                }
            }
        } //loadAll()

        function reset () {
            vm.page = 0;
            vm.transactions = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }

        function clear () {
            vm.transactions = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.searchQuery = null;
            vm.currentSearch = null;
            vm.loadAll();
        }

        function search (searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.transactions = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.loadAll();
        }
    }
})();
