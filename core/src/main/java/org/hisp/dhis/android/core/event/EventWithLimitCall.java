package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramStoreInterface;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

public final class EventWithLimitCall extends SyncCall<Unit> {
    private final int eventLimit;
    private final boolean limitByOrgUnit;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;
    private final ProgramStoreInterface programStore;

    private EventWithLimitCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            @NonNull ProgramStoreInterface programStore,
            int eventLimit,
            boolean limitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.programStore = programStore;
        this.eventLimit = eventLimit;
        this.limitByOrgUnit = limitByOrgUnit;
    }

    @Override
    public Unit call() throws D2Error {
        setExecuted();

        return new D2CallExecutor().executeD2CallTransactionally(databaseAdapter, new Callable<Unit>() {

            @Override
            public Unit call() {
                return getEvents();
            }
        });
    }

    private Unit getEvents() {
        Collection<String> organisationUnitUids;
        List<String> programUids = programStore.queryWithoutRegistrationProgramUids();
        int eventsCount = 0;
        EventQuery.Builder eventQueryBuilder = EventQuery.Builder.create();
        int pageSize = eventQueryBuilder.build().getPageSize();

        if (limitByOrgUnit) {
            organisationUnitUids = getOrgUnitUids();
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootOrganisationUnitUids();
            eventQueryBuilder.withOuMode(OuMode.DESCENDANTS);
        }

        for (String orgUnitUid : organisationUnitUids) {
            if (!limitByOrgUnit && eventsCount == eventLimit) {
                break;
            }
            eventQueryBuilder.withOrgUnit(orgUnitUid);
            eventsCount = eventsCount + getEventsWithPaging(eventQueryBuilder, pageSize, programUids, eventsCount);
        }

        return new Unit();
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private int getEventsWithPaging(EventQuery.Builder eventQueryBuilder,
                                            int pageSize,
                                            Collection<String> programUids,
                                            int globalEventsSize) {
        int eventsCount = 0;
        D2CallExecutor executor = new D2CallExecutor();

        for (String programUid : programUids) {
            try {
                eventQueryBuilder.withProgram(programUid);
                int eventLimitForProgram = limitByOrgUnit ? eventLimit - eventsCount :
                        eventLimit - globalEventsSize - eventsCount;
                List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, eventLimitForProgram);

                for (Paging paging : pagingList) {
                    eventQueryBuilder.withPageSize(paging.pageSize());
                    eventQueryBuilder.withPage(paging.page());

                    List<Event> pageEvents = executor.executeD2Call(
                            EventEndpointCall.create(retrofit, databaseAdapter, eventQueryBuilder.build()));

                    List<Event> eventsToPersist;
                    if (paging.isLastPage()) {
                        int previousItemsToSkip =
                                pageEvents.size() + paging.previousItemsToSkipCount() - paging.pageSize();
                        int toIndex =
                                previousItemsToSkip < 0 ? pageEvents.size() : pageEvents.size() - previousItemsToSkip;
                        eventsToPersist = pageEvents.subList(paging.previousItemsToSkipCount(), toIndex);
                    } else {
                        eventsToPersist = pageEvents;
                    }

                    executor.executeD2Call(EventPersistenceCall.create(databaseAdapter, retrofit, eventsToPersist));
                    eventsCount = eventsCount + eventsToPersist.size();

                    if (pageEvents.size() < paging.pageSize()) {
                        break;
                    }
                }

                if (eventsCount == eventLimit) {
                    break;
                }

            } catch (D2Error ignored) {
                // The D2Error is ignored so that all calls are executed.
            }
        }

        return eventsCount;
    }

    private Set<String> getOrgUnitUids() {
        List<UserOrganisationUnitLinkModel> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll();

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLinkModel linkModel: userOrganisationUnitLinks) {
            if (linkModel.organisationUnitScope().equals(
                    OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(linkModel.organisationUnit());
            }
        }

        return organisationUnitUids;
    }

    public static EventWithLimitCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                            int eventLimit, boolean limitByOrgUnit) {
        return new EventWithLimitCall(
                databaseAdapter,
                retrofit,
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                ProgramStore.create(databaseAdapter),
                eventLimit,
                limitByOrgUnit
        );
    }
}