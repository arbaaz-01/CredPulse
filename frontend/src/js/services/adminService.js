define(['./apiService', '../utils/constants'], function (apiService, constants) {
    'use strict';
    return {
        getStaffUsers: function () { return apiService.get(constants.ENDPOINTS.ADMIN.USERS); },
        createStaffUser: function (data) { return apiService.post(constants.ENDPOINTS.ADMIN.USERS, data); }
    };
});
