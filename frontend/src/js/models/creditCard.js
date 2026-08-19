define([], function () {

    'use strict';


    // =========================================================
    // CREDIT CARD MODEL
    // =========================================================

    function CreditCard(data) {

        data = data || {};


        // =====================================================
        // CORE BACKEND FIELDS
        // =====================================================

        this.id =
            data.id || null;


        this.cardLastFour =
            data.cardLastFour || '';


        this.cardHolderName =
            data.cardHolderName || '';


        this.expiryMonth =
            data.expiryMonth || null;


        this.expiryYear =
            data.expiryYear || null;


        this.creditLimit =
            Number(
                data.creditLimit || 0
            );


        this.availableLimit =
            Number(
                data.availableLimit || 0
            );


        this.status =
            data.status || '';


        this.createdAt =
            data.createdAt || null;


        this.updatedAt =
            data.updatedAt || null;



        // =====================================================
        // CARD PRESENTATION / BANK DETAILS
        // =====================================================
        //
        // These fields allow the frontend to render realistic
        // cards.
        //
        // If the backend eventually returns these values,
        // they will automatically be used.
        //
        // =====================================================

        this.bankName =
            data.bankName ||
            'CredPulse Bank';


        this.cardProductName =
            data.cardProductName ||
            'Credit Card';


        this.cardNetwork =
            data.cardNetwork ||
            'VISA';


        this.cardVariant =
            data.cardVariant || '';


        this.theme =
            data.theme ||
            'default';



        // =====================================================
        // DEMO FLAG
        // =====================================================
        //
        // Real backend cards:
        //
        // isDemo = false
        //
        // Temporary HDFC / ICICI cards:
        //
        // isDemo = true
        //
        // =====================================================

        this.isDemo =
            data.isDemo === true;

    }


    return CreditCard;

});