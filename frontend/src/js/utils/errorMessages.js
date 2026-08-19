define([], function () {
    'use strict';

    /* Backend response content is never rendered. It may contain SQL,
       constraint names, or other implementation details. */
    function forRequest(error, fallback) {
        const status = error && error.status;
        if (status === 0 || !status) return 'We could not reach CredPulse right now. Please check your connection and try again.';
        if (status === 400 || status === 422) return 'Some information needs your attention. Please review the details and try again.';
        if (status === 401) return 'Your session has expired. Please sign in again.';
        if (status === 403) return 'You do not have permission to complete this action.';
        if (status === 404) return 'The requested information is no longer available.';
        if (status === 409) return 'This information is already in use. Please use different details and try again.';
        if (status >= 500) return 'We are unable to complete this request right now. Please try again shortly.';
        return fallback || 'We could not complete your request. Please try again.';
    }

    return {
        forRequest: forRequest,
        forLogin: function (error) { return error && error.status === 401 ? 'Invalid email address or password.' : forRequest(error, 'We could not sign you in. Please try again.'); },
        forRegistration: function (error) { return error && error.status === 409 ? 'An account with these details already exists.' : forRequest(error, 'We could not create your account. Please try again.'); }
    };
});
