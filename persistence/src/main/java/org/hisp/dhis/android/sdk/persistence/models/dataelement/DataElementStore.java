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

package org.hisp.dhis.android.sdk.persistence.models.dataelement;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.flow.DataElement$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DataElement$Flow$Table;

import java.util.List;

public final class DataElementStore implements IIdentifiableObjectStore<DataElement> {

    public DataElementStore() {
        //empty constructor
    }

    @Override
    public boolean insert(DataElement object) {
        DataElement$Flow dataElementFlow = DataElement$Flow.fromModel(object);
        dataElementFlow.insert();

        object.setId(dataElementFlow.getId());
        return true;
    }

    @Override
    public boolean update(DataElement object) {
        DataElement$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(DataElement object) {
        DataElement$Flow dataElementFlow =
                DataElement$Flow.fromModel(object);
        dataElementFlow.save();

        object.setId(dataElementFlow.getId());
        return true;
    }

    @Override
    public boolean delete(DataElement object) {
        DataElement$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<DataElement> queryAll() {
        List<DataElement$Flow> dataElementFlows = new Select()
                .from(DataElement$Flow.class)
                .queryList();
        return DataElement$Flow.toModels(dataElementFlows);
    }

    @Override
    public DataElement queryById(long id) {
        DataElement$Flow dataElementFlow = new Select()
                .from(DataElement$Flow.class)
                .where(Condition.column(DataElement$Flow$Table.ID).is(id))
                .querySingle();
        return DataElement$Flow.toModel(dataElementFlow);
    }

    @Override
    public DataElement queryByUid(String uid) {
        DataElement$Flow dataElementFlow = new Select()
                .from(DataElement$Flow.class)
                .where(Condition.column(DataElement$Flow$Table.UID).is(uid))
                .querySingle();
        return DataElement$Flow.toModel(dataElementFlow);
    }
}
