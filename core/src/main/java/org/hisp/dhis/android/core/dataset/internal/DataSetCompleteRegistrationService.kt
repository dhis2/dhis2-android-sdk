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

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Which
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import retrofit2.Call
import retrofit2.http.*

@Suppress("LongParameterList")
internal interface DataSetCompleteRegistrationService {

    @GET("completeDataSetRegistrations")
    fun getDataSetCompleteRegistrations(
        @Query("fields") @Which fields: Fields<DataSetCompleteRegistration>,
        @Query("lastUpdated") lastUpdated: String?,
        @Query("dataSet") dataSetUids: String,
        @Query("period") periodIds: String,
        @Query("orgUnit") organisationUnitIds: String,
        @Query("children") children: Boolean,
        @Query("paging") paging: Boolean
    ): Single<DataSetCompleteRegistrationPayload>

    @POST("completeDataSetRegistrations")
    fun postDataSetCompleteRegistrations(
        @Body dataSetCompleteRegistrationPayload: DataSetCompleteRegistrationPayload
    ): Call<DataValueImportSummary>

    @POST("completeDataSetRegistrations")
    fun postDataSetCompleteRegistrationsWebResponse(
        @Body dataSetCompleteRegistrationPayload: DataSetCompleteRegistrationPayload
    ): Call<DataValueImportSummaryWebResponse>

    @DELETE("completeDataSetRegistrations")
    fun deleteDataSetCompleteRegistration(
        @Query("ds") dataSet: String,
        @Query("pe") periodId: String,
        @Query("ou") orgUnit: String,
        @Query("cc") categoryComboUid: String,
        @Query("cp") categoryOptionUids: String,
        @Query("multiOu") multiOrganisationUnit: Boolean
    ): Call<Unit>
}
