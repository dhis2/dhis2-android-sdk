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

package org.hisp.dhis.android.core.event.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine;
import org.hisp.dhis.android.core.arch.api.paging.internal.Paging;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
public final class EventWithLimitCallFactory {

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

    public Observable<D2Progress> downloadSingleEvents(ProgramDataDownloadParams params) {
        D2ProgressManager progressManager = new D2ProgressManager(2);
        return Observable.merge(
                downloadSystemInfo(progressManager),
                downloadEventsInternal(params, progressManager));
    }

    private Observable<D2Progress> downloadEventsInternal(ProgramDataDownloadParams params,
                                                          D2ProgressManager progressManager) {
        return Observable.create(emitter -> {
            boolean successfulSync = true;

            EventQuery.Builder eventQueryBuilder = EventQuery.builder();
            int pageSize = eventQueryBuilder.build().pageSize();

            String lastUpdatedStartDate = resourceHandler.getLastUpdated(resourceType);
            eventQueryBuilder.lastUpdatedStartDate(lastUpdatedStartDate);

            OrganisationUnitMode ouMode;
            List<String> orgUnits;

            if (!params.orgUnits().isEmpty()) {
                ouMode = OrganisationUnitMode.SELECTED;
                orgUnits = params.orgUnits();
            } else if (params.limitByOrgunit()) {
                ouMode = OrganisationUnitMode.SELECTED;
                orgUnits = userOrganisationUnitLinkStore
                        .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);
            } else {
                ouMode = OrganisationUnitMode.DESCENDANTS;
                orgUnits = userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids();
            }

            eventQueryBuilder.ouMode(ouMode);

            int eventsCount = 0;
            for (String orgUnitUid : orgUnits) {
                if (params.limitByOrgunit()) {
                    eventsCount = 0;
                }
                if (eventsCount >= params.limit()) {
                    break;
                }
                eventQueryBuilder.orgUnit(orgUnitUid);

                List<String> programs;
                if (params.program() != null) {
                    programs = Collections.singletonList(params.program());
                } else {
                    programs = programStore.queryWithoutRegistrationProgramUids();
                }

                for (String programUid : programs) {
                    if (params.limitByProgram()) {
                        eventsCount = 0;
                    }
                    if (eventsCount >= params.limit()) {
                        break;
                    }

                    eventQueryBuilder.program(programUid);

                    EventsWithPagingResult result = getEventsForOrgUnitProgramCombination(eventQueryBuilder,
                            pageSize, params.limit() - eventsCount);
                    eventsCount = eventsCount + result.eventCount;
                    successfulSync = successfulSync && result.successfulSync;
                }
            }

            if (successfulSync && params.program() == null && params.orgUnits().isEmpty()) {
                resourceHandler.handleResource(resourceType);
            }

            emitter.onNext(progressManager.increaseProgress(Event.class, true));
            emitter.onComplete();
        });
    }

    private Observable<D2Progress> downloadSystemInfo(D2ProgressManager progressManager) {
        return systemInfoRepository.download()
                .toSingle(() -> progressManager.increaseProgress(SystemInfo.class, false))
                .toObservable();
    }

    private EventsWithPagingResult getEventsForOrgUnitProgramCombination(EventQuery.Builder eventQueryBuilder,
                                                                         int pageSize,
                                                                         int combinationLimit) {
        int eventsCount = 0;
        boolean successfulSync = true;

        try {
            eventsCount = getEventsWithPaging(eventQueryBuilder, pageSize, combinationLimit);
        } catch (D2Error ignored) {
            successfulSync = false;
        }

        return new EventsWithPagingResult(eventsCount, successfulSync);
    }

    private int getEventsWithPaging(EventQuery.Builder eventQueryBuilder,
                                    int pageSize,
                                    int combinationLimit) throws D2Error {
        int downloadedEventsForCombination = 0;
        List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, combinationLimit);

        for (Paging paging : pagingList) {
            eventQueryBuilder.pageSize(paging.pageSize());
            eventQueryBuilder.page(paging.page());

            List<Event> pageEvents = d2CallExecutor.executeD2Call(
                    endpointCallFactory.getCall(eventQueryBuilder.build()));

            List<Event> eventsToPersist = getEventsToPersist(paging, pageEvents);

            d2CallExecutor.executeD2CallTransactionally(persistenceCallFactory.getCall(eventsToPersist));
            downloadedEventsForCombination += eventsToPersist.size();

            if (pageEvents.size() < paging.pageSize()) {
                break;
            }
        }

        return downloadedEventsForCombination;
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

    private static class EventsWithPagingResult {
        int eventCount;
        boolean successfulSync;

        EventsWithPagingResult(int eventCount, boolean successfulSync) {
            this.eventCount = eventCount;
            this.successfulSync = successfulSync;
        }
    }
}