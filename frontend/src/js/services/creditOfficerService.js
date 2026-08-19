define(['./apiService', '../utils/constants'], function (apiService, constants) {
    'use strict';
    return {
        getApplications: function () { return apiService.get(constants.ENDPOINTS.CREDIT_OFFICER.APPLICATIONS); },
        getApplication: function (id) { return apiService.get(constants.ENDPOINTS.CREDIT_OFFICER.BY_ID(id)); },
        getApplicationDocuments: function (id) { return apiService.get(constants.ENDPOINTS.CREDIT_OFFICER.BY_ID(id) + '/documents'); },
        approve: function (id) { return apiService.post(constants.ENDPOINTS.CREDIT_OFFICER.APPROVE(id)); },
        reject: function (id) { return apiService.post(constants.ENDPOINTS.CREDIT_OFFICER.REJECT(id)); }
    };
});
