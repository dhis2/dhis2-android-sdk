/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.api;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.client.sdk.android.api.network.NetworkModuleImpl;
import org.hisp.dhis.client.sdk.android.api.persistence.PersistenceModuleImpl;
import org.hisp.dhis.client.sdk.android.api.preferences.PreferencesModuleImpl;
import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.android.api.utils.LoggerImpl;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementScope;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementScopeImpl;
import org.hisp.dhis.client.sdk.android.event.EventScope;
import org.hisp.dhis.client.sdk.android.event.EventScopeImpl;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitScopeImpl;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorScope;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionScope;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleScope;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableScope;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramScope;
import org.hisp.dhis.client.sdk.android.program.ProgramScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementScope;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageScope;
import org.hisp.dhis.client.sdk.android.program.ProgramStageScopeImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionScope;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionScopeImpl;
import org.hisp.dhis.client.sdk.android.program.UserProgramScope;
import org.hisp.dhis.client.sdk.android.program.UserProgramScopeImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeScope;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeScopeImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueScope;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueScopeImpl;
import org.hisp.dhis.client.sdk.android.user.UserAccountScope;
import org.hisp.dhis.client.sdk.android.user.UserAccountScopeImpl;
import org.hisp.dhis.client.sdk.core.common.Logger;
import org.hisp.dhis.client.sdk.core.common.controllers.ControllersModule;
import org.hisp.dhis.client.sdk.core.common.controllers.ControllersModuleImpl;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.NetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.PersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.PreferencesModule;
import org.hisp.dhis.client.sdk.core.common.services.ServicesModule;
import org.hisp.dhis.client.sdk.core.common.services.ServicesModuleImpl;

