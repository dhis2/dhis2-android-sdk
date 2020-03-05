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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.call.processors.internal.CallProcessor;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isDeleted;

class DataSetCompleteRegistrationCallProcessor implements CallProcessor<DataSetCompleteRegistration> {

    private final DatabaseAdapter databaseAdapter;
    private final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;
    private final Handler<DataSetCompleteRegistration> handler;
    private final DataSetCompleteRegistrationQuery query;

    DataSetCompleteRegistrationCallProcessor(DatabaseAdapter databaseAdapter,
                                             DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore,
                                             Handler<DataSetCompleteRegistration> handler,
                                             DataSetCompleteRegistrationQuery query) {
        this.databaseAdapter = databaseAdapter;
        this.dataSetCompleteRegistrationStore = dataSetCompleteRegistrationStore;
        this.handler = handler;
        this.query = query;
    }

    @Override
    public final void process(final List<DataSetCompleteRegistration> objectList) throws D2Error {
        if (objectList != null && !objectList.isEmpty()) {
            D2CallExecutor.create(databaseAdapter).executeD2CallTransactionally(() -> {
                removeExistingRegistersForQuery(query);
                List<DataSetCompleteRegistration> objectsToImport = removeDeletedEntries(objectList);
                handler.handleMany(objectsToImport);
                return null;
            });
        }
    }

    /*
     For versions lower than 2.32:
     Records deleted in the server are not returned in the API. The strategy here is to remove all the records
     linked to a particular query before storing the returned values. Only records in "SYNCED" state are removed.
     */
    private void removeExistingRegistersForQuery(DataSetCompleteRegistrationQuery query) {
        for (String rootOrgUnitUid : query.rootOrgUnitUids()) {
            dataSetCompleteRegistrationStore.removeNotPresentAndSynced(query.dataSetUids(),
                    query.periodIds(), rootOrgUnitUid);
        }
    }

    private List<DataSetCompleteRegistration> removeDeletedEntries(List<DataSetCompleteRegistration> list) {
        List<DataSetCompleteRegistration> objectsToImport = new ArrayList<>();

        for (DataSetCompleteRegistration record : list) {
            if (!isDeleted(record)) {
                objectsToImport.add(record);
            }
        }

        return objectsToImport;
    }

}

