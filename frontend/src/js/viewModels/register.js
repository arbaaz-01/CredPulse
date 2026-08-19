define([
    'knockout',
    '../services/authService',
    '../utils/validators',
    '../utils/errorMessages',
    'ojs/ojrouter'
], function (
    ko,
    authService,
    validators,
    errorMessages,
    Router
) {

    'use strict';

    function RegisterViewModel() {

        const self = this;


        // =====================================================
        // FORM FIELDS
        // =====================================================

        self.name = ko.observable('');
        self.email = ko.observable('');
        self.mobile = ko.observable('');
        self.password = ko.observable('');
        self.confirmPassword = ko.observable('');


        // =====================================================
        // UI STATE
        // =====================================================

        self.errorMessage = ko.observable('');
        self.successMessage = ko.observable('');
        self.isLoading = ko.observable(false);


        // =====================================================
        // REGISTER
        // =====================================================

        self.register = async function () {

            // Prevent duplicate submissions
            if (self.isLoading()) {
                return;
            }

            self.errorMessage('');
            self.successMessage('');


            // =================================================
            // NORMALIZE FORM VALUES
            // =================================================

            const name = self.name()
                ? self.name().trim()
                : '';

            const email = self.email()
                ? self.email().trim().toLowerCase()
                : '';

            const mobile = self.mobile()
                ? self.mobile().trim()
                : '';

            const password =
                self.password() || '';

            const confirmPassword =
                self.confirmPassword() || '';


            // =================================================
            // NAME VALIDATION
            // =================================================

            if (!validators.isRequired(name)) {

                self.errorMessage(
                    'Name is required'
                );

                return;
            }


            if (
                name.length < 2 ||
                name.length > 100
            ) {

                self.errorMessage(
                    'Name must be between 2 and 100 characters'
                );

                return;
            }


            // =================================================
            // EMAIL VALIDATION
            // =================================================

            if (!validators.isRequired(email)) {

                self.errorMessage(
                    'Email is required'
                );

                return;
            }


            if (!validators.isEmail(email)) {

                self.errorMessage(
                    'Invalid email format'
                );

                return;
            }


            if (email.length > 150) {

                self.errorMessage(
                    'Email cannot exceed 150 characters'
                );

                return;
            }


            // =================================================
            // MOBILE VALIDATION
            // =================================================

            if (!validators.isRequired(mobile)) {

                self.errorMessage(
                    'Mobile number is required'
                );

                return;
            }


            if (!validators.isMobile(mobile)) {

                self.errorMessage(
                    'Mobile number must contain exactly 10 digits'
                );

                return;
            }


            // =================================================
            // PASSWORD VALIDATION
            // =================================================

            if (!validators.isRequired(password)) {

                self.errorMessage(
                    'Password is required'
                );

                return;
            }


            if (!validators.isPassword(password)) {

                self.errorMessage(
                    'Password must be between 8 and 100 characters'
                );

                return;
            }


            // =================================================
            // CONFIRM PASSWORD VALIDATION
            // =================================================

            if (!validators.isRequired(confirmPassword)) {

                self.errorMessage(
                    'Please confirm your password'
                );

                return;
            }


            if (password !== confirmPassword) {

                self.errorMessage(
                    'Passwords do not match'
                );

                return;
            }


            // =================================================
            // CALL SPRING BOOT BACKEND
            // =================================================

            self.isLoading(true);

            try {

                /*
                 * Backend RegisterRequest:
                 *
                 * {
                 *   name,
                 *   email,
                 *   mobile,
                 *   password
                 * }
                 *
                 * confirmPassword is frontend-only.
                 */

                await authService.register({

                    name: name,
                    email: email,
                    mobile: mobile,
                    password: password

                });


                // =================================================
                // REGISTRATION SUCCESS
                // =================================================

                self.successMessage(
                    'Registration successful. Redirecting to login...'
                );


                // Clear form
                self.name('');
                self.email('');
                self.mobile('');
                self.password('');
                self.confirmPassword('');


                // =================================================
                // ROUTE TO LOGIN
                // =================================================

                setTimeout(function () {

                    Router.rootInstance
                        .go('login')
                        .catch(function (error) {

                            console.error(
                                'Unable to navigate to login:',
                                error
                            );

                        });

                }, 1000);


            } catch (error) {

                console.error(
                    'Registration failed:',
                    error
                );


                self.errorMessage(errorMessages.forRegistration(error));


            } finally {

                self.isLoading(false);
            }
        };
    }


    return RegisterViewModel;

});
