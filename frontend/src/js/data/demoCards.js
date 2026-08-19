define([], function () {

    'use strict';


    // =========================================================
    // DEMO CREDIT CARDS
    // =========================================================
    //
    // DEVELOPMENT / UI TESTING ONLY
    //
    // These cards are temporary and will be removed or disabled
    // once enough real cards are available from the backend.
    //
    // Real cards will continue to come from:
    //
    // GET /api/v1/cards
    //
    // =========================================================

    return [


        // =====================================================
        // HDFC BANK DEMO CARD
        // =====================================================

        {
            id: 'demo-hdfc-001',

            bankName:
                'HDFC Bank',

            cardProductName:
                'Regalia Gold',

            cardNetwork:
                'VISA',

            cardVariant:
                'Signature',

            cardLastFour:
                '4821',

            cardHolderName:
                'DEMO CUSTOMER',

            expiryMonth:
                8,

            expiryYear:
                2029,

            creditLimit:
                200000,

            availableLimit:
                132500,

            status:
                'ACTIVE',

            theme:
                'hdfc',

            isDemo:
                true
        },


        // =====================================================
        // ICICI BANK DEMO CARD
        // =====================================================

        {
            id: 'demo-icici-001',

            bankName:
                'ICICI Bank',

            cardProductName:
                'Coral Credit Card',

            cardNetwork:
                'MASTERCARD',

            cardVariant:
                'Platinum',

            cardLastFour:
                '7319',

            cardHolderName:
                'DEMO CUSTOMER',

            expiryMonth:
                11,

            expiryYear:
                2030,

            creditLimit:
                150000,

            availableLimit:
                114500,

            status:
                'ACTIVE',

            theme:
                'icici',

            isDemo:
                true
        }

    ];

});