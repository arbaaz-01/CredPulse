define(['./apiService', '../utils/constants'], function (apiService, constants) {
    'use strict';
    return {
        createDraft: function (data) { return apiService.post(constants.ENDPOINTS.APPLICATIONS.ALL, data); },
        getMyApplications: function () { return apiService.get(constants.ENDPOINTS.APPLICATIONS.ALL); },
        getApplication: function (id) { return apiService.get(constants.ENDPOINTS.APPLICATIONS.BY_ID(id)); },
        updateApplication: function (id, data) { return apiService.put(constants.ENDPOINTS.APPLICATIONS.BY_ID(id), data); },
        submitApplication: function (id) { return apiService.post(constants.ENDPOINTS.APPLICATIONS.SUBMIT(id)); }
    };
});
