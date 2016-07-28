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

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardElementFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardItemFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardItemFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsDataStore;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.android.common.AbsStore;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardElementStore;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class DashboardElementStoreImpl extends AbsDataStore<DashboardElement,
        DashboardElementFlow> implements DashboardElementStore {

    public DashboardElementStoreImpl(StateStore stateStore) {
        super(DashboardElementFlow.MAPPER, stateStore);
    }

    @Override
    public List<DashboardElement> query(DashboardItem dashboardItem) {
        isNull(dashboardItem, "dashboard item must not be null");
        List<DashboardElementFlow> elementFlows = new Select()
                .from(DashboardElementFlow.class)
                .where(DashboardElementFlow_Table.dashboardItem.is(dashboardItem.getUId()))
                .queryList();
        return getMapper().mapToModels(elementFlows);
    }

    @Override
    public DashboardElement getDashboardElement(long dashboardElementId) {
        DashboardElementFlow dashboardElementFlow = new Select()
                .from(DashboardElementFlow.class)
                .where(DashboardElementFlow_Table.id.is(dashboardElementId))
                .querySingle();
        return getMapper().mapToModel(dashboardElementFlow);
    }

    @Override
    public boolean insert(DashboardElement dashboardElement) {
        Log.d("UidofElement", dashboardElement.getUId());
        Log.d("UidofElement'sItem", dashboardElement.getDashboardItem().getUId());
        boolean isSuccess = super.insert(dashboardElement);

        return isSuccess;
    }

    @Override
    public boolean update(DashboardElement dashboardElement) {
        boolean isSuccess = super.update(dashboardElement);

        return isSuccess;
    }

    @Override
    public boolean save(DashboardElement dashboardElement) {
        boolean isSuccess = super.save(dashboardElement);

        return isSuccess;
    }

}
