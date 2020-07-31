/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.datavalue.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
public final class DataValuePostCall {

    private final DataValueService dataValueService;
    private final DataValueImportHandler dataValueImportHandler;
    private final APICallExecutor apiCallExecutor;
    private final DataValueStore dataValueStore;

    @Inject
    DataValuePostCall(@NonNull DataValueService dataValueService,
                      @NonNull DataValueImportHandler dataValueImportHandler,
                      @NonNull APICallExecutor apiCallExecutor,
                      @NonNull DataValueStore dataValueStore) {

        this.dataValueService = dataValueService;
        this.dataValueImportHandler = dataValueImportHandler;
        this.apiCallExecutor = apiCallExecutor;
        this.dataValueStore = dataValueStore;
    }

    public Observable<D2Progress> uploadDataValues(List<DataValue> dataValues) {
        return Observable.defer(() -> {
            if (dataValues.isEmpty()) {
                return Observable.empty();
            } else {
                D2ProgressManager progressManager = new D2ProgressManager(1);

                return Observable.create(emitter -> {
                    markObjectsAs(dataValues, State.UPLOADING);

                    try {
                        DataValueSet dataValueSet = new DataValueSet(dataValues);
                        DataValueImportSummary dataValueImportSummary = apiCallExecutor.executeObjectCall(
                                dataValueService.postDataValues(dataValueSet));

                        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary);
                    } catch (D2Error e) {
                        markObjectsAs(dataValues, DataStateHelper.errorIfOnline(e));
                        throw e;
                    }

                    emitter.onNext(progressManager.increaseProgress(DataValue.class, true));
                    emitter.onComplete();
                });
            }
        });
    }

    private void markObjectsAs(Collection<DataValue> dataValues, @Nullable State forcedState) {
        for (DataValue dataValue : dataValues) {
            dataValueStore.setState(dataValue, DataStateHelper.forcedOrOwn(dataValue, forcedState));
        }
    }
}
