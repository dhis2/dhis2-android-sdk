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
import org.hisp.dhis.client.sdk.android.dataelement.DataElementInteractor;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementInteractorImpl;
import org.hisp.dhis.client.sdk.android.event.EventInteractor;
import org.hisp.dhis.client.sdk.android.event.EventInteractorImpl;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitInteractorImpl;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramStageInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionInteractorImpl;
import org.hisp.dhis.client.sdk.android.program.UserProgramInteractor;
import org.hisp.dhis.client.sdk.android.program.UserProgramInteractorImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeInteractor;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeInteractorImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueInteractor;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueInteractorImpl;
import org.hisp.dhis.client.sdk.android.user.UserAccountInteractor;
import org.hisp.dhis.client.sdk.android.user.UserAccountInteractorImpl;
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

// TODO D2 fails on 500 errors (because of
// TODO response conversion in NetworkModule)
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
    // Interactors
    //-----------------------------------------------------------------------------------------

    private final UserAccountInteractor userAccountInteractor;
    private final OrganisationUnitInteractor organisationUnitInteractor;
    private final ProgramInteractor programInteractor;
    private final ProgramStageInteractor programStageInteractor;
    private final ProgramStageSectionInteractor programStageSectionInteractor;
    private final ProgramRuleInteractor programRuleInteractor;
    private final ProgramRuleActionInteractor programRuleActionInteractor;
    private final ProgramRuleVariableInteractor programRuleVariableInteractor;
    private final ProgramIndicatorInteractor programIndicatorInteractor;
    private final TrackedEntityAttributeInteractor trackedEntityAttributeInteractor;
    private final TrackedEntityDataValueInteractor trackedEntityDataValueInteractor;
    private final EventInteractor eventInteractor;
    private final ProgramStageDataElementInteractor programStageDataElementInteractor;
    private final DataElementInteractor dataElementInteractor;

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
            userAccountInteractor = null;
            organisationUnitInteractor = null;
            programInteractor = null;
            programStageInteractor = null;
            programStageSectionInteractor = null;
            eventInteractor = null;
            programStageDataElementInteractor = null;
            dataElementInteractor = null;
            programRuleInteractor = null;
            programRuleActionInteractor = null;
            programRuleVariableInteractor = null;
            programIndicatorInteractor = null;
            trackedEntityAttributeInteractor = null;
            trackedEntityDataValueInteractor = null;
            logger = null;
            return;
        }

        ServicesModule servicesModule = new ServicesModuleImpl(persistenceModule);
        NetworkModule networkModule = new NetworkModuleImpl(
                preferencesModule, flavor.getOkHttpClient());
        ControllersModule controllersModule = new ControllersModuleImpl(
                networkModule, persistenceModule, preferencesModule, new LoggerImpl());

        UserProgramInteractor userProgramInteractor = new UserProgramInteractorImpl(
                servicesModule.getProgramService(),
                controllersModule.getAssignedProgramsController());

        UserOrganisationUnitInteractor userOrganisationUnitInteractor = new UserOrganisationUnitInteractorImpl(
                servicesModule.getOrganisationUnitService(),
                controllersModule.getAssignedOrganisationUnitsController());

        programInteractor = new ProgramInteractorImpl(
                servicesModule.getProgramService(),
                controllersModule.getProgramController());

        programStageInteractor = new ProgramStageInteractorImpl(
                servicesModule.getProgramStageService(),
                controllersModule.getProgramStageController());

        programStageDataElementInteractor = new ProgramStageDataElementInteractorImpl(
                servicesModule.getProgramStageDataElementService(),
                controllersModule.getProgramStageDataElementController());

        programStageSectionInteractor = new ProgramStageSectionInteractorImpl(
                controllersModule.getProgramStageSectionController(),
                servicesModule.getProgramStageSectionService());

        programRuleInteractor = new ProgramRuleInteractorImpl(
                servicesModule.getProgramRuleService(),
                controllersModule.getProgramRuleController());

        programRuleActionInteractor = new ProgramRuleActionInteractorImpl(
                servicesModule.getProgramRuleActionService(),
                controllersModule.getProgramRuleActionController());

        programRuleVariableInteractor = new ProgramRuleVariableInteractorImpl(
                servicesModule.getProgramRuleVariableService(),
                controllersModule.getProgramRuleVariableController());

        programIndicatorInteractor = new ProgramIndicatorInteractorImpl(
                servicesModule.getProgramIndicatorService(),
                controllersModule.getProgramIndicatorController());

        organisationUnitInteractor = new OrganisationUnitInteractorImpl(
                servicesModule.getOrganisationUnitService(),
                controllersModule.getOrganisationUnitController());

        eventInteractor = new EventInteractorImpl(
                servicesModule.getEventService(),
                controllersModule.getEventController());

        dataElementInteractor = new DataElementInteractorImpl(
                servicesModule.getDataElementService(),
                controllersModule.getDataElementController());

        trackedEntityAttributeInteractor = new TrackedEntityAttributeInteractorImpl(
                servicesModule.getTrackedEntityAttributeService(),
                controllersModule.getTrackedEntityAttributeController());

        trackedEntityDataValueInteractor = new TrackedEntityDataValueInteractorImpl(
                servicesModule.getTrackedEntityDataValueService());

        userAccountInteractor = new UserAccountInteractorImpl(
                preferencesModule.getUserPreferences(),
                servicesModule.getUserAccountService(),
                controllersModule.getUserAccountController(),
                userProgramInteractor, userOrganisationUnitInteractor);

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
     * @return UserAccountInteractor instance.
     */
    public static UserAccountInteractor me() {
        return configuredInstance().userAccountInteractor;
    }

    public static ProgramInteractor programs() {
        return configuredInstance().programInteractor;
    }

    public static ProgramStageInteractor programStages() {
        return configuredInstance().programStageInteractor;
    }

    public static ProgramStageSectionInteractor programStageSections() {
        return configuredInstance().programStageSectionInteractor;
    }

    public static OrganisationUnitInteractor organisationUnits() {
        return configuredInstance().organisationUnitInteractor;
    }

    public static ProgramStageDataElementInteractor programStageDataElements() {
        return configuredInstance().programStageDataElementInteractor;
    }

    public static DataElementInteractor dataElements() {
        return configuredInstance().dataElementInteractor;
    }

    public static ProgramRuleInteractor programRules() {
        return configuredInstance().programRuleInteractor;
    }

    public static ProgramRuleActionInteractor programRuleActions() {
        return configuredInstance().programRuleActionInteractor;
    }

    public static ProgramRuleVariableInteractor programRuleVariables() {
        return configuredInstance().programRuleVariableInteractor;
    }

    public static ProgramIndicatorInteractor programIndicators() {
        return configuredInstance().programIndicatorInteractor;
    }

    public static TrackedEntityAttributeInteractor trackedEntityAttributes() {
        return configuredInstance().trackedEntityAttributeInteractor;
    }

    public static EventInteractor events() {
        return configuredInstance().eventInteractor;
    }

    public static TrackedEntityDataValueInteractor trackedEntityDataValues() {
        return configuredInstance().trackedEntityDataValueInteractor;
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
