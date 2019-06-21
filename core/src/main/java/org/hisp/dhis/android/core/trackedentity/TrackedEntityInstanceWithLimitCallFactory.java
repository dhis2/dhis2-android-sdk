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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkModelStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Reusable
public final class TrackedEntityInstanceWithLimitCallFactory {

    private final Resource.Type resourceType = Resource.Type.TRACKED_ENTITY_INSTANCE;

    private final RxAPICallExecutor rxCallExecutor;
    private final ResourceHandler resourceHandler;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final LinkModelStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore;
    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final DHISVersionManager versionManager;


    private final TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory relationshipDownloadCallFactory;
    private final TrackedEntityInstancePersistenceCallFactory persistenceCallFactory;
    private final TrackedEntityInstancesEndpointCallFactory endpointCallFactory;

    // TODO use scheduler for parallel download
    // private final Scheduler teiDownloadScheduler = Schedulers.from(Executors.newFixedThreadPool(6));

    @Inject
    TrackedEntityInstanceWithLimitCallFactory(
            RxAPICallExecutor rxCallExecutor,
            ResourceHandler resourceHandler,
            UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            LinkModelStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore,
            ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory relationshipDownloadCallFactory,
            TrackedEntityInstancePersistenceCallFactory persistenceCallFactory,
            DHISVersionManager versionManager,
            TrackedEntityInstancesEndpointCallFactory endpointCallFactory) {
        this.rxCallExecutor = rxCallExecutor;
        this.resourceHandler = resourceHandler;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.organisationUnitProgramLinkStore = organisationUnitProgramLinkStore;
        this.systemInfoRepository = systemInfoRepository;
        this.versionManager = versionManager;

        this.relationshipDownloadCallFactory = relationshipDownloadCallFactory;
        this.persistenceCallFactory = persistenceCallFactory;
        this.endpointCallFactory = endpointCallFactory;
    }

    public Observable<D2Progress> download(final int teiLimit, final boolean limitByOrgUnit, boolean limitByProgram) {
        Observable<D2Progress> observable = Observable.defer(() -> {
            D2ProgressManager progressManager = new D2ProgressManager(null);
            if (userOrganisationUnitLinkStore.count() == 0) {
                return Observable.just(
                        progressManager.increaseProgress(TrackedEntityInstance.class, true));
            } else {
                BooleanWrapper allOkay = new BooleanWrapper(true);

                return Observable.concat(
                        downloadSystemInfo(progressManager),
                        downloadTeis(progressManager, teiLimit, limitByOrgUnit, limitByProgram, allOkay),
                        downloadRelationshipTeis(progressManager),
                        updateResource(progressManager, allOkay)
                );
            }
        });

        return rxCallExecutor.wrapObservableTransactionally(observable, true);

    }

    private Observable<D2Progress> downloadSystemInfo(D2ProgressManager progressManager) {
        return systemInfoRepository.download()
                .toSingle(() -> progressManager.increaseProgress(SystemInfo.class, false))
                .toObservable();
    }

    private Observable<D2Progress> downloadTeis(D2ProgressManager progressManager,
                                                int teiLimit,
                                                boolean limitByOrgUnit,
                                                boolean limitByProgram,
                                                BooleanWrapper allOkay) {

        int pageSize = TeiQuery.builder().build().pageSize();
        List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, teiLimit);

        Observable<List<TrackedEntityInstance>> teiDownloadObservable =
                Observable.fromIterable(getTeiQueryBuilders(limitByOrgUnit, limitByProgram))
                        .flatMap(teiQueryBuilder -> {
                            return getTrackedEntityInstancesWithPaging(teiQueryBuilder, pagingList, allOkay);
                            // TODO .subscribeOn(teiDownloadScheduler);
                        });

