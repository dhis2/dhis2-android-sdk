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
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SimpleCallFactory;
import org.hisp.dhis.android.core.common.UidsCallFactory;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramAccessEndpointCall;
import org.hisp.dhis.android.core.program.ProgramCall;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageEndpointCall;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityCall;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class MetadataCall implements Call<Response> {

    private final GenericCallData data;
    private boolean isExecuted;

    private final SimpleCallFactory<SystemInfo> systemInfoCallFactory;
    private final SimpleCallFactory<User> userCallFactory;
    private final SimpleCallFactory<Payload<Category>> categoryCallFactory;
    private final SimpleCallFactory<Payload<CategoryCombo>> categoryComboCallFactory;
    private final SimpleCallFactory<Payload<Program>> programAccessCallFactory;
    private final UidsCallFactory<Program> programCallFactory;
    private final UidsCallFactory<ProgramStage> programStageCallFactory;
    private final UidsCallFactory<TrackedEntity> trackedEntityCallFactory;
    private final OrganisationUnitCall.Factory organisationUnitCallFactory;
    private final UidsCallFactory<OptionSet> optionSetCallFactory;
    private final DataSetParentCall.Factory dataSetParentCallFactory;

    public MetadataCall(@NonNull GenericCallData data,
                        @NonNull SimpleCallFactory<SystemInfo> systemInfoCallFactory,
                        @NonNull SimpleCallFactory<User> userCallFactory,
                        @NonNull SimpleCallFactory<Payload<Category>> categoryCallFactory,
                        @NonNull SimpleCallFactory<Payload<CategoryCombo>> categoryComboCallFactory,
                        @NonNull SimpleCallFactory<Payload<Program>> programAccessCallFactory,
                        @NonNull UidsCallFactory<Program> programCallFactory,
                        @NonNull UidsCallFactory<ProgramStage> programStageCallFactory,
                        @NonNull UidsCallFactory<TrackedEntity> trackedEntityCallFactory,
                        @NonNull OrganisationUnitCall.Factory organisationUnitCallFactory,
                        @NonNull UidsCallFactory<OptionSet> optionSetCallFactory,
                        @NonNull DataSetParentCall.Factory dataSetParentCallFactory) {
        this.data = data;
        this.systemInfoCallFactory = systemInfoCallFactory;
        this.userCallFactory = userCallFactory;
        this.categoryCallFactory = categoryCallFactory;
        this.categoryComboCallFactory = categoryComboCallFactory;
        this.programAccessCallFactory = programAccessCallFactory;
        this.programCallFactory = programCallFactory;
        this.programStageCallFactory = programStageCallFactory;
        this.trackedEntityCallFactory = trackedEntityCallFactory;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.optionSetCallFactory = optionSetCallFactory;
        this.dataSetParentCallFactory = dataSetParentCallFactory;
    }


    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

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
            Response<Payload<Program>> programResponse = programCallFactory.create(data, programUids).call();
            if (!programResponse.isSuccessful()) {
                return programResponse;
            }

            List<Program> programs = programResponse.body().items();
            Set<String> assignedProgramStageUids = getAssignedProgramStageUids(programs);
            Response<Payload<ProgramStage>> programStageResponse = programStageCallFactory.create(data,
                    assignedProgramStageUids).call();
            if (!programStageResponse.isSuccessful()) {
                return programStageResponse;
            }

            Set<String> trackedEntityUids = getAssignedTrackedEntityUids(programs);
            Response<Payload<TrackedEntity>> trackedEntityResponse =
                    trackedEntityCallFactory.create(data, trackedEntityUids).call();
            if (!trackedEntityResponse.isSuccessful()) {
                return trackedEntityResponse;
            }

            User user = userResponse.body();
            Response<Payload<OrganisationUnit>> organisationUnitResponse =
                    organisationUnitCallFactory.create(data, user, programUids).call();

            if (!organisationUnitResponse.isSuccessful()) {
                return organisationUnitResponse;
            }

            List<ProgramStage> programStages = programStageResponse.body().items();
            Set<String> optionSetUids = getAssignedOptionSetUids(programs, programStages);
            Response<Payload<OptionSet>> optionSetResponse = optionSetCallFactory.create(data, optionSetUids).call();
            if (!optionSetResponse.isSuccessful()) {
                return optionSetResponse;
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

    /// Utility methods:
    private Set<String> getAssignedOptionSetUids(List<Program> programs, List<ProgramStage> programStages) {
        Set<String> uids = new HashSet<>();

        if (programs != null) {
            for (Program program : programs) {
                getOptionSetUidsForAttributes(uids, program);
            }
        }

        if (programStages != null) {
            getOptionSetUidsForDataElements(uids, programStages);
        }
        return uids;
    }

    private void getOptionSetUidsForDataElements(Set<String> uids, List<ProgramStage> programStages) {
        int programStagesSize = programStages.size();

        for (int j = 0; j < programStagesSize; j++) {
            ProgramStage programStage = programStages.get(j);
            List<ProgramStageDataElement> programStageDataElements =
                    programStage.programStageDataElements();
            int programStageDataElementSize = programStageDataElements.size();

            for (int k = 0; k < programStageDataElementSize; k++) {
                ProgramStageDataElement programStageDataElement = programStageDataElements.get(k);

                if (programStageDataElement.dataElement() != null &&
                        programStageDataElement.dataElement().optionSet() != null) {
                    uids.add(programStageDataElement.dataElement().optionSet().uid());
                }
            }
        }
    }

    private void getOptionSetUidsForAttributes(Set<String> uids, Program program) {
        int programTrackedEntityAttributeSize = program.programTrackedEntityAttributes().size();
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                program.programTrackedEntityAttributes();

        for (int j = 0; j < programTrackedEntityAttributeSize; j++) {
            ProgramTrackedEntityAttribute programTrackedEntityAttribute =
                    programTrackedEntityAttributes.get(j);

            if (programTrackedEntityAttribute.trackedEntityAttribute() != null &&
                    programTrackedEntityAttribute.trackedEntityAttribute().optionSet() != null) {
                uids.add(programTrackedEntityAttribute.trackedEntityAttribute().optionSet().uid());
            }
        }
    }

    private Set<String> getAssignedTrackedEntityUids(List<Program> programs) {
        if (programs == null) {
            return null;
        }

        Set<String> uids = new HashSet<>();

        int size = programs.size();
        for (int i = 0; i < size; i++) {
            Program program = programs.get(i);

            if (program.trackedEntity() != null) {
                uids.add(program.trackedEntity().uid());
            }
        }
        return uids;
    }

    private Set<String> getAssignedProgramStageUids(List<Program> programs) {
        Set<String> programStageUids = new HashSet<>();

        for (Program program : programs) {
            for (ObjectWithUid programStage : program.programStages()) {
                programStageUids.add(programStage.uid());
            }
        }

        return programStageUids;
    }

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
                ProgramCall.FACTORY,
                ProgramStageEndpointCall.FACTORY,
                TrackedEntityCall.FACTORY,
                OrganisationUnitCall.FACTORY,
                OptionSetCall.FACTORY,
                DataSetParentCall.FACTORY
        );
    }
}
