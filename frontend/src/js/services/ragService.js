define(['./apiService', '../utils/constants'], function (apiService, constants) {
    'use strict';
    return {
        chat: function (question) {
            return apiService.post(constants.ENDPOINTS.RAG.CHAT, { question: question });
        },
        ingest: function () {
            return apiService.post(constants.ENDPOINTS.RAG.INGEST, {});
        }
    };
});
