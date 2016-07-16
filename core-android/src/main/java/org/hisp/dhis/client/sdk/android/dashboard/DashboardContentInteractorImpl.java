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
import org.hisp.dhis.client.sdk.core.dashboard.DashboardContentController;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardContentService;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;

import java.util.List;
import java.util.Set;

import rx.Observable;

public class DashboardContentInteractorImpl implements DashboardContentInteractor {
    private final DashboardContentService dashboardContentService;
    private final DashboardContentController dashboardContentController;

    public DashboardContentInteractorImpl(DashboardContentService dashboardContentService,
                                          DashboardContentController dashboardContentController) {
        this.dashboardContentService = dashboardContentService;
        this.dashboardContentController = dashboardContentController;
    }


    @Override
    public Observable<List<DashboardContent>> syncDashboardContent() {
        return syncDashboardContent(SyncStrategy.DEFAULT);
    }

    @Override
    public Observable<List<DashboardContent>> syncDashboardContent(final SyncStrategy strategy) {
        return Observable.create(new DefaultOnSubscribe<List<DashboardContent>>() {
            @Override
            public List<DashboardContent> call() {
                dashboardContentController.syncDashboardContent(strategy);
                return dashboardContentService.list();
            }
        });
    }

    @Override
    public Observable<DashboardContent> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<DashboardContent>() {
            @Override
            public DashboardContent call() {
                return dashboardContentService.get(id);
            }
        });
    }

    @Override
    public Observable<DashboardContent> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<DashboardContent>() {
            @Override
            public DashboardContent call() {
                return dashboardContentService.get(uid);
            }
        });
    }

    @Override
    public Observable<List<DashboardContent>> list() {
        return Observable.create(new DefaultOnSubscribe<List<DashboardContent>>() {
            @Override
            public List<DashboardContent> call() {
                return dashboardContentService.list();
            }
        });
    }

    @Override
    public Observable<List<DashboardContent>> list(final Set<String> types) {
        return Observable.create(new DefaultOnSubscribe<List<DashboardContent>>() {
            @Override
            public List<DashboardContent> call() {
                return dashboardContentService.list(types);
            }
        });
    }
}
