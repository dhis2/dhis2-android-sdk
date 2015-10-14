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

package org.hisp.dhis.android.sdk.core.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.utils.Preconditions;
import org.hisp.dhis.android.sdk.core.models.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.core.models.common.base.IMapper;
import org.hisp.dhis.android.sdk.core.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.DashboardElement$Flow$Table;

import java.util.List;

public class DashboardElementStore extends AbsIdentifiableObjectStore<DashboardElement, DashboardElement$Flow> implements IDashboardElementStore {

    public DashboardElementStore(IMapper<DashboardElement, DashboardElement$Flow> mapper) {
        super(mapper);
    }

    @Override
    public List<DashboardElement> queryByDashboardItem(DashboardItem dashboardItem) {
        Preconditions.isNull(dashboardItem, "dashboard item must not be null");

        List<DashboardElement$Flow> elementFlows = new Select()
                .from(DashboardElement$Flow.class)
                .where(Condition.column(DashboardElement$Flow$Table
                        .DASHBOARDITEM_DASHBOARDITEM).is(dashboardItem.getId()))
                .queryList();

        // converting flow models to Dashboard
        return getMapper().mapToModels(elementFlows);
    }
}
