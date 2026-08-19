define([], function () {
    'use strict';

    function User(data) {
        data = data || {};

        this.id = data.id || data.userId || null;
        this.name = data.name || '';
        this.email = data.email || '';
        this.mobile = data.mobile || '';
        this.status = data.status || '';
        this.role = data.role || '';
        this.createdAt = data.createdAt || null;
    }

    return User;
});