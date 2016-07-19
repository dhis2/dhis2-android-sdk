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
import org.hisp.dhis.client.sdk.core.dashboard.DashboardItemService;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;

import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;

import java.util.List;

import rx.Observable;

public class DashboardItemInteractorImpl implements DashboardItemInteractor {
    private final DashboardItemService mDashboardItemService;

    public DashboardItemInteractorImpl(DashboardItemService dashboardItemService) {
        mDashboardItemService = dashboardItemService;
    }


    @Override
    public Observable<DashboardItem> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<DashboardItem>() {
            @Override
            public DashboardItem call() {
                return mDashboardItemService.get(id);
            }
        });
    }

    @Override
    public Observable<DashboardItem> get(final String uId) {
        return Observable.create(new DefaultOnSubscribe<DashboardItem>() {
            @Override
            public DashboardItem call() {
                return mDashboardItemService.get(uId);
            }
        });
    }

    @Override
    public Observable<List<DashboardItem>> list() {
        return Observable.create(new DefaultOnSubscribe<List<DashboardItem>>() {
            @Override
            public List<DashboardItem> call() {
                return mDashboardItemService.list();
            }
        });
    }

    @Override
    public Observable<DashboardItem> create(final Dashboard object, final String type) {
        return Observable.create(new DefaultOnSubscribe<DashboardItem>() {
            @Override
            public DashboardItem call() {
                return mDashboardItemService.create(object,type);
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final DashboardItem object) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return mDashboardItemService.remove(object);
            }
        });
    }

    @Override
    public Observable<List<DashboardItem>> list(final String uId) {
        return Observable.create(new DefaultOnSubscribe<List<DashboardItem>>() {
            @Override
            public List<DashboardItem> call() {
                return mDashboardItemService.list(uId);
            }
        });
    }
}
