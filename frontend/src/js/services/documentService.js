define(['./apiService', '../utils/constants', '../utils/storage'], function (apiService, constants, storage) {
    'use strict';
    function endpoint(applicationId) { return '/card-applications/' + applicationId + '/documents'; }
    return {
        getDocuments: function (applicationId) { return apiService.get(endpoint(applicationId)); },
        uploadDocument: function (applicationId, documentType, file) {
            const formData = new FormData();
            formData.append('documentType', documentType);
            formData.append('file', file);
            return apiService.postForm(endpoint(applicationId), formData);
        },
        getDocumentPreviewUrl: async function (applicationId, documentId) {
            const response = await fetch(
                constants.API_BASE_URL + endpoint(applicationId) + '/' + documentId + '/view',
                { headers: { Authorization: 'Bearer ' + storage.getAccessToken() } }
            );
            if (!response.ok) { throw { status: response.status }; }
            return URL.createObjectURL(await response.blob());
        }
    };
});
