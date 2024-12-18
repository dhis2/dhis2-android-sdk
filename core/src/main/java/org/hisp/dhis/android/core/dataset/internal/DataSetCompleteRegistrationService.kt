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
package org.hisp.dhis.android.core.dataset.internal

import io.ktor.client.statement.HttpResponse
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class DataSetCompleteRegistrationService(private val client: HttpServiceClient) {

    suspend fun getDataSetCompleteRegistrations(
        fields: Fields<DataSetCompleteRegistration>,
        lastUpdated: String?,
        dataSetUids: String,
        periodIds: String,
        organisationUnitIds: String,
        children: Boolean,
        paging: Boolean,
    ): DataSetCompleteRegistrationPayload {
        return client.get {
            url("completeDataSetRegistrations")
            parameters {
                fields(fields)
                attribute("lastUpdated", lastUpdated)
                attribute("dataSet", dataSetUids)
                attribute("period", periodIds)
                attribute("orgUnit", organisationUnitIds)
                attribute("children", children)
                paging(paging)
            }
        }
    }

    suspend fun postDataSetCompleteRegistrations(
        dataSetCompleteRegistrationPayload: DataSetCompleteRegistrationPayload,
    ): DataValueImportSummary {
        return client.post {
            url("completeDataSetRegistrations")
            body(dataSetCompleteRegistrationPayload)
        }
    }

    suspend fun postDataSetCompleteRegistrationsWebResponse(
        dataSetCompleteRegistrationPayload: DataSetCompleteRegistrationPayload,
    ): DataValueImportSummaryWebResponse {
        return client.post {
            url("completeDataSetRegistrations")
            body(dataSetCompleteRegistrationPayload)
        }
    }

    suspend fun deleteDataSetCompleteRegistration(
        dataSet: String,
        periodId: String,
        orgUnit: String,
        categoryComboUid: String,
        categoryOptionUids: String,
        multiOrganisationUnit: Boolean,
    ): HttpResponse {
        return client.delete {
            url("completeDataSetRegistrations")
            parameters {
                attribute("ds", dataSet)
                attribute("pe", periodId)
                attribute("ou", orgUnit)
                attribute("cc", categoryComboUid)
                attribute("cp", categoryOptionUids)
                attribute("multiOu", multiOrganisationUnit)
            }
        }
    }
}
