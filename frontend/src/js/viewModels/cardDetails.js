define([
    'knockout',
    '../services/cardService',
    '../services/authService',
    '../utils/storage',
    '../utils/constants',
    '../utils/errorMessages',
    '../models/creditCard',
    '../data/demoCards',
    'ojs/ojrouter'
], function (
    ko,
    cardService,
    authService,
    storage,
    constants,
    errorMessages,
    CreditCard,
    demoCards,
    Router
) {

    'use strict';


    function CardDetailsViewModel(params) {

        const self = this;


        // =====================================================
        // DATA
        // =====================================================

        self.card =
            ko.observable(null);


        self.currentUser =
            ko.observable(
                storage.getUser()
            );


        // =====================================================
        // UI STATE
        // =====================================================

        self.isLoading =
            ko.observable(true);


        self.errorMessage =
            ko.observable('');


        // =====================================================
        // USER NAME
        // =====================================================

        self.userName =
            ko.pureComputed(function () {

                const user =
                    self.currentUser();


                if (
                    user &&
                    user.name
                ) {

                    return user.name;
                }


                return 'Customer';
            });


        // =====================================================
        // GET CARD ID FROM ROUTER
        // =====================================================
        //
        // Route:
        //
        // cardDetails/{cardId}
        //
        // Oracle JET observableModuleConfig provides:
        //
        // params.ojRouter.parameters.cardId
        //
        // =====================================================

        self.getCardIdFromRoute =
            function () {

                try {

                    if (
                        !params ||
                        !params.ojRouter ||
                        !params.ojRouter.parameters ||
                        !params.ojRouter.parameters.cardId
                    ) {

                        return null;
                    }


                    const parameter =
                        params
                            .ojRouter
                            .parameters
                            .cardId;


                    const value =
                        ko.unwrap(
                            parameter
                        );


                    if (
                        value === null ||
                        value === undefined ||
                        value === ''
                    ) {

                        return null;
                    }


                    /*
                     * Router values may already be decoded.
                     * decodeURIComponent is kept safe here.
                     */

                    try {

                        return decodeURIComponent(
                            String(value)
                        );

                    } catch (error) {

                        return String(value);
                    }


                } catch (error) {

                    console.error(
                        'Unable to read card ID from route:',
                        error
                    );


                    return null;
                }
            };


        // =====================================================
        // CURRENCY FORMATTER
        // =====================================================

        self.formatCurrency =
            function (amount) {

                const value =
                    Number(
                        amount || 0
                    );


                return new Intl.NumberFormat(
                    'en-IN',
                    {
                        style:
                            'currency',

                        currency:
                            'INR',

                        maximumFractionDigits:
                            2
                    }
                ).format(value);
            };


        // =====================================================
        // DATE FORMATTER
        // =====================================================

        self.formatDate =
            function (value) {

                if (!value) {

                    return '—';
                }


                try {

                    const date =
                        new Date(value);


                    if (
                        Number.isNaN(
                            date.getTime()
                        )
                    ) {

                        return String(value);
                    }


                    return new Intl.DateTimeFormat(
                        'en-IN',
                        {
                            day:
                                '2-digit',

                            month:
                                'short',

                            year:
                                'numeric',

                            hour:
                                '2-digit',

                            minute:
                                '2-digit'
                        }
                    ).format(date);


                } catch (error) {

                    return String(value);
                }
            };


        // =====================================================
        // EXPIRY
        // =====================================================

        self.formattedExpiry =
            ko.pureComputed(function () {

                const card =
                    self.card();


                if (
                    !card ||
                    !card.expiryMonth ||
                    !card.expiryYear
                ) {

                    return '—';
                }


                const month =
                    String(
                        card.expiryMonth
                    )
                        .padStart(
                            2,
                            '0'
                        );


                return (
                    month +
                    '/' +
                    card.expiryYear
                );
            });


        // =====================================================
        // MASKED CARD NUMBER
        // =====================================================

        self.maskedCardNumber =
            ko.pureComputed(function () {

                const card =
                    self.card();


                if (
                    !card ||
                    !card.cardLastFour
                ) {

                    return '•••• •••• •••• ••••';
                }


                return (
                    '•••• •••• •••• ' +
                    card.cardLastFour
                );
            });


        // =====================================================
        // CREDIT LIMIT
        // =====================================================

        self.formattedCreditLimit =
            ko.pureComputed(function () {

                const card =
                    self.card();


                return self.formatCurrency(
                    card
                        ? card.creditLimit
                        : 0
                );
            });


        // =====================================================
        // AVAILABLE CREDIT
        // =====================================================

        self.formattedAvailableLimit =
            ko.pureComputed(function () {

                const card =
                    self.card();


                return self.formatCurrency(
                    card
                        ? card.availableLimit
                        : 0
                );
            });


        // =====================================================
        // USED CREDIT
        // =====================================================

        self.usedCredit =
            ko.pureComputed(function () {

                const card =
                    self.card();


                if (!card) {

                    return 0;
                }


                return Math.max(

                    Number(
                        card.creditLimit || 0
                    ) -

                    Number(
                        card.availableLimit || 0
                    ),

                    0
                );
            });


        self.formattedUsedCredit =
            ko.pureComputed(function () {

                return self.formatCurrency(
                    self.usedCredit()
                );
            });


        // =====================================================
        // UTILIZATION
        // =====================================================

        self.utilizationPercent =
            ko.pureComputed(function () {

                const card =
                    self.card();


                if (
                    !card ||
                    Number(
                        card.creditLimit || 0
                    ) <= 0
                ) {

                    return 0;
                }


                const percentage =
                    (
                        self.usedCredit() /
                        Number(card.creditLimit)
                    ) * 100;


                return Math.min(
                    Math.max(
                        percentage,
                        0
                    ),
                    100
                );
            });


        self.utilizationText =
            ko.pureComputed(function () {

                return (
                    self
                        .utilizationPercent()
                        .toFixed(1) +
                    '%'
                );
            });


        // =====================================================
        // FORMAT CARD VARIANT
        // =====================================================

        self.cardVariantText =
            ko.pureComputed(function () {

                const card =
                    self.card();


                if (
                    !card ||
                    !card.cardVariant
                ) {

                    return 'Standard';
                }


                return card.cardVariant;
            });


        // =====================================================
        // REDIRECT ACCORDING TO ROLE
        // =====================================================

        self.redirectByRole =
            async function (user) {

                if (
                    !user ||
                    !user.role
                ) {

                    await Router
                        .rootInstance
                        .go('login');

                    return;
                }


                switch (user.role) {

                    case constants.ROLES.USER:

                        await Router
                            .rootInstance
                            .go(
                                'customerDashboard'
                            );

                        break;


                    case constants.ROLES.ADMIN:

                        await Router
                            .rootInstance
                            .go(
                                'adminDashboard'
                            );

                        break;


                    case constants.ROLES.MANAGER:

                    case constants.ROLES.CREDIT_OFFICER:

                    default:

                        await Router
                            .rootInstance
                            .go('login');

                        break;
                }
            };


        // =====================================================
        // VALIDATE CUSTOMER ACCESS
        // =====================================================

        self.validateCustomerAccess =
            async function () {

                const user =
                    storage.getUser();


                if (!user) {

                    await Router
                        .rootInstance
                        .go('login');

                    return false;
                }


                if (
                    user.role !==
                    constants.ROLES.USER
                ) {

                    console.warn(
                        'Unauthorized card details access:',
                        user.role
                    );


                    await self.redirectByRole(
                        user
                    );


                    return false;
                }


                self.currentUser(
                    user
                );


                return true;
            };


        // =====================================================
        // FIND DEMO CARD
        // =====================================================

        self.findDemoCard =
            function (cardId) {

                return demoCards.find(
                    function (demoCard) {

                        return (
                            String(demoCard.id) ===
                            String(cardId)
                        );

                    }
                );
            };


        // =====================================================
        // BUILD DEMO CARD
        // =====================================================

        self.loadDemoCard =
            function (demoCard) {

                const cardData =
                    Object.assign(
                        {},
                        demoCard
                    );


                const user =
                    storage.getUser();


                if (
                    user &&
                    user.name
                ) {

                    cardData.cardHolderName =
                        user.name
                            .toUpperCase();
                }


                self.card(
                    new CreditCard(
                        cardData
                    )
                );
            };


        // =====================================================
        // LOAD CARD
        // =====================================================

        self.loadCard =
            async function () {

                self.isLoading(true);

                self.errorMessage('');


                const cardId =
                    self.getCardIdFromRoute();


                // ---------------------------------------------
                // NO CARD ID
                // ---------------------------------------------

                if (!cardId) {

                    self.errorMessage(
                        'No credit card was selected.'
                    );

                    self.isLoading(false);

                    return;
                }


                try {

                    // =========================================
                    // REAL BACKEND CARD
                    // =========================================
                    //
                    // GET /api/v1/issued-cards/{cardId}
                    //
                    // =========================================

                    const response =
                        await cardService
                            .getCard(
                                cardId
                            );


                    if (!response) {

                        self.errorMessage(
                            'Credit card details could not be found.'
                        );

                        return;
                    }


                    self.card(
                        new CreditCard(
                            response
                        )
                    );


                } catch (error) {

                    console.error(
                        'Unable to load credit card details:',
                        error
                    );


                    // =========================================
                    // SESSION EXPIRED
                    // =========================================

                    if (
                        error &&
                        error.status === 401
                    ) {

                        storage.clear();


                        try {

                            await Router
                                .rootInstance
                                .go('login');

                        } catch (routerError) {

                            console.error(
                                'Unable to navigate to login:',
                                routerError
                            );
                        }


                        return;
                    }


                    // =========================================
                    // FORBIDDEN
                    // =========================================

                    if (
                        error &&
                        error.status === 403
                    ) {

                        self.errorMessage(
                            'You do not have permission to view this credit card.'
                        );


                        return;
                    }


                    // =========================================
                    // NOT FOUND
                    // =========================================

                    if (
                        error &&
                        error.status === 404
                    ) {

                        self.errorMessage(
                            'The requested credit card could not be found.'
                        );


                        return;
                    }


                    // =========================================
                    // OTHER ERROR
                    // =========================================

                    self.errorMessage(errorMessages.forRequest(error, 'Unable to load credit card details. Please try again.'));


                } finally {

                    self.isLoading(false);
                }
            };


        // =====================================================
        // BACK TO CUSTOMER DASHBOARD
        // =====================================================

        self.goBack =
            async function () {

                try {

                    await Router
                        .rootInstance
                        .go(
                            'customerDashboard'
                        );

                } catch (error) {

                    console.error(
                        'Unable to return to customer dashboard:',
                        error
                    );
                }
            };


        // =====================================================
        // LOGOUT
        // =====================================================

        self.logout =
            async function () {

                self.errorMessage('');


                try {

                    await authService.logout();

                } catch (error) {

                    console.error(
                        'Logout request failed:',
                        error
                    );
                }


                try {

                    await Router
                        .rootInstance
                        .go('login');

                } catch (routerError) {

                    console.error(
                        'Unable to navigate to login:',
                        routerError
                    );
                }
            };


        // =====================================================
        // INITIALIZE
        // =====================================================

        self.initialize =
            async function () {

                const authorized =
                    await self
                        .validateCustomerAccess();


                if (!authorized) {

                    return;
                }


                await self.loadCard();
            };


        self.initialize();

    }


    return CardDetailsViewModel;

});
