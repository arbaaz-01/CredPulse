define(['./apiService', '../utils/constants'], function (apiService, constants) {
    'use strict';
    return {
        getProducts: function () { return apiService.get(constants.ENDPOINTS.CARDS.ALL); },
        getProduct: function (id) { return apiService.get(constants.ENDPOINTS.CARDS.BY_ID(id)); }
    };
});
