package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitCall;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Retrofit;

final class EventPersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final EventHandler eventHandler;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;
    private final SearchOrganisationUnitCall.Factory organisationUnitCallFactory;

    private final Collection<Event> events;

    private EventPersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull EventHandler eventHandler,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
            @NonNull SearchOrganisationUnitCall.Factory organisationUnitCallFactory,
            @NonNull Collection<Event> events) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.eventHandler = eventHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.events = events;
    }

    @Override
    public Void call() throws D2CallException {
        super.setExecuted();

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            eventHandler.handleMany(events);

            Set<String> searchOrgUnitUids = getMissingOrganisationUnitUids(events);

            if (!searchOrgUnitUids.isEmpty()) {
                AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.query().get(0);
                SearchOrganisationUnitCall organisationUnitCall = organisationUnitCallFactory.create(
                        databaseAdapter, retrofit, searchOrgUnitUids, authenticatedUserModel.user());
                organisationUnitCall.call();
            }

            transaction.setSuccessful();
        } finally {
            transaction.end();
        }

        return null;
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
                EventHandler.create(databaseAdapter),
                new AuthenticatedUserStoreImpl(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter),
                SearchOrganisationUnitCall.FACTORY,
                events
        );
    }
}
