define([], function () {

    'use strict';

    return {


        // =====================================================
        // REQUIRED VALUE
        // =====================================================

        isRequired: function (value) {

            return (
                value !== null &&
                value !== undefined &&
                String(value).trim() !== ''
            );
        },


        // =====================================================
        // NAME
        // Backend:
        // minimum 2 characters
        // maximum 100 characters
        // =====================================================

        isName: function (value) {

            if (!value) {
                return false;
            }

            const name =
                String(value).trim();

            return (
                name.length >= 2 &&
                name.length <= 100
            );
        },


        // =====================================================
        // EMAIL
        // =====================================================

        isEmail: function (value) {

            if (!value) {
                return false;
            }

            const email =
                String(value).trim();

            return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
                .test(email);
        },


        // =====================================================
        // MOBILE
        // Backend requires exactly 10 digits.
        // =====================================================

        isMobile: function (value) {

            if (!value) {
                return false;
            }

            return /^[0-9]{10}$/
                .test(String(value).trim());
        },


        // =====================================================
        // PASSWORD
        // Backend RegisterRequest:
        // minimum 8
        // maximum 100
        // =====================================================

        isPassword: function (value) {

            return (
                typeof value === 'string' &&
                value.length >= 8 &&
                value.length <= 100
            );
        },


        // =====================================================
        // CARD NUMBER
        // Basic frontend validation only.
        //
        // Backend performs the final Luhn validation using
        // CardNumberUtil.
        // =====================================================

        isCardNumber: function (value) {

            if (!value) {
                return false;
            }

            return /^\d{13,19}$/
                .test(String(value).trim());
        },


        // =====================================================
        // CARD HOLDER NAME
        // =====================================================

        isCardHolderName: function (value) {

            if (!value) {
                return false;
            }

            const name =
                String(value).trim();

            return (
                name.length > 0 &&
                name.length <= 100
            );
        },


        // =====================================================
        // EXPIRY MONTH
        // =====================================================

        isExpiryMonth: function (value) {

            const month =
                Number(value);

            return (
                Number.isInteger(month) &&
                month >= 1 &&
                month <= 12
            );
        },


        // =====================================================
        // EXPIRY YEAR
        // Matches your current Spring Boot validation.
        // =====================================================

        isExpiryYear: function (value) {

            const year =
                Number(value);

            return (
                Number.isInteger(year) &&
                year >= 2026
            );
        },


        // =====================================================
        // CREDIT LIMIT
        // Backend requires > 0
        // =====================================================

        isCreditLimit: function (value) {

            const amount =
                Number(value);

            return (
                Number.isFinite(amount) &&
                amount > 0
            );
        }

    };

});