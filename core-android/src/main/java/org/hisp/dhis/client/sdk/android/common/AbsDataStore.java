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

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.models.common.base.Model;
import org.hisp.dhis.client.sdk.models.common.state.Action;

public class AbsDataStore<ModelType extends Model, DataBaseEntityType
        extends Model & com.raizlabs.android.dbflow.structure.Model>
        extends AbsStore<ModelType, DataBaseEntityType> {

    private final StateStore stateStore;

    public AbsDataStore(Mapper<ModelType, DataBaseEntityType> mapper,
                        StateStore stateStore) {
        super(mapper);
        this.stateStore = stateStore;
    }

    @Override
    public boolean insert(ModelType object) {
        return saveActionForModel(super.insert(object), object);
    }

    @Override
    public boolean update(ModelType object) {
        return saveActionForModel(super.update(object), object);
    }

    @Override
    public boolean save(ModelType object) {
        return saveActionForModel(super.save(object), object);
    }

    @Override
    public boolean delete(ModelType object) {
        return deleteActionForModel(super.delete(object), object);
    }

    @Override
    public boolean deleteAll() {
        return deleteAllActionsForModelType(super.deleteAll());
    }

    private boolean saveActionForModel(boolean isModelSaved, ModelType model) {
        return isModelSaved && (stateStore.queryActionForModel(model) != null ||
                stateStore.saveActionForModel(model, Action.SYNCED));
    }

    private boolean deleteActionForModel(boolean isDeleted, ModelType model) {
        return isDeleted && stateStore.deleteActionForModel(model);
    }

    private boolean deleteAllActionsForModelType(boolean areModelsRemoved) {
        return areModelsRemoved && stateStore.deleteActionsForModelType(
                getMapper().getModelTypeClass());
    }
}
