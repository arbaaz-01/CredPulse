define([
    './apiService',
    '../utils/storage',
    '../utils/constants'
], function (
    apiService,
    storage,
    constants
) {

    'use strict';


    // =========================================================
    // AUTHENTICATION SERVICE
    // =========================================================

    return {


        // =====================================================
        // REGISTER
        // =====================================================

        register: function (userData) {

            return apiService.post(
                constants.ENDPOINTS.AUTH.REGISTER,
                userData,
                {
                    // Register is a public endpoint.
                    // Do not send an access token.
                    auth: false
                }
            );
        },


        // =====================================================
        // LOGIN
        // =====================================================

        login: async function (credentials) {

            const response =
                await apiService.post(
                    constants.ENDPOINTS.AUTH.LOGIN,
                    credentials,
                    {
                        // Login is a public endpoint.
                        auth: false
                    }
                );


            // -------------------------------------------------
            // Store JWT tokens
            // -------------------------------------------------

            storage.setTokens(
                response.accessToken,
                response.refreshToken
            );


            // -------------------------------------------------
            // Store basic logged-in user information
            // -------------------------------------------------

            storage.setUser({

                userId: response.userId,
                name: response.name,
                email: response.email,
                role: response.role

            });


            return response;
        },


        // =====================================================
        // REFRESH ACCESS TOKEN
        // =====================================================

        refresh: async function () {

            const refreshToken =
                storage.getRefreshToken();


            if (!refreshToken) {

                throw new Error(
                    'No refresh token available'
                );
            }


            const response =
                await apiService.post(
                    constants.ENDPOINTS.AUTH.REFRESH,
                    {
                        refreshToken: refreshToken
                    },
                    {
                        /*
                         * Refresh must not send the expired
                         * access token.
                         */
                        auth: false
                    }
                );


            // Backend rotates the refresh token,
            // so save BOTH new tokens.
            storage.setTokens(
                response.accessToken,
                response.refreshToken
            );


            // LoginResponse also contains updated
            // user information.
            storage.setUser({

                userId: response.userId,
                name: response.name,
                email: response.email,
                role: response.role

            });


            return response;
        },


        // =====================================================
        // LOGOUT
        // =====================================================

        logout: async function () {

            const refreshToken =
                storage.getRefreshToken();


            try {

                if (refreshToken) {

                    await apiService.post(
                        constants.ENDPOINTS.AUTH.LOGOUT,
                        {
                            refreshToken: refreshToken
                        },
                        {
                            /*
                             * Your backend logout endpoint is
                             * permitAll(), and logout only needs
                             * the refresh token.
                             */
                            auth: false
                        }
                    );
                }

            } finally {

                /*
                 * Clear frontend authentication information
                 * even if the backend request fails.
                 */
                storage.clear();
            }
        },


        // =====================================================
        // LOGIN STATUS
        // =====================================================

        isLoggedIn: function () {

            return storage.isLoggedIn();
        },


        // =====================================================
        // CURRENT STORED USER
        // =====================================================

        getCurrentUser: function () {

            return storage.getUser();
        }

    };

});