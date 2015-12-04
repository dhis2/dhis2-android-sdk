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

package org.hisp.dhis.android.sdk.common.base;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.java.sdk.common.IStateStore;
import org.hisp.dhis.java.sdk.models.common.base.IModel;
import org.hisp.dhis.java.sdk.models.common.state.Action;

import static org.hisp.dhis.java.sdk.utils.Preconditions.isNull;

public class AbsDataStore<ModelType extends IModel, DatabaseEntityType
        extends IModel & Model> extends AbsStore<ModelType, DatabaseEntityType> {

    private final IStateStore stateStore;

    public AbsDataStore(IMapper<ModelType, DatabaseEntityType> mapper, IStateStore stateStore) {
        super(mapper);

        this.stateStore = isNull(stateStore, "stateStore object must not be null");
    }

    @Override
    public boolean insert(ModelType object) {
        if (super.insert(object)) {
            stateStore.saveActionForModel(object, Action.SYNCED);
            return true;
        }
        return false;
    }

    @Override
    public boolean save(ModelType object) {
        if (super.save(object)) {
            Action action = stateStore.queryActionForModel(object);
            if (action == null) {
                action = Action.SYNCED;
            }
            stateStore.saveActionForModel(object, action);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(ModelType object) {
        if (super.delete(object)) {
            stateStore.deleteActionForModel(object);
            return true;
        }

        return false;
    }
}
