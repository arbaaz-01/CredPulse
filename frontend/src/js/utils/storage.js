define([], function () {

    'use strict';


    // =========================================================
    // LOCAL STORAGE KEYS
    // =========================================================

    const ACCESS_TOKEN_KEY =
        'credpulse_access_token';

    const REFRESH_TOKEN_KEY =
        'credpulse_refresh_token';

    const USER_KEY =
        'credpulse_user';


    return {


        // =====================================================
        // TOKENS
        // =====================================================

        setTokens: function (
            accessToken,
            refreshToken
        ) {

            if (accessToken) {

                localStorage.setItem(
                    ACCESS_TOKEN_KEY,
                    accessToken
                );

            } else {

                localStorage.removeItem(
                    ACCESS_TOKEN_KEY
                );
            }


            if (refreshToken) {

                localStorage.setItem(
                    REFRESH_TOKEN_KEY,
                    refreshToken
                );

            } else {

                localStorage.removeItem(
                    REFRESH_TOKEN_KEY
                );
            }
        },


        getAccessToken: function () {

            return localStorage.getItem(
                ACCESS_TOKEN_KEY
            );
        },


        getRefreshToken: function () {

            return localStorage.getItem(
                REFRESH_TOKEN_KEY
            );
        },


        // =====================================================
        // USER
        // =====================================================

        setUser: function (user) {

            if (!user) {

                localStorage.removeItem(
                    USER_KEY
                );

                return;
            }


            localStorage.setItem(
                USER_KEY,
                JSON.stringify(user)
            );
        },


        getUser: function () {

            const storedUser =
                localStorage.getItem(
                    USER_KEY
                );


            if (!storedUser) {
                return null;
            }


            try {

                return JSON.parse(
                    storedUser
                );

            } catch (error) {

                console.error(
                    'Unable to parse stored user:',
                    error
                );


                /*
                 * Invalid/corrupted data should not remain
                 * in localStorage.
                 */
                localStorage.removeItem(
                    USER_KEY
                );

                return null;
            }
        },


        // =====================================================
        // AUTHENTICATION STATUS
        // =====================================================

        isLoggedIn: function () {

            return !!localStorage.getItem(
                ACCESS_TOKEN_KEY
            );
        },


        // =====================================================
        // CLEAR AUTHENTICATION DATA
        // =====================================================

        clear: function () {

            localStorage.removeItem(
                ACCESS_TOKEN_KEY
            );

            localStorage.removeItem(
                REFRESH_TOKEN_KEY
            );

            localStorage.removeItem(
                USER_KEY
            );
        }

    };

});