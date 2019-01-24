package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EventPersistenceCallFactory {

    private final SyncHandlerWithTransformer<Event> eventHandler;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final OrganisationUnitModuleDownloader organisationUnitDownloader;
    private final ForeignKeyCleaner foreignKeyCleaner;

    @Inject
    EventPersistenceCallFactory(
            @NonNull SyncHandlerWithTransformer<Event> eventHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
            @NonNull OrganisationUnitModuleDownloader organisationUnitDownloader,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.eventHandler = eventHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitStore = organisationUnitStore;
        this.organisationUnitDownloader = organisationUnitDownloader;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    public Callable<Void> getCall(final Collection<Event> events) {

        return new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                eventHandler.handleMany(events,
                        new ModelBuilder<Event, Event>() {
                            @Override
                            public Event buildModel(Event event) {
                                return event.toBuilder()
                                        .state(State.SYNCED)
                                        .build();
                            }
                        });

                Set<String> searchOrgUnitUids = getMissingOrganisationUnitUids(events);

                if (!searchOrgUnitUids.isEmpty()) {
                    AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                    Callable<List<OrganisationUnit>> organisationUnitCall =
                            organisationUnitDownloader.downloadSearchOrganisationUnits(
                                searchOrgUnitUids, User.builder().uid(authenticatedUserModel.user()).build());
                    organisationUnitCall.call();
                }

                foreignKeyCleaner.cleanForeignKeyErrors();

                return null;
            }
        };
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
}
