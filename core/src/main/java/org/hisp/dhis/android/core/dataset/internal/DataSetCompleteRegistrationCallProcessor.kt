/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.dataset.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration

@Reusable
internal class DataSetCompleteRegistrationCallProcessor @Inject internal constructor(
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
    private val handler: Handler<DataSetCompleteRegistration>
) {

    internal fun process(objectList: List<DataSetCompleteRegistration>, query: DataSetCompleteRegistrationQuery) {
        if (objectList.isNotEmpty()) {
            removeExistingRegistersForQuery(query)
            val objectsToImport = objectList.filter {
                !CollectionsHelper.isDeleted(it)
            }
            handler.handleMany(objectsToImport)
        }
    }

    /*
     For versions lower than 2.32:
     Records deleted in the server are not returned in the API. The strategy here is to remove all the records
     linked to a particular query before storing the returned values. Only records in "SYNCED" state are removed.
     */
    private fun removeExistingRegistersForQuery(query: DataSetCompleteRegistrationQuery) {
        for (rootOrgUnitUid in query.rootOrgUnitUids()) {
            dataSetCompleteRegistrationStore.removeNotPresentAndSynced(
                query.dataSetUids(), query.periodIds(), rootOrgUnitUid
            )
        }
    }
}
