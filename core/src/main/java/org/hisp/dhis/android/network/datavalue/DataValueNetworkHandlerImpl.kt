/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.datavalue

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaSeparatedCollectionValues
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaSeparatedUids
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueNetworkHandler
import org.hisp.dhis.android.core.datavalue.internal.DataValueSet
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.koin.core.annotation.Singleton

@Singleton
internal class DataValueNetworkHandlerImpl(httpService: HttpServiceClientKotlinx) : DataValueNetworkHandler {
    private val service: DataValueService = DataValueService(httpService)
    override suspend fun getDataValues(bundle: AggregatedDataCallBundle): List<DataValue> {
        val apiResponse = service.getDataValues(
            fields = DataValueFields.allFields,
            lastUpdated = bundle.key.lastUpdatedStr(),
            dataSetUids = commaSeparatedUids(bundle.dataSets),
            periodIds = commaSeparatedCollectionValues(bundle.periodIds),
            orgUnitUids = commaSeparatedCollectionValues(bundle.rootOrganisationUnitUids),
            children = true,
            paging = false,
            includeDeleted = true,
        )
        return apiResponse.dataValues.map { it.toDomain() }
    }

    override suspend fun postDataValues(dataValueSet: DataValueSet): DataValueImportSummary {
        val apiPayload = DataValueSetDTO.fromDomain(dataValueSet)
        val apiResponse = service.postDataValues(apiPayload)
        return apiResponse.toDomain()
    }

    override suspend fun postDataValuesWebResponse(dataValueSet: DataValueSet): DataValueImportSummaryWebResponse {
        val apiPayload = DataValueSetDTO.fromDomain(dataValueSet)
        val apiResponse = service.postDataValuesWebResponse(apiPayload)
        return apiResponse.toDomain()
    }
}
