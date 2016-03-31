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

import org.hisp.dhis.client.sdk.android.api.network.NetworkModule;
import org.hisp.dhis.client.sdk.android.api.persistence.PersistenceModule;
import org.hisp.dhis.client.sdk.android.api.preferences.PreferencesModule;
import org.hisp.dhis.client.sdk.android.api.utils.Logger;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementScope;
import org.hisp.dhis.client.sdk.android.dataelement.IDataElementScope;
import org.hisp.dhis.client.sdk.android.event.EventScope;
import org.hisp.dhis.client.sdk.android.event.IEventScope;
import org.hisp.dhis.client.sdk.android.organisationunit.IOrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.organisationunit.IUserOrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.program.IProgramScope;
import org.hisp.dhis.client.sdk.android.program.IProgramStageScope;
import org.hisp.dhis.client.sdk.android.program.IProgramStageSectionScope;
import org.hisp.dhis.client.sdk.android.program.IUserProgramScope;
import org.hisp.dhis.client.sdk.android.program.ProgramScope;
import org.hisp.dhis.client.sdk.android.program.ProgramStageScope;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionScope;
import org.hisp.dhis.client.sdk.android.program.UserProgramScope;
import org.hisp.dhis.client.sdk.android.user.IUserAccountScope;
import org.hisp.dhis.client.sdk.android.user.UserAccountScope;
import org.hisp.dhis.client.sdk.core.common.controllers.ControllersModule;
import org.hisp.dhis.client.sdk.core.common.controllers.IControllersModule;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.common.services.IServicesModule;
import org.hisp.dhis.client.sdk.core.common.services.ServicesModule;

import rx.Observable;
import rx.Subscriber;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


// TODO Managing logging properly
// TODO Allow clients to set custom objects (like OkHttpClient)
public class D2 {
    private static D2 d2;

    private final Context applicationContext;
    private final boolean isD2Configured;

    private final IPersistenceModule persistenceModule;
    private final IPreferencesModule preferencesModule;

    private final IUserAccountScope userAccountScope;
    private final IOrganisationUnitScope organisationUnitScope;
    private final IProgramScope programScope;
    private final IProgramStageScope programStageScope;
    private final IProgramStageSectionScope programStageSectionScope;
    private final IEventScope eventScope;
    private final IDataElementScope dataElementScope;

    private D2(Context context) {
        applicationContext = context;

        // Modules which preserve state
        persistenceModule = new PersistenceModule(context);
        preferencesModule = new PreferencesModule(context);

        isD2Configured = !isEmpty(preferencesModule
                .getConfigurationPreferences().get().getServerUrl());

        if (!isD2Configured) {
            userAccountScope = null;
            organisationUnitScope = null;
            programScope = null;
            programStageScope = null;
            programStageSectionScope = null;
            eventScope = null;
            dataElementScope = null;
            return;
        }

        IServicesModule servicesModule = new ServicesModule(persistenceModule);
        INetworkModule networkModule = new NetworkModule(preferencesModule);
        IControllersModule controllersModule = new ControllersModule(
                networkModule, persistenceModule, preferencesModule, new Logger());

        IUserProgramScope userProgramScope = new UserProgramScope(
                servicesModule.getProgramService(),
                controllersModule.getAssignedProgramsController());

        IUserOrganisationUnitScope userOrganisationUnitScope = new UserOrganisationUnitScope(
                servicesModule.getOrganisationUnitService(),
                controllersModule.getAssignedOrganisationUnitsController());

        programScope = new ProgramScope(
                servicesModule.getProgramService(),
                controllersModule.getProgramController());

        programStageScope = new ProgramStageScope(
                servicesModule.getProgramStageService(),
                controllersModule.getProgramStageController());

        programStageSectionScope = new ProgramStageSectionScope(
                controllersModule.getProgramStageSectionController(),
                servicesModule.getProgramStageSectionService());

        organisationUnitScope = new OrganisationUnitScope(
                servicesModule.getOrganisationUnitService(),
                controllersModule.getOrganisationUnitController());

        eventScope = new EventScope(
                servicesModule.getEventService(),
                controllersModule.getEventController());

        dataElementScope = new DataElementScope(
                servicesModule.getDataElementService(),
                controllersModule.getDataElementController());

        userAccountScope = new UserAccountScope(
                preferencesModule.getUserPreferences(),
                servicesModule.getUserAccountService(),
                controllersModule.getUserAccountController(),
                userProgramScope, userOrganisationUnitScope);
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
     * <p/>
     * Warning! Use only application context to init D2, otherwise you
     * will certainly create a memory leak of activity or other
     * android component.
     *
     * @param context Application context.
     */
    public static void init(@NonNull Context context) {
        isNull(context, "Context object must not be null");

        d2 = new D2(context);
    }

    /**
     * Sets configuration object to D2 preferences.
     *
     * @param configuration new configuration
     */
    public static Observable<Void> configure(@NonNull final Configuration configuration) {
        isNull(configuration, "Configuration must not be null");

        return Observable.create(new Observable.OnSubscribe<Void>() {

            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    // erase all existing content
                    instance().preferencesModule.clearAllPreferences();
                    instance().persistenceModule.deleteAllTables();

                    // save new configuration object
                    instance().preferencesModule.getConfigurationPreferences()
                            .save(configuration);

                    // re-initialising the whole object graph
                    init(instance().applicationContext);
                    subscriber.onNext(null);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
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
     * @return IUserAccountScope instance.
     */
    public static IUserAccountScope me() {
        return configuredInstance().userAccountScope;
    }

    public static IProgramScope programs() {
        return configuredInstance().programScope;
    }

    public static IProgramStageScope programStages() {
        return configuredInstance().programStageScope;
    }

    public static IProgramStageSectionScope programStageSections() {
        return configuredInstance().programStageSectionScope;
    }

    public static IOrganisationUnitScope organisationUnits() {
        return configuredInstance().organisationUnitScope;
    }

    public static IDataElementScope dataElements() {
        return configuredInstance().dataElementScope;
    }

    public static IEventScope events() {
        return configuredInstance().eventScope;
    }
}
