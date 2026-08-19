define([
    'ojs/ojbootstrap',
    'knockout',
    './appController',
    'ojs/ojrouter',
    'ojs/ojcontext',
    'ojs/ojknockout',
    'ojs/ojmodule'
], function (
    Bootstrap,
    ko,
    app,
    Router,
    Context
) {

    'use strict';

    Bootstrap.whenDocumentReady()
        .then(function () {

            // Synchronize the Oracle JET router
            return Router.sync();

        })
        .then(function () {

            // Apply Knockout bindings
            ko.applyBindings(
                app,
                document.getElementById('globalBody')
            );

            // main.js sets window["oj_whenReady"] = true,
            // so release the JET bootstrap busy state correctly.
            Context
                .getPageContext()
                .getBusyContext()
                .applicationBootstrapComplete();

        })
        .catch(function (error) {

            console.error(
                'Router initialization failed:',
                error
            );

        });

});