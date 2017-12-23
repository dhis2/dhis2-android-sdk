/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.calls.SingleDataCall;
import org.hisp.dhis.android.core.calls.TrackedEntityInstancePostCall;
import org.hisp.dhis.android.core.calls.TrackerDataCall;
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryComboQuery;
import org.hisp.dhis.android.core.category.CategoryComboService;
import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionHandler;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.category.CategoryService;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventPostCall;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.option.OptionSetService;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramIndicatorStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStoreImpl;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStageStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramStoreImpl;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;
import org.hisp.dhis.android.core.user.IsUserLoggedInCallable;
import org.hisp.dhis.android.core.user.LogOutUserCallable;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserAuthenticateCall;
import org.hisp.dhis.android.core.user.UserCredentialsHandler;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkStore;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserRoleStore;
import org.hisp.dhis.android.core.user.UserRoleStoreImpl;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// ToDo: handle corner cases when user initially has been signed in, but later was locked (or
// password has changed)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
public final class D2 {
    private final Retrofit retrofit;
    private final DatabaseAdapter databaseAdapter;

    // services
    private final UserService userService;
    private final SystemInfoService systemInfoService;
    private final ProgramService programService;
    private final OrganisationUnitService organisationUnitService;
    private final TrackedEntityService trackedEntityService;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final OptionSetService optionSetService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final CategoryComboService comboService;

    // Queries
    private final CategoryQuery categoryQuery = CategoryQuery.defaultQuery();
    private final CategoryComboQuery categoryComboQuery = CategoryComboQuery.defaultQuery();

    // stores
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final OrganisationUnitStore organisationUnitStore;
    private final ResourceStore resourceStore;
    private final SystemInfoStore systemInfoStore;
    private final UserRoleStore userRoleStore;
    private final UserRoleProgramLinkStore userRoleProgramLinkStore;
    private final ProgramStore programStore;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    private final ProgramRuleVariableStore programRuleVariableStore;
    private final ProgramIndicatorStore programIndicatorStore;
    private final ProgramStageSectionProgramIndicatorLinkStore
            programStageSectionProgramIndicatorLinkStore;
    private final ProgramRuleActionStore programRuleActionStore;
    private final ProgramRuleStore programRuleStore;
    private final OptionStore optionStore;
    private final OptionSetStore optionSetStore;
    private final DataElementStore dataElementStore;
    private final ProgramStageDataElementStore programStageDataElementStore;
    private final ProgramStageSectionStore programStageSectionStore;
    private final ProgramStageStore programStageStore;
    private final RelationshipTypeStore relationshipStore;
    private final TrackedEntityStore trackedEntityStore;

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;

    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    private final OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore;

    private final CategoryOptionStore categoryOptionStore;
    private final CategoryStore categoryStore;
    private final CategoryComboStore categoryComboStore;
    private final CategoryCategoryComboLinkStore categoryCategoryComboLinkStore;
    private final CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore;
    private final CategoryOptionComboCategoryLinkStore categoryComboOptionCategoryLinkStore;

    //Handlers
    private final UserCredentialsHandler userCredentialsHandler;
    private final EventHandler eventHandler;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final ResourceHandler resourceHandler;
    private final CategoryHandler categoryHandler;
    private final CategoryComboHandler categoryComboHandler;
    private final OrganisationUnitHandler organisationUnitHandler;


