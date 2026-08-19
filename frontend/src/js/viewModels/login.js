define([
    'knockout',
    '../services/authService',
    '../utils/validators',
    '../utils/constants',
    '../utils/errorMessages',
    'ojs/ojrouter'
], function (
    ko,
    authService,
    validators,
    constants,
    errorMessages,
    Router
) {

    'use strict';


    function LoginViewModel() {

        const self = this;


        // =====================================================
        // FORM FIELDS
        // =====================================================

        self.email =
            ko.observable('');

        self.password =
            ko.observable('');


        // =====================================================
        // UI STATE
        // =====================================================

        self.errorMessage =
            ko.observable('');

        self.successMessage =
            ko.observable('');

        self.isLoading =
            ko.observable(false);


        // =====================================================
        // ROUTE USER BY ROLE
        // =====================================================

        self.routeByRole =
            async function (role) {

                switch (role) {


                    // =========================================
                    // CUSTOMER
                    // =========================================

                    case constants.ROLES.USER:

                        await Router
                            .rootInstance
                            .go('customerDashboard');

                        return;


                    // =========================================
                    // ADMIN
                    // =========================================

                    case constants.ROLES.ADMIN:

                        await Router
                            .rootInstance
                            .go('adminDashboard');

                        return;


                    // =========================================
                    // MANAGER
                    // =========================================
                    //
                    // Manager dashboard will be added later.
                    // Do not send Manager to Customer/Admin.
                    // =========================================

                    case constants.ROLES.MANAGER:
                        await Router.rootInstance.go('managerDashboard');
                        return;


                    // =========================================
                    // CREDIT OFFICER
                    // =========================================
                    //
                    // Credit Officer dashboard will be added
                    // later.
                    // =========================================

                    case constants.ROLES.CREDIT_OFFICER:
                        await Router.rootInstance.go('creditOfficerDashboard');
                        return;


                    // =========================================
                    // UNKNOWN ROLE
                    // =========================================

                    default:

                        self.errorMessage(
                            'Your account role is not recognized.'
                        );

                        console.error(
                            'Unknown user role:',
                            role
                        );
                }
            };


        // =====================================================
        // LOGIN
        // =====================================================

        self.login =
            async function () {

                // Prevent duplicate submissions
                if (self.isLoading()) {
                    return;
                }


                self.errorMessage('');
                self.successMessage('');


                // =================================================
                // NORMALIZE VALUES
                // =================================================

                const email =
                    self.email()
                        ? self
                            .email()
                            .trim()
                            .toLowerCase()
                        : '';


                const password =
                    self.password() || '';


                // =================================================
                // EMAIL VALIDATION
                // =================================================

                if (
                    !validators.isRequired(
                        email
                    )
                ) {

                    self.errorMessage(
                        'Email is required'
                    );

                    return;
                }


                if (
                    !validators.isEmail(
                        email
                    )
                ) {

                    self.errorMessage(
                        'Invalid email format'
                    );

                    return;
                }


                // =================================================
                // PASSWORD VALIDATION
                // =================================================

                if (
                    !validators.isRequired(
                        password
                    )
                ) {

                    self.errorMessage(
                        'Password is required'
                    );

                    return;
                }


                // =================================================
                // CALL BACKEND
                // =================================================

                self.isLoading(true);


                try {

                    /*
                     * Backend:
                     *
                     * POST /api/v1/auth/login
                     *
                     * Expected response includes:
                     *
                     * accessToken
                     * refreshToken
                     * userId
                     * name
                     * email
                     * role
                     */

                    const response =
                        await authService.login({

                            email:
                                email,

                            password:
                                password

                        });


                    // =============================================
                    // VALIDATE LOGIN RESPONSE
                    // =============================================

                    if (
                        !response ||
                        !response.role
                    ) {

                        self.errorMessage(
                            'Login succeeded, but no account role was returned.'
                        );

                        console.error(
                            'Login response does not contain a role:',
                            response
                        );

                        return;
                    }


                    // =============================================
                    // LOGIN SUCCESS
                    // =============================================

                    self.successMessage(
                        'Login successful'
                    );


                    // =============================================
                    // ROLE-BASED ROUTING
                    // =============================================

                    try {

                        await self.routeByRole(
                            response.role
                        );

                    } catch (routerError) {

                        console.error(
                            'Role-based navigation failed:',
                            routerError
                        );


                        self.errorMessage(
                            'Login succeeded, but your dashboard could not be opened.'
                        );
                    }


                } catch (error) {

                    console.error(
                        'Login failed:',
                        error
                    );


                    self.errorMessage(errorMessages.forLogin(error));


                } finally {

                    self.isLoading(false);
                }
            };

    }


    return LoginViewModel;

});
