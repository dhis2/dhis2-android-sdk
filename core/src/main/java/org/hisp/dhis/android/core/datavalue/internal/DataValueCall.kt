/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.datavalue.internal

import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCall
import org.hisp.dhis.android.core.arch.helpers.internal.UrlLengthHelper
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle
import org.koin.core.annotation.Singleton

@Singleton
internal class DataValueCall(
    private val networkHandler: DataValueNetworkHandler,
    private val handler: DataValueHandler,
    private val apiDownloader: APIDownloader,
    private val categoryOptionComboStore: CategoryOptionComboStore,
) : QueryCall<DataValue, DataValueQuery> {

    companion object {
        private const val QUERY_WITHOUT_UIDS_LENGTH = (
            "dataValueSets?fields=dataElement,period,orgUnit,categoryOptionCombo,attributeOptionCombo,value," +
                "storedBy,created,lastUpdated,comment,followup,deleted&lastUpdated=0000-00-00T00:00:00.000" +
                "&dataSet=&period=&orgUnit=&attributeOptionCombo=&children=true&includeDeleted=true"
            ).length

        private const val DATA_SET_UIDS = 1
    }

    override suspend fun download(query: DataValueQuery): List<DataValue> {
        val bundle = query.bundle
        val availableUids = availableUidsForAttributeOptionCombos(bundle)

        return bundle.dataSets.flatMap { dataSet ->
            val attributeOptionComboUids = attributeOptionComboUids(dataSet, availableUids)
            apiDownloader.downloadListAsCoroutine(handler) {
                networkHandler.getDataValuesForDataSet(dataSet.uid(), attributeOptionComboUids, bundle)
            }
        }
    }

    private suspend fun attributeOptionComboUids(dataSet: DataSet, availableUids: Int): List<String> {
        val uids = categoryOptionComboStore.getForCategoryCombo(dataSet.categoryCombo().uid())
            .map { it.uid() }

        return if (uids.size <= availableUids) uids else emptyList()
    }

    private fun availableUidsForAttributeOptionCombos(bundle: AggregatedDataCallBundle): Int {
        return UrlLengthHelper.getHowManyUidsFitInURL(QUERY_WITHOUT_UIDS_LENGTH) -
            DATA_SET_UIDS - bundle.periodIds.size - bundle.rootOrganisationUnitUids.size
    }
}
