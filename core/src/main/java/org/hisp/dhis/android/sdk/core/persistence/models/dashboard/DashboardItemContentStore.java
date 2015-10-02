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

package org.hisp.dhis.android.sdk.core.persistence.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition.CombinedCondition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItemContent$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItemContent$Flow$Table;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemContentStore;

import java.util.List;

import static com.raizlabs.android.dbflow.sql.builder.Condition.column;

public final class DashboardItemContentStore implements IDashboardItemContentStore {

    public DashboardItemContentStore() {
        // empty constructor
    }

    @Override
    public void insert(DashboardContent object) {
        DashboardItemContent$Flow flowModel
                = DashboardItemContent$Flow.fromModel(object);
        flowModel.insert();

        object.setId(flowModel.getId());
    }

    @Override
    public void update(DashboardContent object) {
        DashboardItemContent$Flow.fromModel(object).update();
    }

    @Override
    public void save(DashboardContent object) {
        DashboardItemContent$Flow flowModel
                = DashboardItemContent$Flow.fromModel(object);
        flowModel.save();

        object.setId(flowModel.getId());
    }

    @Override
    public void delete(DashboardContent object) {
        DashboardItemContent$Flow.fromModel(object).delete();
    }

    @Override
    public List<DashboardContent> queryAll() {
        List<DashboardItemContent$Flow> flows = new Select()
                .from(DashboardItemContent$Flow.class)
                .queryList();
        return DashboardItemContent$Flow.toModels(flows);
    }

    @Override
    public DashboardContent queryById(long id) {
        DashboardItemContent$Flow dashboardItemContentFlow = new Select()
                .from(DashboardItemContent$Flow.class)
                .where(column(DashboardItemContent$Flow$Table
                        .ID).is(id))
                .querySingle();
        return DashboardItemContent$Flow.toModel(dashboardItemContentFlow);
    }

    @Override
    public DashboardContent queryByUid(String uid) {
        DashboardItemContent$Flow dashboardItemContentFlow = new Select()
                .from(DashboardItemContent$Flow.class)
                .where(column(DashboardItemContent$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return DashboardItemContent$Flow.toModel(dashboardItemContentFlow);
    }

    @Override
    public List<DashboardContent> queryByTypes(List<String> types) {
        CombinedCondition generalCondition = CombinedCondition.begin(
                column(DashboardItemContent$Flow$Table.TYPE).isNotNull());
        CombinedCondition columnConditions = null;
        for (String type : types) {
            if (columnConditions == null) {
                columnConditions = CombinedCondition
                        .begin(column(DashboardItemContent$Flow$Table.TYPE).is(type));
            } else {
                columnConditions = columnConditions
                        .or(column(DashboardItemContent$Flow$Table.TYPE).is(type));
            }
        }
        generalCondition.and(columnConditions);

        List<DashboardItemContent$Flow> resources = new Select()
                .from(DashboardItemContent$Flow.class)
                .where(generalCondition)
                .queryList();
        return DashboardItemContent$Flow.toModels(resources);
    }
}