import okhttp3.OkHttpClient;
import rx.Observable;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class D2 {
    private static D2 d2;

    private final Context applicationContext;
    private final boolean isD2Configured;

    //-----------------------------------------------------------------------------------------
    // Modules
    //-----------------------------------------------------------------------------------------

    private final PersistenceModule persistenceModule;
    private final PreferencesModule preferencesModule;

    //-----------------------------------------------------------------------------------------
    // Scopes
    //-----------------------------------------------------------------------------------------

    private final UserAccountScope userAccountScope;
    private final OrganisationUnitScope organisationUnitScope;
    private final ProgramScope programScope;
    private final ProgramStageScope programStageScope;
    private final ProgramStageSectionScope programStageSectionScope;
    private final ProgramRuleScope programRuleScope;
    private final ProgramRuleActionScope programRuleActionScope;
    private final ProgramRuleVariableScope programRuleVariableScope;
    private final ProgramIndicatorScope programIndicatorScope;
    private final TrackedEntityAttributeScope trackedEntityAttributeScope;
    private final TrackedEntityDataValueScope trackedEntityDataValueScope;
    private final EventScope eventScope;
    private final ProgramStageDataElementScope programStageDataElementScope;
    private final DataElementScope dataElementScope;

    //-----------------------------------------------------------------------------------------
    // Utilities
    //-----------------------------------------------------------------------------------------

    private final Logger logger;


    private D2(Context context, Flavor flavor) {
        applicationContext = context;

        // Modules which preserve state
        persistenceModule = new PersistenceModuleImpl(applicationContext);
        preferencesModule = new PreferencesModuleImpl(applicationContext);

        isD2Configured = !isEmpty(preferencesModule
                .getConfigurationPreferences().get().getServerUrl());

        if (!isD2Configured) {
            userAccountScope = null;
            organisationUnitScope = null;
            programScope = null;
            programStageScope = null;
            programStageSectionScope = null;
            eventScope = null;
            programStageDataElementScope = null;
            dataElementScope = null;
            programRuleScope = null;
            programRuleActionScope = null;
            programRuleVariableScope = null;
            programIndicatorScope = null;
            trackedEntityAttributeScope = null;
            trackedEntityDataValueScope = null;
            logger = null;
            return;
        }

        ServicesModule servicesModule = new ServicesModuleImpl(persistenceModule);
        NetworkModule networkModule = new NetworkModuleImpl(
                preferencesModule, flavor.getOkHttpClient());
        ControllersModule controllersModule = new ControllersModuleImpl(
                networkModule, persistenceModule, preferencesModule, new LoggerImpl());

        UserProgramScope userProgramScope = new UserProgramScopeImpl(
                servicesModule.getProgramService(),
                controllersModule.getAssignedProgramsController());

        UserOrganisationUnitScope userOrganisationUnitScope = new UserOrganisationUnitScopeImpl(
                servicesModule.getOrganisationUnitService(),
                controllersModule.getAssignedOrganisationUnitsController());

        programScope = new ProgramScopeImpl(
                servicesModule.getProgramService(),
                controllersModule.getProgramController());

        programStageScope = new ProgramStageScopeImpl(
                servicesModule.getProgramStageService(),
                controllersModule.getProgramStageController());

        programStageDataElementScope = new ProgramStageDataElementScopeImpl(
                servicesModule.getProgramStageDataElementService(),
                controllersModule.getProgramStageDataElementController());

        programStageSectionScope = new ProgramStageSectionScopeImpl(
                controllersModule.getProgramStageSectionController(),
                servicesModule.getProgramStageSectionService());

        programRuleScope = new ProgramRuleScopeImpl(
                servicesModule.getProgramRuleService(),
                controllersModule.getProgramRuleController());

        programRuleActionScope = new ProgramRuleActionScopeImpl(
                servicesModule.getProgramRuleActionService(),
                controllersModule.getProgramRuleActionController());

        programRuleVariableScope = new ProgramRuleVariableScopeImpl(
                servicesModule.getProgramRuleVariableService(),
                controllersModule.getProgramRuleVariableController());

        programIndicatorScope = new ProgramIndicatorScopeImpl(
                servicesModule.getProgramIndicatorService(),
                controllersModule.getProgramIndicatorController());

        organisationUnitScope = new OrganisationUnitScopeImpl(
                servicesModule.getOrganisationUnitService(),
                controllersModule.getOrganisationUnitController());

        eventScope = new EventScopeImpl(
                servicesModule.getEventService(),
                controllersModule.getEventController());

        dataElementScope = new DataElementScopeImpl(
                servicesModule.getDataElementService(),
                controllersModule.getDataElementController());

        trackedEntityAttributeScope = new TrackedEntityAttributeScopeImpl(
                servicesModule.getTrackedEntityAttributeService(),
                controllersModule.getTrackedEntityAttributeController());

        trackedEntityDataValueScope = new TrackedEntityDataValueScopeImpl(
                servicesModule.getTrackedEntityDataValueService());

        userAccountScope = new UserAccountScopeImpl(
                preferencesModule.getUserPreferences(),
                servicesModule.getUserAccountService(),
                controllersModule.getUserAccountController(),
                userProgramScope, userOrganisationUnitScope);

        logger = flavor.getLogger();
    }

    // utility method which performs check if D2 is initialised
    @NonNull
    private static D2 instance() {
        isNull(d2, "You have to call init first");

        return d2;
    }

    @NonNull
    private static D2 configuredInstance() {
        isNull(d2, "You have to call init first");

        if (!isConfigured()) {
            throw new UnsupportedOperationException("D2 is not configured as should. " +
                    "You have to call D2.configure(configuration) first");
        }

        return d2;
    }

    /**
     * Initialises D2.
     * <p>
     * Warning! Use only application context to init D2, otherwise you
     * will certainly create a memory leak of activity or other
     * android component.
     *
     * @param context Application context.
     */
    public static void init(@NonNull Context context) {
        init(context, new Builder().build());
    }

    public static void init(@NonNull Context context, @NonNull Flavor flavor) {
        isNull(context, "Context object must not be null");
        isNull(flavor, "Flavor must not be null");

        d2 = new D2(context, flavor);
    }

    /**
     * Sets configuration object to D2 preferences.
     *
     * @param configuration new configuration
     */
    public static Observable<Void> configure(@NonNull final Configuration configuration) {
        isNull(configuration, "Configuration must not be null");

        return Observable.create(new DefaultOnSubscribe<Void>() {
            @Override
            public Void call() {
                instance().preferencesModule.clearAllPreferences();
                instance().persistenceModule.deleteAllTables();

                // save new configuration object
                instance().preferencesModule.getConfigurationPreferences()
                        .save(configuration);

                // re-initialising the whole object graph
                init(instance().applicationContext);
                return null;
            }
        });
    }

    public static Observable<Configuration> configuration() {
        return Observable.create(new DefaultOnSubscribe<Configuration>() {

            @Override
            public Configuration call() {
                return configuredInstance().preferencesModule
                        .getConfigurationPreferences().get();
            }
        });
    }

    /**
     * @return true if D2 was configured
     */
    public static boolean isConfigured() {
        return instance().isD2Configured;
    }

    /**
     * Provides current user aware APIs.
     *
     * @return UserAccountScope instance.
     */
    public static UserAccountScope me() {
        return configuredInstance().userAccountScope;
    }

    public static ProgramScope programs() {
        return configuredInstance().programScope;
    }

    public static ProgramStageScope programStages() {
        return configuredInstance().programStageScope;
    }

    public static ProgramStageSectionScope programStageSections() {
        return configuredInstance().programStageSectionScope;
    }

    public static OrganisationUnitScope organisationUnits() {
        return configuredInstance().organisationUnitScope;
    }

    public static ProgramStageDataElementScope programStageDataElements() {
        return configuredInstance().programStageDataElementScope;
    }

    public static DataElementScope dataElements() {
        return configuredInstance().dataElementScope;
    }

    public static ProgramRuleScope programRules() {
        return configuredInstance().programRuleScope;
    }

    public static ProgramRuleActionScope programRuleActions() {
        return configuredInstance().programRuleActionScope;
    }

    public static ProgramRuleVariableScope programRuleVariables() {
        return configuredInstance().programRuleVariableScope;
    }

    public static ProgramIndicatorScope programIndicators() {
        return configuredInstance().programIndicatorScope;
    }

    public static TrackedEntityAttributeScope trackedEntityAttributes() {
        return configuredInstance().trackedEntityAttributeScope;
    }

    public static EventScope events() {
        return configuredInstance().eventScope;
    }

    public static TrackedEntityDataValueScope trackedEntityDataValues() {
        return configuredInstance().trackedEntityDataValueScope;
    }

    public static Logger logger() {
        return instance().logger;
    }

    public static final class Flavor {
        private final OkHttpClient okHttpClient;
        private final Logger logger;

        public Flavor(OkHttpClient okHttpClient, Logger logger) {
            this.okHttpClient = okHttpClient;
            this.logger = logger;
        }

        public OkHttpClient getOkHttpClient() {
            return okHttpClient;
        }

        public Logger getLogger() {
            return logger;
        }
    }

    public static final class Builder {
        private OkHttpClient okHttpClient;
        private Logger logger;

        public Builder() {
            // explicit empty constructor
        }

        public Builder okHttp(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Flavor build() {
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient();
            }

            if (logger == null) {
                logger = new LoggerImpl();
            }

            return new Flavor(okHttpClient, logger);
        }
    }
}
