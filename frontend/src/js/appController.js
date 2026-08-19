define([
    'knockout',
    'ojs/ojrouter'
], function (
    ko,
    Router
) {

    'use strict';


    // =====================================================
    // ROUTER URL CONFIGURATION
    // =====================================================

    Router.defaults['urlAdapter'] =
        new Router.urlParamAdapter();

    Router.defaults['baseUrl'] = '/';



    function ControllerViewModel() {

        const self = this;


        // =====================================================
        // APPLICATION
        // =====================================================

        self.appName =
            ko.observable('CredPulse');


        // =====================================================
        // ROUTER
        // =====================================================

        self.router =
            Router.rootInstance;


        // =====================================================
        // ROUTES
        // =====================================================

        self.router.configure({

            login: {
                label: 'Login',
                isDefault: true
            },

            register: {
                label: 'Register'
            },

            customerDashboard: {
                label: 'Customer Dashboard'
            },

            'productDetails/{productId}': { label: 'Product Details', value: 'productDetails' },
            'applicationForm/{applicationId}': { label: 'Credit Card Application', value: 'applicationForm' },
            'applicationDetails/{applicationId}': { label: 'Application Details', value: 'applicationDetails' },
            'cardDetails/{cardId}': { label: 'Issued Card Details', value: 'cardDetails' },

            adminDashboard: {
                label: 'Admin Dashboard'
            },

            managerDashboard: {
                label: 'Manager Dashboard'
            },

            creditOfficerDashboard: {
                label: 'Credit Officer Dashboard'
            }

        });


        // =====================================================
        // ORACLE JET MODULE CONFIGURATION
        // =====================================================
        //
        self.moduleConfig =
            self.router.observableModuleConfig;


        // =====================================================
        // NAVIGATION - LOGIN
        // =====================================================

        self.goToLogin = function () {

            return self.router
                .go('login')
                .catch(function (error) {

                    console.error(
                        'Unable to navigate to login:',
                        error
                    );

                });
        };


        // =====================================================
        // NAVIGATION - REGISTER
        // =====================================================

        self.goToRegister = function () {

            return self.router
                .go('register')
                .catch(function (error) {

                    console.error(
                        'Unable to navigate to register:',
                        error
                    );

                });
        };


        // =====================================================
        // NAVIGATION - CUSTOMER DASHBOARD
        // =====================================================

        self.goToCustomerDashboard = function () {

            return self.router
                .go('customerDashboard')
                .catch(function (error) {

                    console.error(
                        'Unable to navigate to customer dashboard:',
                        error
                    );

                });
        };


        // =====================================================
        // NAVIGATION - PRODUCT DETAILS
        // =====================================================

        self.goToProductDetails = function (productId) {

            if (
                productId === null ||
                productId === undefined ||
                productId === ''
            ) {

                console.error(
                    'Cannot open product details without a product ID.'
                );

                return Promise.reject(
                    new Error(
                        'Product ID is required.'
                    )
                );
            }


            const encodedProductId =
                encodeURIComponent(
                    String(productId)
                );


            return self.router
                .go(
                    'productDetails/' +
                    encodedProductId
                )
                .catch(function (error) {

                    console.error(
                        'Unable to navigate to product details:',
                        error
                    );

                    throw error;

                });
        };


        // =====================================================
        // NAVIGATION - ADMIN DASHBOARD
        // =====================================================

        self.goToAdminDashboard = function () {

            return self.router
                .go('adminDashboard')
                .catch(function (error) {

                    console.error(
                        'Unable to navigate to admin dashboard:',
                        error
                    );

                });
        };

    }


    return new ControllerViewModel();

});
