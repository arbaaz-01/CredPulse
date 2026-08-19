define(['knockout', '../services/productService', '../services/applicationService', '../utils/storage', '../utils/constants', '../utils/errorMessages', 'ojs/ojrouter'], function (ko, productService, applicationService, storage, constants, errorMessages, Router) {
    'use strict';
    function ProductDetailsViewModel(params) {
        const self = this;
        self.product = ko.observable(null); self.requestedCreditLimit = ko.observable(''); self.isLoading = ko.observable(true); self.isSubmitting = ko.observable(false); self.errorMessage = ko.observable('');
        self.userName = ko.pureComputed(function () { return (storage.getUser() || {}).name || 'Customer'; });
        self.formatCurrency = function (value) { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(Number(value || 0)); };
        self.productTheme = function (product) {
            if (!product) {
                return 'bank-card-default';
            }
            return Number(product.id || 0) % 3 === 0 ? 'bank-card-icici' : Number(product.id || 0) % 2 === 0 ? 'bank-card-hdfc' : 'bank-card-default';
        };
        self.back = function () { return Router.rootInstance.go('customerDashboard'); };
        self.startApplication = async function () {
            const product = self.product(); const limit = Number(self.requestedCreditLimit()); self.errorMessage('');
            if (!Number.isFinite(limit) || limit <= 0) { self.errorMessage('Enter a valid requested credit limit.'); return; }
            if (limit < Number(product.minCreditLimit) || limit > Number(product.maxCreditLimit)) { self.errorMessage('Requested limit must be within this product’s permitted range.'); return; }
            self.isSubmitting(true);
            try { const application = await applicationService.createDraft({ productId: product.id, requestedCreditLimit: limit }); await Router.rootInstance.go('applicationForm/' + application.id); }
            catch (error) { console.error('Application start failed:', error); self.errorMessage(errorMessages.forRequest(error, 'Unable to start the application. Please try again.')); }
            finally { self.isSubmitting(false); }
        };
        self.initialize = async function () {
            const stored = storage.getUser(); const id = params && params.ojRouter && params.ojRouter.parameters.productId && params.ojRouter.parameters.productId();
            if (!stored || stored.role !== constants.ROLES.USER) { await Router.rootInstance.go('login'); return; }
            try { const product = await productService.getProduct(id); self.product(product); self.requestedCreditLimit(product.minCreditLimit); }
            catch (error) { self.errorMessage('This credit card product is unavailable.'); }
            finally { self.isLoading(false); }
        }; self.initialize();
    } return ProductDetailsViewModel;
});
