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

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface DataSetCompleteRegistrationService {

    @GET("completeDataSetRegistrations")
    Call<DataSetCompleteRegistrationPayload> getDataSetCompleteRegistrations(
            @Query("fields") @Which Fields<DataSetCompleteRegistration> fields,
            @Query("lastUpdated") String lastUpdated,
            @Query("dataSet") String dataSetUids,
            @Query("period") String periodIds,
            @Query("orgUnit") String organisationUnitIds,
            @Query("children") Boolean children,
            @Query("paging") Boolean paging
    );

    @POST("completeDataSetRegistrations")
    Call<DataValueImportSummary> postDataSetCompleteRegistrations(
            @Body DataSetCompleteRegistrationPayload dataSetCompleteRegistrationPayload);

    @DELETE("completeDataSetRegistrations")
    Call<Unit> deleteDataSetCompleteRegistration(
            @Query("ds") String dataSet,
            @Query("pe") String periodId,
            @Query("ou") String orgUnit,
            @Query("cc") String categoryComboUid,
            @Query("cp") String categoryOptionUids,
            @Query("multiOu") Boolean multiOrganisationUnit);
}