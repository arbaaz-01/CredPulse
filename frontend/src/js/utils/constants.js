define([], function () {

    'use strict';


    // =========================================================
    // API
    // =========================================================

    const API_BASE_URL =
        'http://localhost:8080/api/v1';


    // =========================================================
    // APPLICATION ROLES
    // =========================================================

    const ROLES = {

        USER:
            'USER',

        ADMIN:
            'ADMIN',

        MANAGER:
            'MANAGER',

        CREDIT_OFFICER:
            'CREDIT_OFFICER'

    };


    // =========================================================
    // API ENDPOINTS
    // =========================================================

    const ENDPOINTS = {


        // -----------------------------------------------------
        // AUTHENTICATION
        // -----------------------------------------------------

        AUTH: {

            LOGIN:
                '/auth/login',

            REGISTER:
                '/auth/register',

            REFRESH:
                '/auth/refresh',

            LOGOUT:
                '/auth/logout'

        },


        // -----------------------------------------------------
        // USERS
        // -----------------------------------------------------

        USERS: {

            ME:
                '/users/me'

        },

        ADMIN: {
            USERS: '/admin/users'
        },


        // -----------------------------------------------------
        // CARDS
        // -----------------------------------------------------

        CARDS: {

            ALL:
                '/cards',


            BY_ID: function (id) {

                return '/cards/' + id;
            },


            STATUS: function (id) {

                return (
                    '/cards/' +
                    id +
                    '/status'
                );
            }

        }

        ,
        ISSUED_CARDS: {
            ALL: '/issued-cards',
            BY_ID: function (id) { return '/issued-cards/' + id; }
        },
        CREDIT_OFFICER: {
            APPLICATIONS: '/credit-officer/applications',
            BY_ID: function (id) { return '/credit-officer/applications/' + id; },
            APPROVE: function (id) { return '/credit-officer/applications/' + id + '/approve'; },
            REJECT: function (id) { return '/credit-officer/applications/' + id + '/reject'; }
        }

        ,
        APPLICATIONS: {
            ALL: '/card-applications',
            BY_ID: function (id) { return '/card-applications/' + id; },
            SUBMIT: function (id) { return '/card-applications/' + id + '/submit'; }
        }

    };


    // =========================================================
    // EXPORT
    // =========================================================

    return {

        API_BASE_URL:
            API_BASE_URL,

        ROLES:
            ROLES,

        ENDPOINTS:
            ENDPOINTS

    };

});
