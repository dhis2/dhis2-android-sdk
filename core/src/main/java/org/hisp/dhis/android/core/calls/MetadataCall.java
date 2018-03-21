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
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryComboQuery;
import org.hisp.dhis.android.core.category.CategoryComboService;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.category.CategoryService;
import org.hisp.dhis.android.core.category.ResponseValidator;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DictionaryTableHandler;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetCall;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.option.OptionSetService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramAccessEndpointCall;
import org.hisp.dhis.android.core.program.ProgramCall;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserRoleStore;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.CyclomaticComplexity",
 "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.ExcessiveMethodLength"})
public class MetadataCall implements Call<Response> {
    private final DatabaseAdapter databaseAdapter;
    private final SystemInfoService systemInfoService;
    private final UserService userService;
    private final ProgramService programService;
    private final OrganisationUnitService organisationUnitService;
    private final TrackedEntityService trackedEntityService;
    private final OptionSetService optionSetService;
    private final SystemInfoStore systemInfoStore;
    private final ResourceStore resourceStore;
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final UserRoleStore userRoleStore;
    private final OrganisationUnitStore organisationUnitStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final ProgramStore programStore;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    private final ProgramRuleVariableStore programRuleVariableStore;
    private final ProgramIndicatorStore programIndicatorStore;
    private final ProgramStageSectionProgramIndicatorLinkStore
            programStageSectionProgramIndicatorLinkStore;
    private final ProgramRuleActionStore programRuleActionStore;
    private final ProgramRuleStore programRuleStore;
    private final ProgramStageDataElementStore programStageDataElementStore;
    private final ProgramStageSectionStore programStageSectionStore;
    private final IdentifiableObjectStore<ProgramStageModel> programStageStore;
    private final RelationshipTypeStore relationshipStore;
    private final TrackedEntityStore trackedEntityStore;
    private final GenericHandler<OptionSet, OptionSetModel> optionSetHandler;
    private final GenericHandler<DataElement, DataElementModel> dataElementHandler;
    private final GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;
    private final DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    private final Retrofit retrofit;
    private final CategoryQuery categoryQuery;
    private final CategoryComboQuery categoryComboQuery;
    private final CategoryService categoryService;
    private final CategoryComboService categoryComboService;
    private final CategoryHandler categoryHandler;
    private final CategoryComboHandler categoryComboHandler;
    private final DataSetParentCall.Factory dataSetParentCallFactory;

    private boolean isExecuted;

    private final OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore;

    public MetadataCall(@NonNull DatabaseAdapter databaseAdapter,
                        @NonNull SystemInfoService systemInfoService,
                        @NonNull UserService userService,
                        @NonNull ProgramService programService,
                        @NonNull OrganisationUnitService organisationUnitService,
                        @NonNull TrackedEntityService trackedEntityService,
                        @NonNull OptionSetService optionSetService,
                        @NonNull SystemInfoStore systemInfoStore,
                        @NonNull ResourceStore resourceStore,
                        @NonNull UserStore userStore,
                        @NonNull UserCredentialsStore userCredentialsStore,
                        @NonNull UserRoleStore userRoleStore,
                        @NonNull OrganisationUnitStore organisationUnitStore,
                        @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
                        @NonNull ProgramStore programStore,
                        @NonNull TrackedEntityAttributeStore trackedEntityAttributeStore,
                        @NonNull ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore,
                        @NonNull ProgramRuleVariableStore programRuleVariableStore,
                        @NonNull ProgramIndicatorStore programIndicatorStore,
                        @NonNull ProgramStageSectionProgramIndicatorLinkStore
                                programStageSectionProgramIndicatorLinkStore,
                        @NonNull ProgramRuleActionStore programRuleActionStore,
                        @NonNull ProgramRuleStore programRuleStore,
                        @NonNull ProgramStageDataElementStore programStageDataElementStore,
                        @NonNull ProgramStageSectionStore programStageSectionStore,
                        @NonNull IdentifiableObjectStore<ProgramStageModel> programStageStore,
                        @NonNull RelationshipTypeStore relationshipStore,
                        @NonNull TrackedEntityStore trackedEntityStore,
                        @NonNull OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore,
                        @NonNull CategoryQuery categoryQuery,
                        @NonNull CategoryService categoryService,
                        @NonNull CategoryHandler categoryHandler,
                        @NonNull CategoryComboQuery categoryComboQuery,
                        @NonNull CategoryComboService categoryComboService,
                        @NonNull CategoryComboHandler categoryComboHandler,
                        @NonNull GenericHandler<OptionSet, OptionSetModel> optionSetHandler,
                        @NonNull GenericHandler<DataElement, DataElementModel> dataElementHandler,
                        @NonNull DataSetParentCall.Factory dataSetParentCallFactory,
                        @NonNull GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler,
                        @NonNull DictionaryTableHandler<ValueTypeRendering> renderTypeHandler,
                        @NonNull Retrofit retrofit
                        ) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoService = systemInfoService;
        this.userService = userService;
        this.programService = programService;
        this.organisationUnitService = organisationUnitService;
        this.trackedEntityService = trackedEntityService;
        this.optionSetService = optionSetService;
        this.systemInfoStore = systemInfoStore;
        this.resourceStore = resourceStore;
        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.userRoleStore = userRoleStore;
        this.organisationUnitStore = organisationUnitStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.programStore = programStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
        this.programRuleVariableStore = programRuleVariableStore;
        this.programIndicatorStore = programIndicatorStore;
        this.programStageSectionProgramIndicatorLinkStore =
                programStageSectionProgramIndicatorLinkStore;
        this.programRuleActionStore = programRuleActionStore;
        this.programRuleStore = programRuleStore;
        this.programStageDataElementStore = programStageDataElementStore;
        this.programStageSectionStore = programStageSectionStore;
        this.programStageStore = programStageStore;
        this.relationshipStore = relationshipStore;
        this.trackedEntityStore = trackedEntityStore;
        this.organisationUnitProgramLinkStore = organisationUnitProgramLinkStore;
        this.categoryQuery = categoryQuery;
        this.categoryService = categoryService;
        this.categoryHandler = categoryHandler;
        this.categoryComboQuery = categoryComboQuery;
        this.categoryComboService = categoryComboService;
        this.categoryComboHandler = categoryComboHandler;
        this.optionSetHandler = optionSetHandler;
        this.dataElementHandler = dataElementHandler;
        this.dataSetParentCallFactory = dataSetParentCallFactory;
        this.styleHandler = styleHandler;
        this.renderTypeHandler = renderTypeHandler;

