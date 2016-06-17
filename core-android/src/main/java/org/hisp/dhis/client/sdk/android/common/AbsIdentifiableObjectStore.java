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

package org.hisp.dhis.client.sdk.android.common;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.BaseIdentifiableObjectFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.BaseModelFlow;
import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;

import java.util.List;
import java.util.Set;

public abstract class AbsIdentifiableObjectStore<ModelType extends IdentifiableObject,
        DatabaseEntityType extends Model & IdentifiableObject> extends AbsStore<ModelType,
        DatabaseEntityType> implements IdentifiableObjectStore<ModelType> {

    public AbsIdentifiableObjectStore(Mapper<ModelType, DatabaseEntityType> mapper) {
        super(mapper);
    }

    @Override
    public ModelType queryById(long id) {
        List<DatabaseEntityType> databaseEntities = new Select()
                .from(getMapper().getDatabaseEntityTypeClass())
                .where(Condition.column(NameAlias.builder(BaseModelFlow
                        .COLUMN_ID).build()).is(id))
                .queryList();

        if (databaseEntities != null && !databaseEntities.isEmpty()) {
            return getMapper().mapToModel(databaseEntities.get(0));
        }
        return null;

    }

    @Override
    public ModelType queryByUid(String uid) {
        List<DatabaseEntityType> databaseEntities = new Select()
                .from(getMapper().getDatabaseEntityTypeClass())
                .where(Condition.column(NameAlias.builder(BaseIdentifiableObjectFlow
                        .COLUMN_UID).build()).is(uid))
                .queryList();

        if (databaseEntities != null && !databaseEntities.isEmpty()) {
            return getMapper().mapToModel(databaseEntities.get(0));
        } else {
            return null;
        }
    }

    @Override
    public List<ModelType> queryByUids(Set<String> uids) {
        if (uids != null && !uids.isEmpty()) {
            List<DatabaseEntityType> databaseEntities = query(uids);

            if (databaseEntities != null && !databaseEntities.isEmpty()) {
                return getMapper().mapToModels(databaseEntities);
            }
        }

        return null;
    }

    @Override
    public boolean areStored(Set<String> uids) {
        List<DatabaseEntityType> databaseEntities = query(uids);
        return ModelUtils.toUidSet(databaseEntities).equals(uids);
    }

    private List<DatabaseEntityType> query(Set<String> uids) {
        if (uids != null && !uids.isEmpty()) {
            return new Select()
                    .from(getMapper().getDatabaseEntityTypeClass())
                    .where(Condition.column(NameAlias.builder(BaseIdentifiableObjectFlow
                            .COLUMN_UID).build()).in(uids))
                    .queryList();
        }

        return null;
    }
}
