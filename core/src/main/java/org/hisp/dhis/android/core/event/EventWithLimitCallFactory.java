/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.Resource;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class EventWithLimitCallFactory {

    private final Resource.Type resourceType = Resource.Type.EVENT;

    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final ResourceHandler resourceHandler;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final ProgramStoreInterface programStore;

    private final D2CallExecutor d2CallExecutor;

    private final EventEndpointCallFactory endpointCallFactory;
    private final EventPersistenceCallFactory persistenceCallFactory;

    @Inject
    EventWithLimitCallFactory(
            @NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            @NonNull ResourceHandler resourceHandler,
            @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            @NonNull ProgramStoreInterface programStore,
            @NonNull D2CallExecutor d2CallExecutor,
            @NonNull EventEndpointCallFactory endpointCallFactory,
            @NonNull EventPersistenceCallFactory persistenceCallFactory) {
        this.systemInfoRepository = systemInfoRepository;
        this.resourceHandler = resourceHandler;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.programStore = programStore;
        this.d2CallExecutor = d2CallExecutor;
        this.endpointCallFactory = endpointCallFactory;
        this.persistenceCallFactory = persistenceCallFactory;
    }

    public Callable<Unit> getCall(final int eventLimit, final boolean limitByOrgUnit) {
        return () -> getEvents(eventLimit, limitByOrgUnit);
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

        for (String programUid : programUids) {
            try {
                eventQueryBuilder.program(programUid);
                int eventLimitForProgram = limitByOrgUnit ? eventLimit - eventsCount :
                        eventLimit - globalEventsSize - eventsCount;
                List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, eventLimitForProgram);

                for (Paging paging : pagingList) {
                    eventQueryBuilder.pageSize(paging.pageSize());
                    eventQueryBuilder.page(paging.page());

                    List<Event> pageEvents = d2CallExecutor.executeD2Call(
                            endpointCallFactory.getCall(eventQueryBuilder.build()));

                    List<Event> eventsToPersist = getEventsToPersist(paging, pageEvents);

                    d2CallExecutor.executeD2CallTransactionally(persistenceCallFactory.getCall(eventsToPersist));
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

    private List<Event> getEventsToPersist(Paging paging, List<Event> pageEvents) {
        if (paging.isLastPage() && pageEvents.size() > paging.previousItemsToSkipCount()) {
            int toIndex = pageEvents.size() < paging.pageSize() - paging.posteriorItemsToSkipCount() ?
                    pageEvents.size() :
                    paging.pageSize() - paging.posteriorItemsToSkipCount();

            return pageEvents.subList(paging.previousItemsToSkipCount(), toIndex);
        } else {
            return pageEvents;
        }
    }

    private Set<String> getOrgUnitUids() {
        List<UserOrganisationUnitLink> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll();

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLink link: userOrganisationUnitLinks) {
            if (link.organisationUnitScope().equals(
                    OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(link.organisationUnit());
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