    @VisibleForTesting
    D2(@NonNull Retrofit retrofit, @NonNull DatabaseAdapter databaseAdapter) {
        this.retrofit = retrofit;
        this.databaseAdapter = databaseAdapter;

        // services
        this.userService = retrofit.create(UserService.class);
        this.systemInfoService = retrofit.create(SystemInfoService.class);
        this.programService = retrofit.create(ProgramService.class);
        this.organisationUnitService = retrofit.create(OrganisationUnitService.class);
        this.trackedEntityService = retrofit.create(TrackedEntityService.class);
        this.optionSetService = retrofit.create(OptionSetService.class);
        this.trackedEntityInstanceService = retrofit.create(TrackedEntityInstanceService.class);
        this.eventService = retrofit.create(EventService.class);
        this.categoryService = retrofit.create(CategoryService.class);
        this.comboService = retrofit.create(CategoryComboService.class);

        // stores

        this.userStore =
                new UserStoreImpl(databaseAdapter);
        this.userCredentialsStore =
                new UserCredentialsStoreImpl(databaseAdapter);
        this.userOrganisationUnitLinkStore =
                new UserOrganisationUnitLinkStoreImpl(databaseAdapter);
        this.authenticatedUserStore =
                new AuthenticatedUserStoreImpl(databaseAdapter);
        this.organisationUnitStore =
                new OrganisationUnitStoreImpl(databaseAdapter);
        this.resourceStore =
                new ResourceStoreImpl(databaseAdapter);
        this.systemInfoStore =
                new SystemInfoStoreImpl(databaseAdapter);
        this.userRoleStore =
                new UserRoleStoreImpl(databaseAdapter);
        this.userRoleProgramLinkStore =
                new UserRoleProgramLinkStoreImpl(databaseAdapter);
        this.programStore =
                new ProgramStoreImpl(databaseAdapter);
        this.trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter);
        this.programTrackedEntityAttributeStore =
                new ProgramTrackedEntityAttributeStoreImpl(databaseAdapter);
        this.programRuleVariableStore =
                new ProgramRuleVariableStoreImpl(databaseAdapter);
        this.programIndicatorStore =
                new ProgramIndicatorStoreImpl(databaseAdapter);
        this.programStageSectionProgramIndicatorLinkStore =
                new ProgramStageSectionProgramIndicatorLinkStoreImpl(databaseAdapter);
        this.programRuleActionStore =
                new ProgramRuleActionStoreImpl(databaseAdapter);
        this.programRuleStore =
                new ProgramRuleStoreImpl(databaseAdapter);
        this.optionStore =
                new OptionStoreImpl(databaseAdapter);
        this.optionSetStore =
                new OptionSetStoreImpl(databaseAdapter);
        this.dataElementStore =
                new DataElementStoreImpl(databaseAdapter);
        this.programStageDataElementStore =
                new ProgramStageDataElementStoreImpl(databaseAdapter);
        this.programStageSectionStore =
                new ProgramStageSectionStoreImpl(databaseAdapter);
        this.programStageStore =
                new ProgramStageStoreImpl(databaseAdapter);
        this.relationshipStore =
                new RelationshipTypeStoreImpl(databaseAdapter);
        this.trackedEntityStore =
                new TrackedEntityStoreImpl(databaseAdapter);
        this.trackedEntityInstanceStore =
                new TrackedEntityInstanceStoreImpl(databaseAdapter);
        this.enrollmentStore =
                new EnrollmentStoreImpl(databaseAdapter);
        this.eventStore =
                new EventStoreImpl(databaseAdapter);

