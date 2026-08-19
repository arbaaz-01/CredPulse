define(['knockout', '../services/applicationService', '../services/documentService', '../utils/storage', '../utils/constants', '../utils/errorMessages', 'ojs/ojrouter'], function (ko, applicationService, documentService, storage, constants, errorMessages, Router) {
    'use strict';
    function ApplicationDetailsViewModel(params) {
        const self = this;
        self.application = ko.observable(null);
        self.documents = ko.observableArray([]);
        self.isLoading = ko.observable(true);
        self.errorMessage = ko.observable('');
        self.isOpeningDocument = ko.observable(false);
        self.formatCurrency = function (value) { return new Intl.NumberFormat('en-IN', { style:'currency', currency:'INR', maximumFractionDigits:0 }).format(Number(value || 0)); };
        self.formatDate = function (value) { if (!value) { return '—'; } const date = /^\d{4}-\d{2}-\d{2}$/.test(String(value)) ? new Date(value + 'T00:00:00') : new Date(value); return Number.isNaN(date.getTime()) ? '—' : new Intl.DateTimeFormat('en-IN', { day:'2-digit', month:'short', year:'numeric' }).format(date); };
        self.formatStatus = function (value) { return String(value || '').replace(/_/g, ' '); };
        self.formatDocumentType = function (value) { return String(value || '').replace(/_/g, ' '); };
        self.formatSize = function (value) { return value ? (Number(value) / 1024 / 1024).toFixed(2) + ' MB' : ''; };
        self.formattedAddress = function (application) { if (!application) { return '—'; } return [application.addressLine1, application.addressLine2, application.city, application.state, application.postalCode, application.country].filter(function (value) { return value !== null && value !== undefined && String(value).trim() !== ''; }).join(', ') || '—'; };
        self.back = function () { return Router.rootInstance.go('customerDashboard'); };
        self.openDocument = async function (document) {
            self.errorMessage(''); self.isOpeningDocument(true);
            try {
                const previewUrl = await documentService.getDocumentPreviewUrl(self.application().id, document.id);
                const previewWindow = window.open(previewUrl, '_blank');
                if (!previewWindow) { window.location.assign(previewUrl); }
                window.setTimeout(function () { URL.revokeObjectURL(previewUrl); }, 60000);
            } catch (error) { console.error('Document preview failed:', error); self.errorMessage(errorMessages.forRequest(error, 'The document preview could not be opened. Please try again.')); }
            finally { self.isOpeningDocument(false); }
        };
        self.initialize = async function () {
            const stored = storage.getUser();
            const id = params && params.ojRouter && params.ojRouter.parameters.applicationId && params.ojRouter.parameters.applicationId();
            if (!stored || stored.role !== constants.ROLES.USER) { await Router.rootInstance.go('login'); return; }
            try {
                const result = await Promise.all([applicationService.getApplication(id), documentService.getDocuments(id)]);
                self.application(result[0]); self.documents(result[1] || []);
            } catch (error) { console.error('Application detail load failed:', error); self.errorMessage(errorMessages.forRequest(error, 'This application could not be loaded.')); }
            finally { self.isLoading(false); }
        };
        self.initialize();
    }
    return ApplicationDetailsViewModel;
});
