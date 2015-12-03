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

package org.hisp.dhis.android.sdk.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.Condition.CombinedCondition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.common.base.IMapper;
import org.hisp.dhis.android.sdk.flow.DashboardContent$Flow$Table;
import org.hisp.dhis.java.sdk.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.android.sdk.flow.DashboardContent$Flow;

import java.util.List;

public final class DashboardContentStore extends AbsIdentifiableObjectStore<DashboardContent,
        DashboardContent$Flow> implements IDashboardItemContentStore {

    public DashboardContentStore(IMapper<DashboardContent, DashboardContent$Flow> contentMapper) {
        super(contentMapper);
    }

    @Override
    public List<DashboardContent> queryByTypes(List<String> types) {
        CombinedCondition generalCondition = CombinedCondition.begin(
                Condition.column(DashboardContent$Flow$Table.TYPE).isNotNull());
        CombinedCondition columnConditions = null;
        for (String type : types) {
            if (columnConditions == null) {
                columnConditions = CombinedCondition
                        .begin(Condition.column(DashboardContent$Flow$Table.TYPE).is(type));
            } else {
                columnConditions = columnConditions
                        .or(Condition.column(DashboardContent$Flow$Table.TYPE).is(type));
            }
        }
        generalCondition.and(columnConditions);

        List<DashboardContent$Flow> resources = new Select()
                .from(DashboardContent$Flow.class)
                .where(generalCondition)
                .queryList();
        return getMapper().mapToModels(resources);
    }
}
