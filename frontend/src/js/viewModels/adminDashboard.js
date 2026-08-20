define([
    'knockout',
    '../services/adminService',
    '../services/ragService',
    '../components/ragChatbot',
    '../services/authService',
    '../utils/storage',
    '../utils/constants',
    '../utils/errorMessages',
    'ojs/ojrouter'
], function (
    ko,
    adminService,
    ragService,
    ragChatbot,
    authService,
    storage,
    constants,
    errorMessages,
    Router
) {

    'use strict';


    function AdminDashboardViewModel() {

        const self = this;


        // =====================================================
        // CURRENT ADMIN
        // =====================================================

        self.currentUser =
            ko.observable(
                storage.getUser()
            );


        self.adminName =
            ko.pureComputed(function () {

                const user =
                    self.currentUser();


                if (
                    user &&
                    user.name
                ) {

                    return user.name;
                }


                return 'Admin';
            });


        // =====================================================
        // STAFF DATA
        // =====================================================
        //
        // This collection will contain BOTH:
        //
        // MANAGER
        // CREDIT_OFFICER
        //
        // returned by one backend endpoint.
        //
        // =====================================================

        self.staff =
            ko.observableArray([]);


        // =====================================================
        // UI STATE
        // =====================================================

        self.isLoading =
            ko.observable(false);

        self.isCreating =
            ko.observable(false);

        self.errorMessage =
            ko.observable('');

        self.successMessage =
            ko.observable('');

        Object.assign(self, ragChatbot.create());
        self.isRagIngesting = ko.observable(false);
        self.ragKnowledgeMessage = ko.observable('');
        self.isRagKnowledgeError = ko.observable(false);
        self.ingestRagKnowledge = async function () {
            if (self.isRagIngesting()) { return; }
            self.isRagIngesting(true);
            try {
                const result = await ragService.ingest();
                const counts = result && result.documentsProcessed !== undefined && result.chunksIndexed !== undefined ? ' ' + result.documentsProcessed + ' files and ' + result.chunksIndexed + ' chunks indexed.' : '';
                self.isRagKnowledgeError(false); self.ragKnowledgeMessage('Knowledge base refreshed successfully.' + counts);
                window.setTimeout(function () { self.ragKnowledgeMessage(''); }, 6000);
            } catch (error) {
                self.isRagKnowledgeError(true); self.ragKnowledgeMessage(errorMessages.forRequest(error, 'Could not refresh the knowledge base. Please try again.'));
            } finally { self.isRagIngesting(false); }
        };


        // =====================================================
        // CREATE STAFF FORM
        // =====================================================

        self.newStaffName =
            ko.observable('');

        self.newStaffEmail =
            ko.observable('');

        self.newStaffMobile =
            ko.observable('');

        self.newStaffPassword =
            ko.observable('');

        self.newStaffRole =
            ko.observable('');


        // =====================================================
        // DIRECTORY FILTERS
        // =====================================================

        self.searchQuery =
            ko.observable('');

        self.roleFilter =
            ko.observable('ALL');


        // =====================================================
        // TOTAL STAFF
        // =====================================================

        self.totalStaff =
            ko.pureComputed(function () {

                return self.staff().length;
            });


        // =====================================================
        // TOTAL MANAGERS
        // =====================================================

        self.totalManagers =
            ko.pureComputed(function () {

                return self.staff()
                    .filter(function (member) {

                        return (
                            member.role ===
                            constants.ROLES.MANAGER
                        );

                    })
                    .length;
            });


        // =====================================================
        // TOTAL CREDIT OFFICERS
        // =====================================================

        self.totalCreditOfficers =
            ko.pureComputed(function () {

                return self.staff()
                    .filter(function (member) {

                        return (
                            member.role ===
                            constants.ROLES.CREDIT_OFFICER
                        );

                    })
                    .length;
            });


        // =====================================================
        // FILTERED STAFF
        // =====================================================

        self.filteredStaff =
            ko.pureComputed(function () {

                const filter =
                    self.roleFilter();


                const query =
                    (
                        self.searchQuery() ||
                        ''
                    )
                        .trim()
                        .toLowerCase();


                return self.staff()
                    .filter(function (member) {


                        // -------------------------------------
                        // ROLE FILTER
                        // -------------------------------------

                        if (
                            filter !== 'ALL' &&
                            member.role !== filter
                        ) {

                            return false;
                        }


                        // -------------------------------------
                        // NO SEARCH QUERY
                        // -------------------------------------

                        if (!query) {

                            return true;
                        }


                        // -------------------------------------
                        // SEARCH
                        // -------------------------------------

                        const name =
                            (
                                member.name ||
                                ''
                            )
                                .toLowerCase();


                        const email =
                            (
                                member.email ||
                                ''
                            )
                                .toLowerCase();


                        return (
                            name.includes(query) ||
                            email.includes(query)
                        );

                    });
            });


        // =====================================================
        // SET ROLE FILTER
        // =====================================================

        self.setRoleFilter =
            function (role) {

                self.roleFilter(
                    role
                );
            };


        // =====================================================
        // FORMAT ROLE
        // =====================================================

        self.formatRole =
            function (role) {

                if (
                    role ===
                    constants.ROLES.CREDIT_OFFICER
                ) {

                    return 'Credit Officer';
                }


                if (
                    role ===
                    constants.ROLES.MANAGER
                ) {

                    return 'Manager';
                }


                return role || '';
            };


        // =====================================================
        // CLEAR CREATE STAFF FORM
        // =====================================================

        self.clearCreateForm =
            function () {

                self.newStaffName('');
                self.newStaffEmail('');
                self.newStaffMobile('');
                self.newStaffPassword('');
                self.newStaffRole('');

                self.errorMessage('');
                self.successMessage('');
            };


        // =====================================================
        // CREATE STAFF
        // =====================================================

        self.createStaff =
            async function () {

                if (
                    self.isCreating()
                ) {

                    return;
                }


                self.errorMessage('');
                self.successMessage('');


                // =================================================
                // NORMALIZE VALUES
                // =================================================

                const name =
                    self.newStaffName()
                        ? self
                            .newStaffName()
                            .trim()
                        : '';


                const email =
                    self.newStaffEmail()
                        ? self
                            .newStaffEmail()
                            .trim()
                            .toLowerCase()
                        : '';


                const password =
                    self.newStaffPassword() ||
                    '';

                const mobile =
                    self.newStaffMobile()
                        ? self.newStaffMobile().trim()
                        : '';


                const role =
                    self.newStaffRole();


                // =================================================
                // NAME VALIDATION
                // =================================================

                if (!name) {

                    self.errorMessage(
                        'Full name is required.'
                    );

                    return;
                }


                if (
                    name.length < 2 ||
                    name.length > 100
                ) {

                    self.errorMessage(
                        'Name must be between 2 and 100 characters.'
                    );

                    return;
                }


                // =================================================
                // EMAIL VALIDATION
                // =================================================

                if (!email) {

                    self.errorMessage(
                        'Email address is required.'
                    );

                    return;
                }


                const emailPattern =
                    /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


                if (
                    !emailPattern.test(
                        email
                    )
                ) {

                    self.errorMessage(
                        'Enter a valid email address.'
                    );

                    return;
                }

                if (!/^\d{10}$/.test(mobile)) {
                    self.errorMessage('Mobile number must contain exactly 10 digits.');
                    return;
                }


                // =================================================
                // PASSWORD VALIDATION
                // =================================================

                if (!password) {

                    self.errorMessage(
                        'Temporary password is required.'
                    );

                    return;
                }


                if (
                    password.length < 8
                ) {

                    self.errorMessage(
                        'Password must contain at least 8 characters.'
                    );

                    return;
                }


                // =================================================
                // ROLE VALIDATION
                // =================================================

                if (
                    role !==
                        constants.ROLES.MANAGER &&
                    role !==
                        constants.ROLES.CREDIT_OFFICER
                ) {

                    self.errorMessage(
                        'Select either Manager or Credit Officer.'
                    );

                    return;
                }


                self.isCreating(true);
                try {
                    const created = await adminService.createStaffUser({ name: name, email: email, mobile: mobile, password: password, role: role });
                    self.staff.unshift(created);
                    self.clearCreateForm();
                    self.successMessage('Staff account created successfully.');
                } catch (error) {
                    console.error('Staff account creation failed:', error);
                    self.errorMessage(errorMessages.forRequest(error, 'Unable to create the staff account.'));
                } finally {
                    self.isCreating(false);
                }
            };


        // =====================================================
        // LOAD STAFF
        // =====================================================
        //
        // This will eventually call ONE endpoint returning both:
        //
        // MANAGER
        // CREDIT_OFFICER
        //
        // No mock data is used.
        // =====================================================

        self.loadStaff =
            async function () {

                self.isLoading(true);
                self.errorMessage('');
                try {
                    self.staff(await adminService.getStaffUsers());
                } catch (error) {
                    console.error('Staff account load failed:', error);
                    self.errorMessage(errorMessages.forRequest(error, 'Unable to load staff accounts.'));
                } finally {
                    self.isLoading(false);
                }
            };


        // =====================================================
        // REDIRECT USER TO CORRECT AREA
        // =====================================================

        self.redirectByRole =
            async function (user) {


                // -------------------------------------------------
                // NO LOGGED-IN USER
                // -------------------------------------------------

                if (
                    !user ||
                    !user.role
                ) {

                    await Router
                        .rootInstance
                        .go('login');

                    return;
                }


                switch (user.role) {


                    // =============================================
                    // CUSTOMER
                    // =============================================

                    case constants.ROLES.USER:

                        await Router
                            .rootInstance
                            .go(
                                'customerDashboard'
                            );

                        break;


                    // =============================================
                    // ADMIN
                    // =============================================

                    case constants.ROLES.ADMIN:

                        /*
                         * Already on the correct dashboard.
                         */

                        break;


                    // =============================================
                    // MANAGER
                    // =============================================

                    case constants.ROLES.MANAGER:
                        await Router.rootInstance.go('managerDashboard');

                        break;


                    // =============================================
                    // CREDIT OFFICER
                    // =============================================

                    case constants.ROLES.CREDIT_OFFICER:
                        await Router.rootInstance.go('creditOfficerDashboard');

                        break;


                    // =============================================
                    // UNKNOWN ROLE
                    // =============================================

                    default:

                        console.warn(
                            'Unknown role attempted Admin dashboard access:',
                            user.role
                        );


                        await Router
                            .rootInstance
                            .go('login');

                        break;
                }
            };


        // =====================================================
        // VALIDATE ADMIN ACCESS
        // =====================================================

        self.validateAdminAccess =
            async function () {

                const user =
                    storage.getUser();


                // -------------------------------------------------
                // NOT LOGGED IN
                // -------------------------------------------------

                if (!user) {

                    await Router
                        .rootInstance
                        .go('login');

                    return false;
                }


                // -------------------------------------------------
                // CORRECT ROLE
                // -------------------------------------------------

                if (
                    user.role ===
                    constants.ROLES.ADMIN
                ) {

                    self.currentUser(
                        user
                    );


                    return true;
                }


                // -------------------------------------------------
                // WRONG ROLE
                // -------------------------------------------------

                console.warn(
                    'Unauthorized Admin dashboard access:',
                    user.role
                );


                await self.redirectByRole(
                    user
                );


                return false;
            };


        // =====================================================
        // LOGOUT
        // =====================================================

        self.logout =
            async function () {

                self.errorMessage('');
                self.successMessage('');


                try {

                    await authService.logout();

                } catch (error) {

                    console.error(
                        'Admin logout request failed:',
                        error
                    );
                }


                try {

                    await Router
                        .rootInstance
                        .go('login');

                } catch (routerError) {

                    console.error(
                        'Unable to navigate to login:',
                        routerError
                    );
                }
            };


        // =====================================================
        // INITIALIZE ADMIN DASHBOARD
        // =====================================================

        self.initialize =
            async function () {

                const authorized =
                    await self
                        .validateAdminAccess();


                if (!authorized) {

                    return;
                }


                await self.loadStaff();
            };


        self.initialize();

    }


    return AdminDashboardViewModel;

});
