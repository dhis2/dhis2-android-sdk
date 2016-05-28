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

package org.hisp.dhis.client.sdk.android.dataset;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataSetFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.core.dataset.DataSetStore;
import org.hisp.dhis.client.sdk.models.dataset.DataSet;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.Collection;
import java.util.List;

public final class DataSetStoreImpl extends AbsIdentifiableObjectStore<DataSet, DataSetFlow>
        implements DataSetStore {

    private final Mapper<OrganisationUnit, OrganisationUnitFlow> organisationUnitMapper;

    public DataSetStoreImpl(Mapper<DataSet, DataSetFlow> mapper, Mapper<OrganisationUnit,
                OrganisationUnitFlow> organisationUnitMapper) {
        super(mapper);
        this.organisationUnitMapper = organisationUnitMapper;
    }

    @Override
    public List<OrganisationUnit> query(Collection<DataSet> dataSets) {
//        List<UnitToDataSetRelationShip_Flow> relationShipFlows = new Select()
//                .from(UnitToDataSetRelationShip_Flow.class)
//                .where(Condition.column(UnitToDataSetRelationShip_Flow_Table
//                        .DATASET_DATASET).is(dataSet.getUId()))
//                .queryList();

//        List<OrganisationUnit> organisationUnits = new ArrayList<>();
//        for (UnitToDataSetRelationShip_Flow relationShipFlow : relationShipFlows) {
//            OrganisationUnit_Flow organisationUnitFlow = relationShipFlow.getOrganisationUnit();
//            organisationUnits.add(organisationUnitMapper.mapToModel(organisationUnitFlow));
//        }

//        return organisationUnits;
        return null;
    }
}
