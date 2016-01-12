/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models.meta;

import com.raizlabs.android.dbflow.structure.BaseModel;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * This class is intended to implement partial
 * functionality of ContentProviderOperation for DbFlow.
 */
public final class DbOperation {
    private final BaseModel.Action mAction;
    private final BaseModel mModel;

    private DbOperation(BaseModel.Action action, BaseModel model) {
        mModel = isNull(model, "BaseModel object must nto be null,");
        mAction = isNull(action, "BaseModel.Action object must not be null");
    }

    public static <T extends BaseModel> DbOperation insert(T model) {
        return new DbOperation(BaseModel.Action.INSERT, model);
    }

    public static <T extends BaseModel> DbOperation update(T model) {
        return new DbOperation(BaseModel.Action.UPDATE, model);
    }

    public static <T extends BaseModel> DbOperation save(T model) {
        return new DbOperation(BaseModel.Action.SAVE, model);
    }

    public static <T extends BaseModel> DbOperation delete(T model) {
        return new DbOperation(BaseModel.Action.DELETE, model);
    }

    public BaseModel getModel() {
        return mModel;
    }

    public BaseModel.Action getAction() {
        return mAction;
    }
}
