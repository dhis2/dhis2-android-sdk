package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitOnDemandCall;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

public final class EventPersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final APICallExecutor apiCallExecutor;

    private final EventHandler eventHandler;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final SearchOrganisationUnitOnDemandCall.Factory searchOrganisationUnitOnDemandCallFactory;
    private final ForeignKeyCleaner foreignKeyCleaner;

    private final Collection<Event> events;

    private EventPersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull EventHandler eventHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            @NonNull SearchOrganisationUnitOnDemandCall.Factory searchOrganisationUnitOnDemandCallFactory,
            @NonNull Collection<Event> events,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.apiCallExecutor = apiCallExecutor;
        this.eventHandler = eventHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
        this.searchOrganisationUnitOnDemandCallFactory = searchOrganisationUnitOnDemandCallFactory;
        this.events = events;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Void call() throws D2Error {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor(databaseAdapter);

        return executor.executeD2CallTransactionally(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                eventHandler.handleMany(events);

                Set<String> searchOrgUnitUids = getMissingOrganisationUnitUids(events);

                if (!searchOrgUnitUids.isEmpty()) {
                    AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                    Call<List<OrganisationUnit>> organisationUnitCall =
                            searchOrganisationUnitOnDemandCallFactory.create(
                                databaseAdapter, retrofit, searchOrgUnitUids,
                                User.builder().uid(authenticatedUserModel.user()).build(), apiCallExecutor);
                    organisationUnitCall.call();
                }

                foreignKeyCleaner.cleanForeignKeyErrors();

                return null;
            }
        });
    }

    private Set<String> getMissingOrganisationUnitUids(Collection<Event> events) {
        Set<String> uids = new HashSet<>();
        for (Event event : events) {
            if (event.organisationUnit() != null) {
                uids.add(event.organisationUnit());
            }
        }
        uids.removeAll(organisationUnitStore.selectUids());
        return uids;
    }

    public static EventPersistenceCall create(DatabaseAdapter databaseAdapter,
                                              Retrofit retrofit,
                                              Collection<Event> events) {
        return new EventPersistenceCall(
                databaseAdapter,
                retrofit,
                APICallExecutorImpl.create(databaseAdapter),
                EventHandler.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
                SearchOrganisationUnitOnDemandCall.FACTORY,
                events,
                ForeignKeyCleanerImpl.create(databaseAdapter)
        );
    }
}
