define([
    './apiService',
    '../utils/constants'
], function (
    apiService,
    constants
) {

    'use strict';


    return {

        // =====================================================
        // ADD CREDIT CARD
        // =====================================================
        //
        // POST /api/v1/cards
        //
        // apiService automatically attaches the JWT.
        // =====================================================

        addCard: function (cardData) {

            return apiService.post(
                constants.ENDPOINTS.CARDS.ALL,
                cardData
            );
        },


        // =====================================================
        // GET CURRENT USER'S CARDS
        // =====================================================
        //
        // GET /api/v1/cards
        // =====================================================

        getMyCards: function () {

            return apiService.get(
                constants.ENDPOINTS.ISSUED_CARDS.ALL
            );
        },


        // =====================================================
        // GET ONE CARD
        // =====================================================
        //
        // GET /api/v1/cards/{cardId}
        // =====================================================

        getCard: function (cardId) {

            return apiService.get(
                constants.ENDPOINTS.ISSUED_CARDS.BY_ID(cardId)
            );
        },


        // =====================================================
        // UPDATE CARD STATUS
        // =====================================================
        //
        // PATCH /api/v1/cards/{cardId}/status
        //
        // Request:
        //
        // {
        //     status: "ACTIVE"
        // }
        //
        // or
        //
        // {
        //     status: "INACTIVE"
        // }
        // =====================================================

        updateCardStatus: function (
            cardId,
            status
        ) {

            return apiService.patch(
                constants.ENDPOINTS.CARDS.STATUS(cardId),
                {
                    status: status
                }
            );
        },


        // =====================================================
        // REMOVE CARD
        // =====================================================
        //
        // DELETE /api/v1/cards/{cardId}
        // =====================================================

        removeCard: function (cardId) {

            return apiService.delete(
                constants.ENDPOINTS.CARDS.BY_ID(cardId)
            );
        }

    };

});
