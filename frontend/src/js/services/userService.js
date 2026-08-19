define([
    './apiService',
    '../utils/constants'
], function (
    apiService,
    constants
) {

    'use strict';


    return {

        // =====================================================
        // GET CURRENT LOGGED-IN USER
        // =====================================================
        //
        // Backend:
        //
        // GET /api/v1/users/me
        //
        // apiService automatically adds:
        //
        // Authorization: Bearer <accessToken>
        //
        // =====================================================

        getCurrentUser: function () {

            return apiService.get(
                constants.ENDPOINTS.USERS.ME
            );
        }

    };

});