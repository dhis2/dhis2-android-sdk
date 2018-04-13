/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboEndpointCall;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SimpleCallFactory;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramAccessEndpointCall;
import org.hisp.dhis.android.core.program.ProgramParentCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity", "PMD.ExcessiveImports"})
public class MetadataCall implements Call<Response> {

    private final GenericCallData data;
    private boolean isExecuted;

    private final SimpleCallFactory<SystemInfo> systemInfoCallFactory;
    private final SimpleCallFactory<User> userCallFactory;
    private final SimpleCallFactory<Payload<Category>> categoryCallFactory;
    private final SimpleCallFactory<Payload<CategoryCombo>> categoryComboCallFactory;
    private final SimpleCallFactory<Payload<Program>> programAccessCallFactory;
    private final ProgramParentCall.Factory programParentCallFactory;
    private final OrganisationUnitCall.Factory organisationUnitCallFactory;
    private final DataSetParentCall.Factory dataSetParentCallFactory;

    public MetadataCall(@NonNull GenericCallData data,
                        @NonNull SimpleCallFactory<SystemInfo> systemInfoCallFactory,
                        @NonNull SimpleCallFactory<User> userCallFactory,
                        @NonNull SimpleCallFactory<Payload<Category>> categoryCallFactory,
                        @NonNull SimpleCallFactory<Payload<CategoryCombo>> categoryComboCallFactory,
                        @NonNull SimpleCallFactory<Payload<Program>> programAccessCallFactory,
                        @NonNull ProgramParentCall.Factory programParentCallFactory,
                        @NonNull OrganisationUnitCall.Factory organisationUnitCallFactory,
                        @NonNull DataSetParentCall.Factory dataSetParentCallFactory) {
        this.data = data;
        this.systemInfoCallFactory = systemInfoCallFactory;
        this.userCallFactory = userCallFactory;
        this.categoryCallFactory = categoryCallFactory;
        this.categoryComboCallFactory = categoryComboCallFactory;
        this.programAccessCallFactory = programAccessCallFactory;
        this.programParentCallFactory = programParentCallFactory;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.dataSetParentCallFactory = dataSetParentCallFactory;
    }


    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        Transaction transaction = data.databaseAdapter().beginNewTransaction();
        try {

            Response<SystemInfo> systemCallResponse = systemInfoCallFactory.create(data).call();
            if (!systemCallResponse.isSuccessful()) {
                return systemCallResponse;
            }

            Response<User> userResponse = userCallFactory.create(data).call();
            if (!userResponse.isSuccessful()) {
                return userResponse;
            }

            Response<Payload<Category>> categoryResponse = categoryCallFactory.create(data).call();
            if (!categoryResponse.isSuccessful()) {
                return categoryResponse;
            }

            Response<Payload<CategoryCombo>> categoryComboResponse = categoryComboCallFactory.create(data).call();
            if (!categoryComboResponse.isSuccessful()) {
                return categoryComboResponse;
            }

            Response<Payload<Program>> programAccessResponse = programAccessCallFactory.create(data).call();
            if (!programAccessResponse.isSuccessful()) {
                return programAccessResponse;
            }

            Set<String> programUids = getProgramUidsWithDataReadAccess(programAccessResponse.body().items());
            Response programResponse = programParentCallFactory.create(data, programUids).call();
            if (!programResponse.isSuccessful()) {
                return programResponse;
            }

            User user = userResponse.body();
            Response<Payload<OrganisationUnit>> organisationUnitResponse =
                    organisationUnitCallFactory.create(data, user, programUids).call();

            if (!organisationUnitResponse.isSuccessful()) {
                return organisationUnitResponse;
            }

            List<OrganisationUnit> organisationUnits = organisationUnitResponse.body().items();
            Response dataSetParentCallResponse = dataSetParentCallFactory.create(user, data, organisationUnits).call();

            if (dataSetParentCallResponse.isSuccessful()) {
                transaction.setSuccessful();
            }

            return dataSetParentCallResponse;
        } finally {
            transaction.end();
        }
    }

    // Utility method
    private Set<String> getProgramUidsWithDataReadAccess(List<Program> programsWithAccess) {
        Set<String> programUids = new HashSet<>();
        for (Program program: programsWithAccess) {
            Access access = program.access();
            if (access != null && access.data().read()) {
                programUids.add(program.uid());
            }
        }

        return programUids;
    }

    public static MetadataCall create(GenericCallData data) {
        return new MetadataCall(
                data,
                SystemInfoCall.FACTORY,
                UserCall.FACTORY,
                CategoryEndpointCall.FACTORY,
                CategoryComboEndpointCall.FACTORY,
                ProgramAccessEndpointCall.FACTORY,
                ProgramParentCall.FACTORY,
                OrganisationUnitCall.FACTORY,
                DataSetParentCall.FACTORY
        );
    }
}
