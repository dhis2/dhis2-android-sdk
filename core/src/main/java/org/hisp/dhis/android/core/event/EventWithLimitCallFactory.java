package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Retrofit;

@Reusable
public final class EventWithLimitCallFactory {

    private final ResourceModel.Type resourceType = ResourceModel.Type.EVENT;

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final ResourceHandler resourceHandler;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;
    private final ProgramStoreInterface programStore;
    private final D2InternalModules d2InternalModules;

    @Inject
    EventWithLimitCallFactory(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            @NonNull ResourceHandler resourceHandler,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            @NonNull ProgramStoreInterface programStore,
            @NonNull D2InternalModules d2InternalModules) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.systemInfoRepository = systemInfoRepository;
        this.resourceHandler = resourceHandler;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.programStore = programStore;
        this.d2InternalModules = d2InternalModules;
    }

    public Callable<Unit> getCall(final int eventLimit, final boolean limitByOrgUnit) {
        return new Callable<Unit>() {
            @Override
            public Unit call() throws Exception {
                return getEvents(eventLimit, limitByOrgUnit);
            }
        };
    }

    private Unit getEvents(int eventLimit, boolean limitByOrgUnit) throws Exception {
        Collection<String> organisationUnitUids;
        int eventsCount = 0;
        boolean successfulSync = true;

        EventQuery.Builder eventQueryBuilder = EventQuery.builder();
        int pageSize = eventQueryBuilder.build().pageSize();

        List<String> programUids = programStore.queryWithoutRegistrationProgramUids();

        String lastUpdatedStartDate = resourceHandler.getLastUpdated(resourceType);
        eventQueryBuilder.lastUpdatedStartDate(lastUpdatedStartDate);

        systemInfoRepository.download().call();

        if (limitByOrgUnit) {
            organisationUnitUids = getOrgUnitUids();
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids();
            eventQueryBuilder.ouMode(OuMode.DESCENDANTS);
        }

        for (String orgUnitUid : organisationUnitUids) {
            if (!limitByOrgUnit && eventsCount == eventLimit) {
                break;
            }
            eventQueryBuilder.orgUnit(orgUnitUid);
            EventsWithPagingResult result  = getEventsWithPaging(eventQueryBuilder, pageSize,
                    programUids, eventsCount, eventLimit, limitByOrgUnit);
            eventsCount = eventsCount + result.eventCount;
            successfulSync = successfulSync && result.successfulSync;
        }

        if (successfulSync) {
            resourceHandler.handleResource(resourceType);
        }

        return new Unit();
    }

    private EventsWithPagingResult getEventsWithPaging(EventQuery.Builder eventQueryBuilder,
                                                       int pageSize,
                                                       Collection<String> programUids,
                                                       int globalEventsSize,
                                                       int eventLimit,
                                                       boolean limitByOrgUnit) {
        int eventsCount = 0;
        boolean successfulSync = true;
        D2CallExecutor executor = new D2CallExecutor(databaseAdapter);

        for (String programUid : programUids) {
            try {
                eventQueryBuilder.program(programUid);
                int eventLimitForProgram = limitByOrgUnit ? eventLimit - eventsCount :
                        eventLimit - globalEventsSize - eventsCount;
                List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, eventLimitForProgram);

                for (Paging paging : pagingList) {
                    eventQueryBuilder.pageSize(paging.pageSize());
                    eventQueryBuilder.page(paging.page());

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

                    executor.executeD2Call(EventPersistenceCall.create(databaseAdapter, d2InternalModules,
                            eventsToPersist));
                    eventsCount = eventsCount + eventsToPersist.size();

                    if (pageEvents.size() < paging.pageSize()) {
                        break;
                    }
                }

                if (globalEventsSize + eventsCount == eventLimit) {
                    break;
                }

            } catch (D2Error ignored) {
                successfulSync = false;
            }
        }

        return new EventsWithPagingResult(eventsCount, successfulSync);
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

    private static class EventsWithPagingResult {
        int eventCount;
        boolean successfulSync;

        EventsWithPagingResult(int eventCount, boolean successfulSync) {
            this.eventCount = eventCount;
            this.successfulSync = successfulSync;
        }
    }
}