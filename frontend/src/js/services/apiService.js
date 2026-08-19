define([
    '../utils/constants',
    '../utils/storage'
], function (
    constants,
    storage
) {

    'use strict';


    // =========================================================
    // REFRESH STATE
    // =========================================================
    //
    // The dashboard may make multiple API requests at the
    // same time.
    //
    // Example:
    //
    // GET /users/me
    // GET /cards
    //
    // If the access token expires, both requests may receive
    // 401 at almost exactly the same time.
    //
    // Because the backend ROTATES refresh tokens, we must make
    // sure only ONE refresh request is sent.
    // =========================================================

    let refreshPromise = null;


    // =========================================================
    // READ RESPONSE BODY
    // =========================================================

    async function readResponse(response) {

        if (response.status === 204) {
            return null;
        }


        const contentType =
            response.headers.get(
                'content-type'
            ) || '';


        if (
            contentType.includes(
                'application/json'
            )
        ) {

            return await response.json();
        }


        const text =
            await response.text();


        if (!text) {
            return null;
        }


        return {
            message: text
        };
    }


    // =========================================================
    // REFRESH ACCESS TOKEN
    // =========================================================

    async function refreshAccessToken() {

        /*
         * If another request is already refreshing the token,
         * reuse that same Promise.
         */
        if (refreshPromise) {
            return refreshPromise;
        }


        const refreshToken =
            storage.getRefreshToken();


        // -----------------------------------------------------
        // No refresh token
        // -----------------------------------------------------

        if (!refreshToken) {

            storage.clear();

            throw {
                status: 401,
                data: {
                    message:
                        'Your session has expired. Please log in again.'
                }
            };
        }


        // -----------------------------------------------------
        // Start one shared refresh operation
        // -----------------------------------------------------

        refreshPromise =
            (async function () {

                try {

                    const url =
                        constants.API_BASE_URL +
                        constants.ENDPOINTS.AUTH.REFRESH;


                    const response =
                        await fetch(
                            url,
                            {
                                method: 'POST',

                                headers: {
                                    'Content-Type':
                                        'application/json'
                                },

                                body: JSON.stringify({
                                    refreshToken:
                                        refreshToken
                                })
                            }
                        );


                    const data =
                        await readResponse(
                            response
                        );


                    // -----------------------------------------
                    // Refresh failed
                    // -----------------------------------------

                    if (!response.ok) {

                        const error = {
                            status:
                                response.status,

                            data:
                                data
                        };


                        /*
                         * Invalid / expired refresh token.
                         *
                         * Clear local authentication state.
                         */
                        if (
                            response.status === 400 ||
                            response.status === 401 ||
                            response.status === 403
                        ) {

                            storage.clear();
                        }


                        throw error;
                    }


                    // -----------------------------------------
                    // Validate refresh response
                    // -----------------------------------------

                    if (
                        !data ||
                        !data.accessToken ||
                        !data.refreshToken
                    ) {

                        storage.clear();

                        throw {
                            status: 401,

                            data: {
                                message:
                                    'Invalid token refresh response.'
                            }
                        };
                    }


                    // -----------------------------------------
                    // Backend rotates refresh tokens.
                    //
                    // Therefore BOTH tokens must be replaced.
                    // -----------------------------------------

                    storage.setTokens(
                        data.accessToken,
                        data.refreshToken
                    );


                    // -----------------------------------------
                    // Keep stored user information synchronized
                    // -----------------------------------------

                    if (data.userId) {

                        storage.setUser({

                            userId:
                                data.userId,

                            name:
                                data.name,

                            email:
                                data.email,

                            role:
                                data.role

                        });
                    }


                    return data.accessToken;


                } finally {

                    /*
                     * Allow a future refresh operation after
                     * this one completes or fails.
                     */
                    refreshPromise = null;
                }

            })();


        return refreshPromise;
    }


    // =========================================================
    // GENERIC API REQUEST
    // =========================================================

    async function request(
        endpoint,
        options
    ) {

        options =
            options || {};


        const url =
            constants.API_BASE_URL +
            endpoint;


        // =====================================================
        // HEADERS
        // =====================================================

        const headers = Object.assign({}, options.headers || {});

        if (!(options.body instanceof FormData) && !headers['Content-Type']) {
            headers['Content-Type'] = 'application/json';
        }


        // =====================================================
        // AUTHENTICATION
        // =====================================================
        //
        // auth: false
        //
        // Public request:
        //
        // login
        // register
        // refresh
        // logout
        //
        // Otherwise attach the latest access token.
        // =====================================================

        if (options.auth !== false) {

            const accessToken =
                storage.getAccessToken();


            if (accessToken) {

                /*
                 * Always use the CURRENT token.
                 *
                 * This is important when retrying after
                 * a successful refresh.
                 */
                headers.Authorization =
                    'Bearer ' +
                    accessToken;
            }
        }


        // =====================================================
        // FETCH CONFIGURATION
        // =====================================================

        const config = {

            method:
                options.method ||
                'GET',

            headers:
                headers
        };


        if (
            options.body !== undefined
        ) {

            config.body = options.body instanceof FormData
                ? options.body
                : JSON.stringify(options.body);
        }


        // =====================================================
        // EXECUTE REQUEST
        // =====================================================

        let response;


        try {

            response =
                await fetch(
                    url,
                    config
                );

        } catch (networkError) {

            /*
             * fetch() itself failed.
             *
             * Possible reasons:
             *
             * - Spring Boot is down
             * - Network disconnected
             * - CORS failure
             */

            throw networkError;
        }


        const data =
            await readResponse(
                response
            );


        // =====================================================
        // SUCCESS
        // =====================================================

        if (response.ok) {

            return data;
        }


        // =====================================================
        // ACCESS TOKEN EXPIRED / INVALID
        // =====================================================
        //
        // Conditions:
        //
        // 1. Backend returned 401
        // 2. Request requires authentication
        // 3. We have not already retried this request
        //
        // =====================================================

        if (
            response.status === 401 &&
            options.auth !== false &&
            options._retried !== true
        ) {

            try {

                // ---------------------------------------------
                // Get fresh tokens
                // ---------------------------------------------

                await refreshAccessToken();


                // ---------------------------------------------
                // Retry ORIGINAL request exactly once
                // ---------------------------------------------

                const retryOptions =
                    Object.assign(
                        {},
                        options,
                        {
                            _retried: true
                        }
                    );


                return await request(
                    endpoint,
                    retryOptions
                );


            } catch (refreshError) {

                /*
                 * Refresh token may itself be invalid,
                 * expired or revoked.
                 */

                throw refreshError;
            }
        }


        // =====================================================
        // NORMAL BACKEND ERROR
        // =====================================================

        throw {

            status:
                response.status,

            data:
                data
        };
    }


    // =========================================================
    // PUBLIC API METHODS
    // =========================================================

    return {


        // =====================================================
        // GET
        // =====================================================

        get: function (
            endpoint,
            options
        ) {

            const requestOptions =
                Object.assign(
                    {},
                    options || {},
                    {
                        method: 'GET'
                    }
                );


            return request(
                endpoint,
                requestOptions
            );
        },


        // =====================================================
        // POST
        // =====================================================

        post: function (
            endpoint,
            body,
            options
        ) {

            const requestOptions =
                Object.assign(
                    {},
                    options || {},
                    {
                        method:
                            'POST',

                        body:
                            body
                    }
                );


            return request(
                endpoint,
                requestOptions
            );
        },


        // =====================================================
        // PATCH
        // =====================================================

        patch: function (
            endpoint,
            body,
            options
        ) {

            const requestOptions =
                Object.assign(
                    {},
                    options || {},
                    {
                        method:
                            'PATCH',

                        body:
                            body
                    }
                );


            return request(
                endpoint,
                requestOptions
            );
        },

        postForm: function (endpoint, formData, options) {
            return request(endpoint, Object.assign({}, options || {}, {
                method: 'POST',
                body: formData
            }));
        },

        put: function (endpoint, body, options) {
            const requestOptions = Object.assign({}, options || {}, {
                method: 'PUT',
                body: body
            });
            return request(endpoint, requestOptions);
        },


        // =====================================================
        // DELETE
        // =====================================================

        delete: function (
            endpoint,
            options
        ) {

            const requestOptions =
                Object.assign(
                    {},
                    options || {},
                    {
                        method:
                            'DELETE'
                    }
                );


            return request(
                endpoint,
                requestOptions
            );
        }

    };

});
