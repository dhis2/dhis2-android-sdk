/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.network.datasetcompleteregistration

import io.ktor.client.statement.HttpResponse
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationFields
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationNetworkHandler
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.koin.core.annotation.Singleton
import java.net.HttpURLConnection

@Singleton
internal class DataSetCompleteRegistrationNetworkHandlerImpl(
    httpClient: HttpServiceClientKotlinx,
    private val versionManager: DHISVersionManager,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) : DataSetCompleteRegistrationNetworkHandler {
    private val service: DataSetCompleteRegistrationService = DataSetCompleteRegistrationService(httpClient)

    override suspend fun getDataSetCompleteRegistrations(
        lastUpdated: String?,
        dataSetUids: String,
        periodIds: String,
        organisationUnitIds: String,
        children: Boolean,
        paging: Boolean,
    ): List<DataSetCompleteRegistration> {
        val dataSetCompleteRegistrations = service.getDataSetCompleteRegistrations(
            DataSetCompleteRegistrationFields.allFields,
            lastUpdated,
            dataSetUids,
            periodIds,
            organisationUnitIds,
            children,
            paging,
        )
        return dataSetCompleteRegistrations.items.map { it.toDomain() }
    }

    override suspend fun postDataSetCompleteRegistrations(
        dataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
    ): Result<DataValueImportSummary, D2Error> {
        val apiPayload = DataSetCompleteRegistrationPayload(null, dataSetCompleteRegistrations.map { it.toDto() })

        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_38)) {
            coroutineAPICallExecutor.wrap(
                acceptedErrorCodes = listOf(HttpURLConnection.HTTP_CONFLICT),
                errorClass = DataValueImportSummaryWebResponse::class.java,
            ) {
                service.postDataSetCompleteRegistrationsWebResponse(apiPayload)
                    .toDomain()
            }.map { it.response }
        } else {
            coroutineAPICallExecutor.wrap {
                service.postDataSetCompleteRegistrations(apiPayload)
                    .toDomain()
            }
        }
    }

    override suspend fun deleteDataSetCompleteRegistration(
        dataSet: String,
        periodId: String,
        orgUnit: String,
        categoryComboUid: String,
        categoryOptionUids: String,
        multiOrganisationUnit: Boolean,
    ): Result<HttpResponse, D2Error> {
        return coroutineAPICallExecutor.wrap {
            service.deleteDataSetCompleteRegistration(
                dataSet,
                periodId,
                orgUnit,
                categoryComboUid,
                categoryOptionUids,
                multiOrganisationUnit,
            )
        }
    }
}