        this.trackedEntityDataValueStore =
                new TrackedEntityDataValueStoreImpl(databaseAdapter);
        this.trackedEntityAttributeValueStore =
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter);
        this.organisationUnitProgramLinkStore =
                new OrganisationUnitProgramLinkStoreImpl(databaseAdapter);

        this.categoryStore = new CategoryStoreImpl(databaseAdapter);
        this.categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        this.categoryCategoryOptionLinkStore = new CategoryCategoryOptionLinkStoreImpl(
                databaseAdapter());
        this.categoryComboOptionCategoryLinkStore
                = new CategoryOptionComboCategoryLinkStoreImpl(databaseAdapter);
        this.categoryComboStore = new CategoryComboStoreImpl(databaseAdapter());
        this.categoryCategoryComboLinkStore = new CategoryCategoryComboLinkStoreImpl(
                databaseAdapter());
        CategoryOptionComboStore categoryOptionComboStore = new CategoryOptionComboStoreImpl(
                databaseAdapter());

        //handlers
        userCredentialsHandler = new UserCredentialsHandler(userCredentialsStore);
        resourceHandler = new ResourceHandler(resourceStore);

        organisationUnitHandler = new OrganisationUnitHandler(organisationUnitStore,
                userOrganisationUnitLinkStore, organisationUnitProgramLinkStore);

        TrackedEntityDataValueHandler trackedEntityDataValueHandler =
                new TrackedEntityDataValueHandler(trackedEntityDataValueStore);

        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore);

        this.eventHandler = new EventHandler(eventStore, trackedEntityDataValueHandler);

        TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler =
                new TrackedEntityAttributeValueHandler(trackedEntityAttributeValueStore);

        EnrollmentHandler enrollmentHandler = new EnrollmentHandler(enrollmentStore, eventHandler);

        trackedEntityInstanceHandler =
                new TrackedEntityInstanceHandler(
                        trackedEntityInstanceStore,
                        trackedEntityAttributeValueHandler,
                        enrollmentHandler);

        categoryHandler = new CategoryHandler(categoryStore, categoryOptionHandler,
                categoryCategoryOptionLinkStore);

        CategoryOptionComboHandler optionComboHandler = new CategoryOptionComboHandler(
                categoryOptionComboStore);

        categoryComboHandler = new CategoryComboHandler(categoryComboStore,
                categoryComboOptionCategoryLinkStore,
                categoryCategoryComboLinkStore, optionComboHandler);
    }

    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    public DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }

    @NonNull
    public Call<Response<User>> logIn(@NonNull String username, @NonNull String password) {
        if (username == null) {
            throw new NullPointerException("username == null");
        }
        if (password == null) {
            throw new NullPointerException("password == null");
        }

        return new UserAuthenticateCall(userService, databaseAdapter, userStore,
                userCredentialsHandler, resourceHandler,
                authenticatedUserStore, organisationUnitHandler, username, password
        );
    }

    @NonNull
    public Callable<Boolean> isUserLoggedIn() {
        AuthenticatedUserStore authenticatedUserStore =
                new AuthenticatedUserStoreImpl(databaseAdapter);

        return new IsUserLoggedInCallable(authenticatedUserStore);
    }

    @NonNull
    public Callable<Void> logout() {
        List<DeletableStore> deletableStoreList = new ArrayList<>();
        deletableStoreList.add(authenticatedUserStore);
        return new LogOutUserCallable(
                deletableStoreList
        );
    }

    @NonNull
    public Callable<Void> wipeDB() {
        List<DeletableStore> deletableStoreList = new ArrayList<>();
        deletableStoreList.add(userStore);
        deletableStoreList.add(userCredentialsStore);
        deletableStoreList.add(userOrganisationUnitLinkStore);
        deletableStoreList.add(authenticatedUserStore);
        deletableStoreList.add(organisationUnitStore);
        deletableStoreList.add(resourceStore);
        deletableStoreList.add(systemInfoStore);
        deletableStoreList.add(userRoleStore);
        deletableStoreList.add(userRoleProgramLinkStore);
        deletableStoreList.add(programStore);
        deletableStoreList.add(trackedEntityAttributeStore);
        deletableStoreList.add(programTrackedEntityAttributeStore);
        deletableStoreList.add(programRuleVariableStore);
        deletableStoreList.add(programIndicatorStore);
        deletableStoreList.add(programStageSectionProgramIndicatorLinkStore);
        deletableStoreList.add(programRuleActionStore);
        deletableStoreList.add(programRuleStore);
        deletableStoreList.add(optionStore);
        deletableStoreList.add(optionSetStore);
        deletableStoreList.add(dataElementStore);
        deletableStoreList.add(programStageDataElementStore);
        deletableStoreList.add(programStageSectionStore);
        deletableStoreList.add(programStageStore);
        deletableStoreList.add(relationshipStore);
        deletableStoreList.add(trackedEntityStore);
        deletableStoreList.add(trackedEntityInstanceStore);
        deletableStoreList.add(enrollmentStore);
        deletableStoreList.add(trackedEntityDataValueStore);
        deletableStoreList.add(trackedEntityAttributeValueStore);
        deletableStoreList.add(organisationUnitProgramLinkStore);
        deletableStoreList.add(eventStore);
        deletableStoreList.add(categoryStore);
        deletableStoreList.add(categoryOptionStore);
        deletableStoreList.add(categoryCategoryOptionLinkStore);
        deletableStoreList.add(categoryComboOptionCategoryLinkStore);
        deletableStoreList.add(categoryComboStore);
        deletableStoreList.add(categoryCategoryComboLinkStore);
        return new LogOutUserCallable(
                deletableStoreList
        );
    }

    @NonNull
    public Call<Response> syncMetaData() {
        return new MetadataCall(
                databaseAdapter, systemInfoService, userService, programService,
                organisationUnitService,
                trackedEntityService, optionSetService, systemInfoStore, resourceStore, userStore,
                userCredentialsStore, userRoleStore, userRoleProgramLinkStore,
                organisationUnitStore,
                userOrganisationUnitLinkStore, programStore, trackedEntityAttributeStore,
                programTrackedEntityAttributeStore, programRuleVariableStore, programIndicatorStore,
                programStageSectionProgramIndicatorLinkStore, programRuleActionStore,
                programRuleStore, optionStore,
                optionSetStore, dataElementStore, programStageDataElementStore,
                programStageSectionStore,
                programStageStore, relationshipStore, trackedEntityStore,
                organisationUnitProgramLinkStore, categoryQuery,
                categoryService, categoryHandler, categoryComboQuery, comboService,
                categoryComboHandler);
    }

    @NonNull
    public Call<Response> syncSingleData(int eventLimitByOrgUnit) {
        return new SingleDataCall(organisationUnitStore, systemInfoStore, systemInfoService,
                resourceStore,
                eventService, databaseAdapter, resourceHandler, eventHandler, eventLimitByOrgUnit);
    }

    @NonNull
    public Call<Response> syncTrackerData() {
        return new TrackerDataCall(trackedEntityInstanceStore, systemInfoStore, systemInfoService,
                resourceStore, trackedEntityInstanceService, databaseAdapter, resourceHandler,
                trackedEntityInstanceHandler);
    }

    @NonNull
    public Call<Response<WebResponse>> syncTrackedEntityInstances() {
        return new TrackedEntityInstancePostCall(trackedEntityInstanceService,
                trackedEntityInstanceStore, enrollmentStore, eventStore,
                trackedEntityDataValueStore,
                trackedEntityAttributeValueStore);
    }

    public Call<Response<WebResponse>> syncSingleEvents() {
        return new EventPostCall(eventService, eventStore, trackedEntityDataValueStore);
    }

    public static class Builder {
        private ConfigurationModel configuration;
        private DatabaseAdapter databaseAdapter;
        private OkHttpClient okHttpClient;

        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder configuration(@NonNull ConfigurationModel configuration) {
            this.configuration = configuration;
            return this;
        }

        @NonNull
        public Builder databaseAdapter(@NonNull DatabaseAdapter databaseAdapter) {
            this.databaseAdapter = databaseAdapter;
            return this;
        }

        @NonNull
        public Builder okHttpClient(@NonNull OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public D2 build() {
            if (databaseAdapter == null) {
                throw new IllegalArgumentException("databaseAdapter == null");
            }

            if (configuration == null) {
                throw new IllegalStateException("configuration must be set first");
            }

            if (okHttpClient == null) {
                throw new IllegalArgumentException("okHttpClient == null");
            }

            ObjectMapper objectMapper = new ObjectMapper()
                    .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(configuration.serverUrl())
                    .client(okHttpClient)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .addConverterFactory(FilterConverterFactory.create())
                    .addConverterFactory(FieldsConverterFactory.create())
                    .validateEagerly(true)
                    .build();

            return new D2(retrofit, databaseAdapter);
        }
    }
}