        this.retrofit = retrofit;
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

        Response response = null;
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            response = new SystemInfoCall(
                    databaseAdapter, systemInfoStore,
                    systemInfoService, resourceStore
            ).call();
            if (!response.isSuccessful()) {
                return response;
            }
            GenericCallData data = GenericCallData.create(databaseAdapter,
                    new ResourceHandler(resourceStore), retrofit);

            Response<User> userResponse = new UserCall(
                    userService,
                    databaseAdapter,
                    userStore,
                    userCredentialsStore,
                    userRoleStore,
                    resourceStore,
                    data.serverDate()
            ).call();
            response = userResponse;
            if (!response.isSuccessful()) {
                return response;
            }


            response = downloadCategories(data.serverDate());

            if (!response.isSuccessful()) {
                return response;
            }
            response = downloadCategoryCombos(data.serverDate());

            if (!response.isSuccessful()) {
                return response;
            }

            Response<Payload<Program>> programAccessResponse = ProgramAccessEndpointCall.FACTORY
                    .create(data, programService).call();
            response = programAccessResponse;

            if (!response.isSuccessful()) {
                return response;
            }

            Set<String> programUids = getProgramUidsWithDataReadAccess(programAccessResponse.body().items());
            response = new ProgramCall(
                    programService, databaseAdapter, resourceStore, programUids, programStore,
                    data.serverDate(),
                    trackedEntityAttributeStore, programTrackedEntityAttributeStore,
                    programRuleVariableStore,
                    programIndicatorStore, programStageSectionProgramIndicatorLinkStore,
                    programRuleActionStore,
                    programRuleStore,
                    programStageDataElementStore,
                    programStageSectionStore, programStageStore, relationshipStore,
                    dataElementHandler, styleHandler, renderTypeHandler
            ).call();
            if (!response.isSuccessful()) {
                return response;
            }

            List<Program> programs = ((Response<Payload<Program>>) response).body().items();
            Set<String> trackedEntityUids = getAssignedTrackedEntityUids(programs);
            response = new TrackedEntityCall(
                    trackedEntityUids, databaseAdapter, trackedEntityStore,
                    resourceStore, trackedEntityService, data.serverDate()
            ).call();
            if (!response.isSuccessful()) {
                return response;
            }

            User user = userResponse.body();
            Response<Payload<OrganisationUnit>> organisationUnitResponse = new OrganisationUnitCall(
                    user, organisationUnitService, databaseAdapter, organisationUnitStore,
                    resourceStore, data.serverDate(), userOrganisationUnitLinkStore,
                    organisationUnitProgramLinkStore, programUids).call();
            if (!organisationUnitResponse.isSuccessful()) {
                return organisationUnitResponse;
            }

            Set<String> optionSetUids = getAssignedOptionSetUids(programs);
            response = new OptionSetCall(
                    data, optionSetService, optionSetHandler, optionSetUids).call();
            if (!response.isSuccessful()) {
                return response;
            }
            List<OrganisationUnit> organisationUnits = organisationUnitResponse.body().items();
            response = dataSetParentCallFactory.create(user, data, organisationUnits).call();
            if (!response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();
            return response;
        } finally {
            transaction.end();
        }
    }

    /// Utilty methods:
    private Set<String> getAssignedOptionSetUids(List<Program> programs) {
        if (programs == null) {
            return null;
        }
        Set<String> uids = new HashSet<>();
        int size = programs.size();
        for (int i = 0; i < size; i++) {
            Program program = programs.get(i);

            getOptionSetUidsForAttributes(uids, program);
            getOptionSetUidsForDataElements(uids, program);
        }
        return uids;
    }

    private void getOptionSetUidsForDataElements(Set<String> uids, Program program) {
        List<ProgramStage> programStages = program.programStages();
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

    private Response<Payload<Category>> downloadCategories(Date serverDate) throws Exception {
        ResponseValidator<Category> validator = new ResponseValidator<>();
        return new CategoryEndpointCall(categoryQuery, categoryService, validator,
                categoryHandler,
                new ResourceHandler(resourceStore), databaseAdapter, serverDate).call();
    }

    private Response<Payload<CategoryCombo>> downloadCategoryCombos(Date serverDate)
            throws Exception {

        ResponseValidator<CategoryCombo> comboValidator = new ResponseValidator<>();

        return new CategoryComboEndpointCall(categoryComboQuery, categoryComboService,
                comboValidator, categoryComboHandler,
                new ResourceHandler(resourceStore), databaseAdapter, serverDate).call();
    }
}
