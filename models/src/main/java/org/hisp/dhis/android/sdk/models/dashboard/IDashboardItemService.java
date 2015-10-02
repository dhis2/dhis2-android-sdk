/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IService;
import org.hisp.dhis.android.sdk.models.common.repository.IAdd;
import org.hisp.dhis.android.sdk.models.common.repository.IGet;
import org.hisp.dhis.android.sdk.models.common.repository.IGetUid;
import org.hisp.dhis.android.sdk.models.common.repository.IList;
import org.hisp.dhis.android.sdk.models.common.repository.IRemove;
import org.hisp.dhis.android.sdk.models.common.repository.IUpdate;

import java.util.List;

public interface IDashboardItemService extends IService, IRemove<DashboardItem>,
        IList<DashboardItem>, IGet<DashboardItem>, IGetUid<DashboardItem> {
    // DashboardItem add(Dashboard dashboard, DashboardContent content);

    // boolean remove(DashboardItem dashboardItem);

    // List<DashboardItem> list();

    List<DashboardItem> list(Dashboard dashboard);

    // DashboardItem get(long id);

    // DashboardItem get(String uid);

    // int getContentCount(DashboardItem dashboardItem);
}