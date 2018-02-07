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
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramCall;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeFactory;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkStore;
import org.hisp.dhis.android.core.user.UserRoleStore;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import retrofit2.Response;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.CyclomaticComplexity",
 "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class MetadataCall implements Call<Response> {
    private final DatabaseAdapter databaseAdapter;
    private final SystemInfoService systemInfoService;
    private final UserService userService;
    private final ProgramService programService;
    private final OrganisationUnitService organisationUnitService;
    private final SystemInfoStore systemInfoStore;
    private final ResourceStore resourceStore;
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final UserRoleStore userRoleStore;
    private final UserRoleProgramLinkStore userRoleProgramLinkStore;
    private final OrganisationUnitStore organisationUnitStore;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final ProgramStore programStore;
    private final ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    private final ProgramRuleVariableStore programRuleVariableStore;
    private final ProgramIndicatorStore programIndicatorStore;
    private final ProgramStageSectionProgramIndicatorLinkStore
            programStageSectionProgramIndicatorLinkStore;
    private final ProgramRuleActionStore programRuleActionStore;
    private final ProgramRuleStore programRuleStore;
    private final ProgramStageDataElementStore programStageDataElementStore;
    private final ProgramStageSectionStore programStageSectionStore;
    private final ProgramStageStore programStageStore;

    private final CategoryQuery categoryQuery;
    private final CategoryComboQuery categoryComboQuery;
    private final CategoryService categoryService;
    private final CategoryComboService categoryComboService;
    private final CategoryHandler categoryHandler;
    private final CategoryComboHandler categoryComboHandler;

    private final DataElementFactory dataElementFactory;
    private final OptionSetFactory optionSetFactory;
    private final TrackedEntityFactory trackedEntityFactory;
    private final TrackedEntityAttributeFactory trackedEntityAttributeFactory;
    private final RelationshipTypeFactory relationshipTypeFactory;

    private boolean isExecuted;

    private final OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore;

    public MetadataCall(@NonNull DatabaseAdapter databaseAdapter,
            @NonNull SystemInfoService systemInfoService,
            @NonNull UserService userService,
            @NonNull ProgramService programService,
            @NonNull OrganisationUnitService organisationUnitService,
            @NonNull SystemInfoStore systemInfoStore,
            @NonNull ResourceStore resourceStore,
            @NonNull UserStore userStore,
            @NonNull UserCredentialsStore userCredentialsStore,
            @NonNull UserRoleStore userRoleStore,
            @NonNull UserRoleProgramLinkStore userRoleProgramLinkStore,
            @NonNull OrganisationUnitStore organisationUnitStore,
            @NonNull UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            @NonNull ProgramStore programStore,
            @NonNull ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore,
            @NonNull ProgramRuleVariableStore programRuleVariableStore,
            @NonNull ProgramIndicatorStore programIndicatorStore,
            @NonNull ProgramStageSectionProgramIndicatorLinkStore
                    programStageSectionProgramIndicatorLinkStore,
            @NonNull ProgramRuleActionStore programRuleActionStore,
            @NonNull ProgramRuleStore programRuleStore,
            @NonNull ProgramStageDataElementStore programStageDataElementStore,
            @NonNull ProgramStageSectionStore programStageSectionStore,
            @NonNull ProgramStageStore programStageStore,
            @NonNull OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore,
            @NonNull CategoryQuery categoryQuery,
            @NonNull CategoryService categoryService,
            @NonNull CategoryHandler categoryHandler,
            @NonNull CategoryComboQuery categoryComboQuery,
            @NonNull CategoryComboService categoryComboService,
            @NonNull CategoryComboHandler categoryComboHandler,
            @NonNull OptionSetFactory optionSetFactory,
            @NonNull TrackedEntityFactory trackedEntityFactory,
            @NonNull TrackedEntityAttributeFactory trackedEntityAttributeFactory,
            @NonNull DataElementFactory dataElementFactory,
            @Nonnull RelationshipTypeFactory relationshipTypeFactory) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoService = systemInfoService;
        this.userService = userService;
        this.programService = programService;
        this.organisationUnitService = organisationUnitService;
        this.systemInfoStore = systemInfoStore;
        this.resourceStore = resourceStore;
        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.userRoleStore = userRoleStore;
        this.userRoleProgramLinkStore = userRoleProgramLinkStore;
        this.organisationUnitStore = organisationUnitStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.programStore = programStore;
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
        this.organisationUnitProgramLinkStore = organisationUnitProgramLinkStore;
        this.categoryQuery = categoryQuery;
        this.categoryService = categoryService;
        this.categoryHandler = categoryHandler;
        this.categoryComboQuery = categoryComboQuery;
        this.categoryComboService = categoryComboService;
        this.categoryComboHandler = categoryComboHandler;

        this.optionSetFactory = optionSetFactory;
        this.trackedEntityFactory = trackedEntityFactory;
        this.trackedEntityAttributeFactory = trackedEntityAttributeFactory;
        this.dataElementFactory = dataElementFactory;
        this.relationshipTypeFactory = relationshipTypeFactory;
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
            SystemInfo systemInfo = (SystemInfo) response.body();
            Date serverDate = systemInfo.serverDate();


            response = new UserCall(
                    userService,
                    databaseAdapter,
                    userStore,
                    userCredentialsStore,
                    userRoleStore,
                    resourceStore,
                    serverDate,
                    userRoleProgramLinkStore
            ).call();
            if (!response.isSuccessful()) {
                return response;
            }

            User user = (User) response.body();
            response = new OrganisationUnitCall(
                    user, organisationUnitService, databaseAdapter, organisationUnitStore,
                    resourceStore, serverDate, userOrganisationUnitLinkStore,
                    organisationUnitProgramLinkStore).call();
            if (!response.isSuccessful()) {
                return response;
            }
            response = downloadCategories(serverDate);

            if (!response.isSuccessful()) {
                return response;
            }
            response = downloadCategoryCombos(serverDate);

            if (!response.isSuccessful()) {
                return response;
            }

            Set<String> programUids = getAssignedProgramUids(user);
            response = new ProgramCall(
                    programService, databaseAdapter, resourceStore, programUids, programStore,
                    serverDate,
                    trackedEntityAttributeFactory.getTrackedEntityAttributeStore(),
                    programTrackedEntityAttributeStore,
                    programRuleVariableStore,
                    programIndicatorStore, programStageSectionProgramIndicatorLinkStore,
                    programRuleActionStore,
                    programRuleStore, optionSetFactory,
                    dataElementFactory.getDataElementStore(),
                    programStageDataElementStore,
                    programStageSectionStore, programStageStore,
                    relationshipTypeFactory.getRelationshipTypeStore()
            ).call();
            if (!response.isSuccessful()) {
                return response;
            }

            List<Program> programs = ((Response<Payload<Program>>) response).body().items();

            Set<String> trackedEntityUids = getAssignedTrackedEntityUids(programs);

            response = trackedEntityFactory.newEndPointCall(trackedEntityUids, serverDate).call();

            if (!response.isSuccessful()) {
                return response;
            }

            Set<String> optionSetUids = getAssignedOptionSetUids(programs);
            response = optionSetFactory.newEndPointCall(optionSetUids, serverDate).call();

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

    private Set<String> getAssignedProgramUids(User user) {
        if (user == null || user.userCredentials() == null
                || user.userCredentials().userRoles() == null) {
            return null;
        }

        Set<String> programUids = new HashSet<>();

        getProgramUidsFromUserRoles(user, programUids);
        getProgramUidsFromOrganisationUnits(user, programUids);

        return programUids;
    }

    private void getProgramUidsFromOrganisationUnits(User user, Set<String> programUids) {
        List<OrganisationUnit> organisationUnits = user.organisationUnits();

        if (organisationUnits != null) {
            int size = organisationUnits.size();
            for (int i = 0; i < size; i++) {
                OrganisationUnit organisationUnit = organisationUnits.get(i);

                int programSize = organisationUnit.programs().size();
                for (int j = 0; j < programSize; j++) {
                    Program program = organisationUnit.programs().get(j);

                    programUids.add(program.uid());
                }
            }
        }
    }

    private void getProgramUidsFromUserRoles(User user, Set<String> programUids) {
        List<UserRole> userRoles = user.userCredentials().userRoles();
        if (userRoles != null) {
            int size = userRoles.size();
            for (int i = 0; i < size; i++) {
                UserRole userRole = userRoles.get(i);

                int programSize = userRole.programs().size();
                for (int j = 0; j < programSize; j++) {
                    Program program = userRole.programs().get(j);

                    programUids.add(program.uid());
                }
            }
        }
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
