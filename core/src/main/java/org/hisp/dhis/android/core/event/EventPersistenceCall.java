package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitDownloadModule;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public final class EventPersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;

    private final SyncHandler<Event> eventHandler;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final OrganisationUnitDownloadModule organisationUnitDownloadModule;
    private final ForeignKeyCleaner foreignKeyCleaner;

    private final Collection<Event> events;

    private EventPersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull SyncHandler<Event> eventHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            @NonNull OrganisationUnitDownloadModule organisationUnitDownloadModule,
            @NonNull Collection<Event> events,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.eventHandler = eventHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
        this.organisationUnitDownloadModule = organisationUnitDownloadModule;
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

                    Callable<List<OrganisationUnit>> organisationUnitCall =
                            organisationUnitDownloadModule.downloadSearchOrganisationUnits(
                                searchOrgUnitUids, User.builder().uid(authenticatedUserModel.user()).build());
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
                                              D2InternalModules d2InternalModules,
                                              Collection<Event> events) {
        return new EventPersistenceCall(
                databaseAdapter,
                EventHandler.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
                d2InternalModules.organisationUnit,
                events,
                ForeignKeyCleanerImpl.create(databaseAdapter)
        );
    }
}