        return teiDownloadObservable.map(
                teiList -> {
                    persistenceCallFactory.getCall(teiList).call();
                    return progressManager.increaseProgress(TrackedEntityInstance.class, false);
                });
    }

    private Observable<D2Progress> downloadRelationshipTeis(D2ProgressManager progressManager) {
        Observable<List<TrackedEntityInstance>> observable = versionManager.is2_29()
                ? Observable.just(Collections.emptyList())
                : relationshipDownloadCallFactory.downloadAndPersist().toObservable();

        return observable.map(
                trackedEntityInstances -> progressManager.increaseProgress(TrackedEntityInstance.class, true));
    }

    private List<TeiQuery.Builder> getTeiQueryBuilders(boolean limitByOrgUnit, boolean limitByProgram) {

        String lastUpdated = resourceHandler.getLastUpdated(resourceType);

        List<TeiQuery.Builder> builders = new ArrayList<>();
        if (limitByOrgUnit) {
            if (limitByProgram) {
                for (OrganisationUnitProgramLink link : organisationUnitProgramLinkStore.selectAll()) {
                    builders.add(getTeiBuilderForOrgUnit(lastUpdated, link.organisationUnit()).program(link.program()));
                }
            } else {
                for (String orgUnitUid: getOrgUnitUids()) {
                    builders.add(getTeiBuilderForOrgUnit(lastUpdated, orgUnitUid));
                }
            }
        } else {
            if (limitByProgram) {
                Set<String> programs = new HashSet<>();
                for (OrganisationUnitProgramLink link : organisationUnitProgramLinkStore.selectAll()) {
                    programs.add(link.program());
                }
                for (String program : programs) {
                    builders.add(getTeiBuilderForRoot(lastUpdated).program(program));
                }
            } else {
                builders.add(getTeiBuilderForRoot(lastUpdated));
            }
        }
        return builders;
    }

    private TeiQuery.Builder getTeiBuilderForRoot(String lastUpdated) {
        return TeiQuery.builder()
                .lastUpdatedStartDate(lastUpdated)
                .orgUnits(userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids())
                .ouMode(OrganisationUnitMode.DESCENDANTS);
    }

    private TeiQuery.Builder getTeiBuilderForOrgUnit(String lastUpdated, String orgUnitUid) {
        return TeiQuery.builder()
                .lastUpdatedStartDate(lastUpdated)
                .orgUnits(Collections.singleton(orgUnitUid));
    }

    private Observable<List<TrackedEntityInstance>> getTrackedEntityInstancesWithPaging(
            TeiQuery.Builder teiQueryBuilder, List<Paging> pagingList, BooleanWrapper allOkay) {
        Observable<Paging> pagingObservable = Observable.fromIterable(pagingList);
        return pagingObservable
                .flatMapSingle(paging -> {
                    teiQueryBuilder.page(paging.page()).pageSize(paging.pageSize());
                    return endpointCallFactory.getCall(teiQueryBuilder.build()).map(payload ->
                            new TeiListWithPaging(true, limitTeisForPage(payload.items(), paging), paging))
                            .onErrorResumeNext((err) -> {
                                allOkay.set(false);
                                return Single.just(new TeiListWithPaging(false, Collections.emptyList(), paging));
                            });
                })
                .takeUntil(res -> res.isSuccess && (res.paging.isLastPage() ||
                        !res.paging.isLastPage() && res.teiList.size() < res.paging.pageSize()))
                .map(tuple -> tuple.teiList);
    }

    private List<TrackedEntityInstance> limitTeisForPage(List<TrackedEntityInstance> pageTrackedEntityInstances,
                                                         Paging paging) {
        if (paging.isLastPage()
                && pageTrackedEntityInstances.size() > paging.previousItemsToSkipCount()) {
            int toIndex = pageTrackedEntityInstances.size() <
                    paging.pageSize() - paging.posteriorItemsToSkipCount() ?
                    pageTrackedEntityInstances.size() :
                    paging.pageSize() - paging.posteriorItemsToSkipCount();

            return pageTrackedEntityInstances.subList(paging.previousItemsToSkipCount(), toIndex);
        } else {
            return pageTrackedEntityInstances;
        }
    }

    private Set<String> getOrgUnitUids() {
        List<UserOrganisationUnitLink> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll();

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLink userOrganisationUnitLink : userOrganisationUnitLinks) {
            if (userOrganisationUnitLink.organisationUnitScope().equals(
                    OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(userOrganisationUnitLink.organisationUnit());
            }
        }

        return organisationUnitUids;
    }

    private Observable<D2Progress> updateResource(D2ProgressManager progressManager, BooleanWrapper allOkay) {
        return Single.fromCallable(() -> {
            if (allOkay.get()) {
                resourceHandler.handleResource(resourceType);
            }
            return progressManager.increaseProgress(TrackedEntityInstance.class, true);
        }).toObservable();
    }

    private static class TeiListWithPaging {
        final boolean isSuccess;
        final List<TrackedEntityInstance> teiList;
        final Paging paging;

        TeiListWithPaging(boolean isSuccess, List<TrackedEntityInstance> teiList, Paging paging) {
            this.isSuccess = isSuccess;
            this.teiList = teiList;
            this.paging = paging;
        }
    }

    private static class BooleanWrapper {
        private boolean value;

        BooleanWrapper(boolean value) {
            this.value = value;
        }

        boolean get() {
            return value;
        }

        void set(boolean value) {
            this.value = value;
        }
    }
}