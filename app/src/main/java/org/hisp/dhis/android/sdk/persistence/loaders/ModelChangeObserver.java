/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.persistence.loaders;

import android.util.Log;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public class ModelChangeObserver<ModelClass extends Model> implements FlowContentObserver.OnModelStateChangedListener {
    private static final String TAG = ModelChangeObserver.class.getSimpleName();

    private final DbLoader<?> mLoader;
    private final Class<ModelClass> mModelClass;
    private final FlowContentObserver mObserver;

    public ModelChangeObserver(Class<ModelClass> modelClass, DbLoader<?> loader) {
        mModelClass = isNull(modelClass, "Class<ModelClass> object must not be null");
        mLoader = isNull(loader, "DbLoader must not be null");
        mObserver = new FlowContentObserver();
    }

    public void registerObserver() {
        mObserver.registerForContentChanges(
                mLoader.getContext(), mModelClass
        );
        mObserver.addModelChangeListener(this);
    }

    public void unregisterObserver() {
        mObserver.unregisterForContentChanges(mLoader.getContext());
        mObserver.removeModelChangeListener(this);
    }

    @Override
    public void onModelStateChanged(Class<? extends Model> aClass, BaseModel.Action action) {
        Log.d(TAG, "onModelStateChanged() " + aClass.getSimpleName() + ": " + action);

        if (!mLoader.isLoading()) {
            mLoader.onContentChanged();
        }
    }
}