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

package org.hisp.dhis.client.sdk.android.common.base;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.client.sdk.android.flow.BaseIdentifiableObjectFlow;
import org.hisp.dhis.client.sdk.android.flow.BaseModelFlow;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.common.base.IModel;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;

public abstract class AbsIdentifiableObjectDataStore<ModelType extends IdentifiableObject,
        DatabaseEntityType extends Model & IModel> extends AbsDataStore<ModelType,
        DatabaseEntityType> implements IIdentifiableObjectStore<ModelType> {

    public AbsIdentifiableObjectDataStore(IMapper<ModelType, DatabaseEntityType> mapper,
                                          IStateStore stateStore) {
        super(mapper, stateStore);
    }

    @Override
    public ModelType queryById(long id) {
        DatabaseEntityType databaseEntity = new Select()
                .from(getMapper().getDatabaseEntityTypeClass())
                .where(Condition.column(new NameAlias(BaseModelFlow
                        .COLUMN_ID)).is(id))
                .querySingle();
        return getMapper().mapToModel(databaseEntity);
    }

    @Override
    public ModelType queryByUid(String uid) {
        DatabaseEntityType databaseEntity = new Select()
                .from(getMapper().getDatabaseEntityTypeClass())
                .where(Condition.column(new NameAlias(BaseIdentifiableObjectFlow
                        .COLUMN_UID)).is(uid)).querySingle();
        return getMapper().mapToModel(databaseEntity);
    }
}
