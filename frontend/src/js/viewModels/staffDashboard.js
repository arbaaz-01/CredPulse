define(['knockout', '../services/userService', '../services/authService', '../utils/storage', '../utils/errorMessages', 'ojs/ojrouter'], function (ko, userService, authService, storage, errorMessages, Router) {
    'use strict';
    return function StaffDashboardViewModel(role, title, description) {
        const self = this;
        self.title = title; self.description = description; self.user = ko.observable(storage.getUser()); self.isLoading = ko.observable(true); self.errorMessage = ko.observable('');
        self.logout = async function () { await authService.logout(); await Router.rootInstance.go('login'); };
        self.initialize = async function () {
            const stored = storage.getUser();
            if (!stored || stored.role !== role) { await Router.rootInstance.go('login'); return; }
            try { self.user(await userService.getCurrentUser()); }
            catch (error) { console.error('Staff profile load failed:', error); self.errorMessage(errorMessages.forRequest(error, 'Unable to load your profile.')); }
            finally { self.isLoading(false); }
        };
        self.initialize();
    };
});
