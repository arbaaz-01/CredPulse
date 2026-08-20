define(['knockout', '../services/userService', '../services/productService', '../services/applicationService', '../services/cardService', '../components/ragChatbot', '../services/authService', '../utils/storage', '../utils/constants', '../utils/errorMessages', 'ojs/ojrouter'], function (ko, userService, productService, applicationService, cardService, ragChatbot, authService, storage, constants, errorMessages, Router) {
    'use strict';
    function CustomerDashboardViewModel() {
        const self = this;
        self.user = ko.observable(null); self.products = ko.observableArray([]); self.applications = ko.observableArray([]); self.issuedCards = ko.observableArray([]); self.isLoading = ko.observable(true); self.errorMessage = ko.observable('');
        self.userName = ko.pureComputed(function () { return (self.user() || storage.getUser() || {}).name || 'Customer'; });
        self.draftCount = ko.pureComputed(function () { return self.applications().filter(function (a) { return a.status === 'DRAFT'; }).length; });
        self.submittedCount = ko.pureComputed(function () { return self.applications().filter(function (a) { return a.status === 'SUBMITTED' || a.status === 'UNDER_REVIEW'; }).length; });
        self.hasApplications = ko.pureComputed(function () { return self.applications().length > 0; });
        self.hasIssuedCards = ko.pureComputed(function () { return self.issuedCards().length > 0; });
        self.primaryProduct = ko.pureComputed(function () { return self.products()[0] || null; });
        self.primaryDraft = ko.pureComputed(function () { return self.applications().find(function (application) { return application.status === 'DRAFT'; }) || null; });
        self.formatCurrency = function (value) { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(Number(value || 0)); };
        self.formatExpiry = function (month, year) { return String(month || '').padStart(2, '0') + ' / ' + String(year || '').slice(-2); };
        self.formatStatus = function (status) { return (status || '').replace(/_/g, ' '); };
        self.productTheme = function (product) { return Number(product.id || 0) % 3 === 0 ? 'bank-card-icici' : Number(product.id || 0) % 2 === 0 ? 'bank-card-hdfc' : 'bank-card-default'; };
        self.openProduct = function (product) { return Router.rootInstance.go('productDetails/' + encodeURIComponent(product.id)); };
        self.continueApplication = function (application) { return Router.rootInstance.go('applicationForm/' + encodeURIComponent(application.id)); };
        self.viewApplication = function (application) { return Router.rootInstance.go('applicationDetails/' + encodeURIComponent(application.id)); };
        self.viewCard = function (card) { return Router.rootInstance.go('cardDetails/' + encodeURIComponent(card.id)); };
        self.startPrimaryProduct = function () { const product = self.primaryProduct(); return product ? self.openProduct(product) : null; };
        Object.assign(self, ragChatbot.create());
        self.logout = async function () { await authService.logout(); await Router.rootInstance.go('login'); };
        self.loadDashboard = async function () {
            self.isLoading(true); self.errorMessage('');
            try { const result = await Promise.all([userService.getCurrentUser(), productService.getProducts(), applicationService.getMyApplications(), cardService.getMyCards()]); self.user(result[0]); self.products(result[1] || []); self.applications(result[2] || []); self.issuedCards(result[3] || []); }
            catch (error) { if (error && error.status === 401) { await Router.rootInstance.go('login'); return; } console.error('Customer dashboard load failed:', error); self.errorMessage(errorMessages.forRequest(error, 'We could not load your credit-card information. Please try again.')); }
            finally { self.isLoading(false); }
        };
        self.initialize = async function () { const stored = storage.getUser(); if (!stored) { await Router.rootInstance.go('login'); return; } if (stored.role !== constants.ROLES.USER) { await Router.rootInstance.go(stored.role === constants.ROLES.ADMIN ? 'adminDashboard' : 'login'); return; } await self.loadDashboard(); };
        self.initialize();
    }
    return CustomerDashboardViewModel;
});
