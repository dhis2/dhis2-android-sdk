package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramStoreInterface;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

public final class EventWithLimitCall extends SyncCall<List<Event>> {
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
    public List<Event> call() throws D2Error {
        setExecuted();

        return new D2CallExecutor().executeD2CallTransactionally(databaseAdapter, new Callable<List<Event>>() {

            @Override
            public List<Event> call() throws Exception {
                return getEvents();
            }
        });
    }

    private List<Event> getEvents() throws D2Error {
        Collection<String> organisationUnitUids;
        List<String> programUids = programStore.queryWithoutRegistrationProgramUids();
        List<Event> events = new ArrayList<>();
        EventQuery.Builder eventQueryBuilder = EventQuery.Builder.create();
        int pageSize = eventQueryBuilder.build().getPageSize();

        if (limitByOrgUnit) {
            organisationUnitUids = getOrgUnitUids();
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootOrganisationUnitUids();
            eventQueryBuilder.withOuMode(OuMode.DESCENDANTS);
        }

        for (String orgUnitUid : organisationUnitUids) {
            if (!limitByOrgUnit && events.size() == eventLimit) {
                break;
            }
            eventQueryBuilder.withOrgUnit(orgUnitUid);
            events.addAll(getEventsWithPaging(eventQueryBuilder, pageSize, programUids, events.size()));
        }

        return events;
    }

    private List<Event> getEventsWithPaging(EventQuery.Builder eventQueryBuilder,
                                            int pageSize,
                                            Collection<String> programUids,
                                            int globalEventsSize) throws D2Error {
        List<Event> events = new ArrayList<>();

        D2CallExecutor executor = new D2CallExecutor();

        for (String programUid : programUids) {
            eventQueryBuilder.withProgram(programUid);
            int eventLimitForProgram = limitByOrgUnit ? eventLimit -events.size() :
                    eventLimit - globalEventsSize - events.size();
            List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, eventLimitForProgram);

            for (Paging paging : pagingList) {
                eventQueryBuilder.withPageSize(paging.pageSize());
                eventQueryBuilder.withPage(paging.page());

                List<Event> pageEvents = executor.executeD2Call(
                        EventEndpointCall.create(retrofit, eventQueryBuilder.build()));

                if (paging.isLastPage()) {
                    int previousItemsToSkip = pageEvents.size() + paging.previousItemsToSkipCount() - paging.pageSize();
                    int toIndex = previousItemsToSkip < 0 ? pageEvents.size() : pageEvents.size() - previousItemsToSkip;
                    events.addAll(pageEvents.subList(paging.previousItemsToSkipCount(), toIndex));
                } else {
                    events.addAll(pageEvents);
                }

                if (pageEvents.size() < paging.pageSize()) {
                    break;
                }
            }

            if (events.size() == eventLimit) {
                break;
            }
        }

        executor.executeD2Call(EventPersistenceCall.create(databaseAdapter, retrofit, events));

        return events;
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
