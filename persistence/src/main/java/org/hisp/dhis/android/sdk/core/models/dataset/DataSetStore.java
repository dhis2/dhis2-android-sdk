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

package org.hisp.dhis.android.sdk.core.models.dataset;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.models.dataset.DataSet;
import org.hisp.dhis.android.sdk.models.dataset.IDataSetStore;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.sdk.core.models.flow.DataSet$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.DataSet$Flow$Table;
import org.hisp.dhis.android.sdk.core.models.flow.OrganisationUnit$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.UnitToDataSetRelationShip$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.UnitToDataSetRelationShip$Flow$Table;

import java.util.ArrayList;
import java.util.List;

public final class DataSetStore implements IDataSetStore {

    @Override
    public List<OrganisationUnit> query(DataSet dataSet) {
        List<UnitToDataSetRelationShip$Flow> relationShipFlows = new Select()
                .from(UnitToDataSetRelationShip$Flow.class)
                .where(Condition.column(UnitToDataSetRelationShip$Flow$Table
                        .DATASET_DATASET).is(dataSet.getUId()))
                .queryList();

        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        for (UnitToDataSetRelationShip$Flow relationShipFlow : relationShipFlows) {
            OrganisationUnit$Flow organisationUnitFlow = relationShipFlow.getOrganisationUnit();
            organisationUnits.add(OrganisationUnit$Flow.toModel(organisationUnitFlow));
        }

        return organisationUnits;
    }

    @Override
    public DataSet queryById(long id) {
        DataSet$Flow dataSetFlow = new Select()
                .from(DataSet$Flow.class)
                .where(Condition.column(DataSet$Flow$Table.ID).is(id))
                .querySingle();
        return DataSet$Flow.toModel(dataSetFlow);
    }

    @Override
    public DataSet queryByUid(String uid) {
        DataSet$Flow dataSetFlow = new Select()
                .from(DataSet$Flow.class)
                .where(Condition.column(DataSet$Flow$Table.UID).is(uid))
                .querySingle();
        return DataSet$Flow.toModel(dataSetFlow);
    }

    @Override
    public List<DataSet> queryAll() {
        List<DataSet$Flow> dataSetFlows = new Select()
                .from(DataSet$Flow.class).queryList();
        return DataSet$Flow.toModels(dataSetFlows);
    }

    @Override
    public boolean insert(DataSet object) {
        DataSet$Flow dataSetFlow = DataSet$Flow.fromModel(object);
        dataSetFlow.insert();

        object.setId(dataSetFlow.getId());
        return true;
    }

    @Override
    public boolean update(DataSet object) {
        DataSet$Flow dataSetFlow = DataSet$Flow.fromModel(object);
        dataSetFlow.update();

        return true;
    }

    @Override
    public boolean save(DataSet object) {
        DataSet$Flow dataSetFlow = DataSet$Flow.fromModel(object);
        dataSetFlow.save();

        object.setId(dataSetFlow.getId());
        return true;
    }

    @Override
    public boolean delete(DataSet object) {
        DataSet$Flow dataSetFlow = DataSet$Flow.fromModel(object);
        dataSetFlow.delete();
        return true;
    }
}
