/*
 * Copyright (c) 2004-2018, University of Oslo
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

package org.hisp.dhis.android.core.datavalue;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.imports.ImportSummary;

import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Retrofit;

public final class DataValuePostCall extends SyncCall<ImportSummary> {

    private final DataValueService dataValueService;
    private final DataValueStore dataValueStore;

    @Override
    public ImportSummary call() throws Exception {

        setExecuted();

        Collection<DataValue> toPostDataValues = new ArrayList<>();

        appendPostableDataValues(toPostDataValues);
        appendUpdatableDataValues(toPostDataValues);

        if (toPostDataValues.isEmpty()) {
            return null;
        }

        DataValueSet dataValueSet = new DataValueSet(toPostDataValues);

        ImportSummary importSummary = new APICallExecutor().executeObjectCall(
                dataValueService.postDataValues(dataValueSet));

        handleImportSummary(dataValueSet, importSummary);

        return importSummary;
    }

    private void appendPostableDataValues(Collection<DataValue> dataValues) {
        dataValues.addAll(dataValueStore.getDataValuesWithState(State.TO_POST));
    }

    private void appendUpdatableDataValues(Collection<DataValue> dataValues) {
        dataValues.addAll(dataValueStore.getDataValuesWithState(State.TO_UPDATE));
    }

    private void handleImportSummary(DataValueSet dataValueSet, ImportSummary importSummary) {

        DataValueImportHandler dataValueImportHandler =
                new DataValueImportHandler(dataValueStore);

        dataValueImportHandler.handleImportSummary(dataValueSet, importSummary);
    }

    private DataValuePostCall(@NonNull DataValueService dataValueService,
                              @NonNull DataValueStore dataValueSetStore) {

        this.dataValueService = dataValueService;
        this.dataValueStore = dataValueSetStore;
    }

    public static DataValuePostCall create(@NonNull DatabaseAdapter databaseAdapter,
                                     @NonNull Retrofit retrofit) {

        return new DataValuePostCall(retrofit.create(DataValueService.class),
                                     DataValueStore.create(databaseAdapter));
    }
}
