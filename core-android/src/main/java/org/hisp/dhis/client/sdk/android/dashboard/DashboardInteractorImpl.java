/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.client.sdk.android.dashboard;

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardController;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardService;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class DashboardInteractorImpl implements DashboardInteractor {
    private final DashboardService dashboardService;
    private final DashboardController dashboardController;

    public DashboardInteractorImpl(DashboardService dashboardService,
                                   DashboardController dashboardController) {
        this.dashboardService = dashboardService;
        this.dashboardController = dashboardController;
    }

    @Override
    public Observable<List<Dashboard>> syncDashboards() {
        return syncDashboards(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<Dashboard>> syncDashboards(final SyncStrategy strategy) {
        return Observable.create(new DefaultOnSubscribe<List<Dashboard>>() {
            @Override
            public List<Dashboard> call() {
                dashboardController.syncDashboards(strategy);
                return dashboardService.list();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final Dashboard dashboard) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return dashboardService.save(dashboard);
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Dashboard dashboard) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return dashboardService.remove(dashboard);
            }
        });
    }

    @Override
    public Observable<Dashboard> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<Dashboard>() {
            @Override
            public Dashboard call() {
                return dashboardService.get(id);
            }
        });
    }

    @Override
    public Observable<Dashboard> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<Dashboard>() {
            @Override
            public Dashboard call() {
                return dashboardService.get(uid);
            }
        });
    }

    @Override
    public Observable<List<Dashboard>> list() {
        return Observable.create(new DefaultOnSubscribe<List<Dashboard>>() {
            @Override
            public List<Dashboard> call() {
                return dashboardService.list();
            }
        });
    }

    @Override
    public Observable<List<Dashboard>> listByActions(final Set<Action> actionSet) {
        return Observable.create(new DefaultOnSubscribe<List<Dashboard>>() {
            @Override
            public List<Dashboard> call() {
                return dashboardService.listByActions(actionSet);
            }
        });
    }

    @Override
    public Observable<List<Dashboard>>pull() {
        return pull(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<Dashboard>> pull(final SyncStrategy syncStrategy) {
        return Observable.create(new DefaultOnSubscribe<List<Dashboard>>() {
            @Override
            public List<Dashboard> call() {
                dashboardController.pull(syncStrategy);
                return dashboardService.list();
            }
        });
    }

    @Override
    public Observable<Integer> countItems(final Dashboard dashboard) {
        return Observable.create(new DefaultOnSubscribe<Integer>() {
            @Override
            public Integer call() {
                return dashboardService.countItems(dashboard);
            }
        });
    }

    @Override
    public Observable<Boolean> addContent(final Dashboard dashboard,
                                          final DashboardContent content) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return dashboardService.addContent(dashboard, content);
            }
        });
    }
}
