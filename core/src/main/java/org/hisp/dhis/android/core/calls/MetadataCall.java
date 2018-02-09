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
import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryComboQuery;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectEndPointCall;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandler;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectService;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramFactory;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;
import org.hisp.dhis.android.core.user.UserHandler;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserService;

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
    private final SystemInfoStore systemInfoStore;
    private final ResourceStore resourceStore;
    private final UserHandler userHandler;

    private final OptionSetFactory optionSetFactory;
    private final TrackedEntityFactory trackedEntityFactory;
    private final CategoryFactory categoryFactory;
    private final ProgramFactory programFactory;
    private final OrganisationUnitFactory organisationUnitFactory;
    private final CategoryComboFactory categoryComboFactory;
    private final DeletedObjectService deletedObjectService;
    private final DeletedObjectHandler deletedObjectHandler;

    private boolean isExecuted;

    public MetadataCall(@NonNull DatabaseAdapter databaseAdapter,
            @NonNull SystemInfoService systemInfoService,
            @NonNull UserService userService,
            @Nonnull UserHandler userHandler,
            @NonNull SystemInfoStore systemInfoStore,
            @NonNull ResourceStore resourceStore,
            @NonNull OptionSetFactory optionSetFactory,
            @NonNull TrackedEntityFactory trackedEntityFactory,
            @Nonnull ProgramFactory programFactory,
            @NonNull OrganisationUnitFactory organisationUnitFactory,
            @NonNull CategoryFactory categoryFactory,
            @NonNull CategoryComboFactory categoryComboFactory,
            @NonNull DeletedObjectHandler deletedObjectHandler) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoService = systemInfoService;
        this.userService = userService;
        this.userHandler = userHandler;
        this.systemInfoStore = systemInfoStore;
        this.resourceStore = resourceStore;

        this.optionSetFactory = optionSetFactory;
        this.trackedEntityFactory = trackedEntityFactory;
        this.programFactory = programFactory;
        this.organisationUnitFactory = organisationUnitFactory;
        this.categoryFactory = categoryFactory;
        this.categoryComboFactory = categoryComboFactory;
        this.deletedObjectService = deletedObjectService;
        this.deletedObjectHandler = deletedObjectHandler;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    @SuppressWarnings({"PMD.NPathComplexity"})
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

            response = syncDeletedObject(serverDate, User.class.getSimpleName());

            if (!response.isSuccessful()) {
                return response;
            }

            response = new UserCall(
                    userService,
                    databaseAdapter,
                    userHandler,
                    serverDate
            ).call();

            if (!response.isSuccessful()) {
                return response;
            }

            @SuppressWarnings({"PMD.PrematureDeclaration"})
            User user = (User) response.body();

            response = syncDeletedObject(serverDate, OrganisationUnit.class.getSimpleName());

            if (!response.isSuccessful()) {
                return response;
            }

            response = getOrganisationUnits(serverDate, user);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncCategories(serverDate);

            if(!response.isSuccessful()){
                return response;
            }

            response = syncPrograms(serverDate, user);

            if(!response.isSuccessful()){
                return response;
            }

            List<Program> programs = ((Response<Payload<Program>>) response).body().items();

            response = syncTrackedEntities(serverDate, programs);

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncOptionSets(serverDate, programs);

            if (!response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();
            return response;
        } finally {
            transaction.end();
        }
    }

    private Response syncOptionSets(Date serverDate, List<Program> programs) throws Exception {
        Response response = syncDeletedObject(serverDate,
                Option.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }
        response = syncDeletedObject(serverDate, OptionSet.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        Set<String> optionSetUids = getAssignedOptionSetUids(programs);
        response = optionSetFactory.newEndPointCall(optionSetUids,
                serverDate).call();

        return response;
    }

    private Response syncTrackedEntities(Date serverDate, List<Program> programs) throws Exception {
        Response response = syncDeletedObject(serverDate, TrackedEntity.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        Set<String> trackedEntityUids = getAssignedTrackedEntityUids(programs);

        response = trackedEntityFactory.newEndPointCall(trackedEntityUids, serverDate).call();


        return response;
    }
    private Response syncCategories(Date serverDate)
            throws Exception {

        Response response = syncDeletedObject(serverDate, Category.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, CategoryOption.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = categoryFactory.newEndPointCall(CategoryQuery.defaultQuery(),
                serverDate).call();

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, CategoryCombo.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }


        response = syncDeletedObject(serverDate, CategoryOptionCombo.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = categoryComboFactory.newEndPointCall(CategoryComboQuery.defaultQuery(),
                serverDate).call();

        return response;
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private Response syncPrograms(Date serverDate, User user)
            throws Exception {
        Response response = syncDeletedObject(serverDate, ProgramRule.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramRuleAction.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramRuleVariable.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramIndicator.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate,
                DataElement.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramStage.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramStageDataElement.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramStageSection.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, ProgramTrackedEntityAttribute.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, TrackedEntityAttribute.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, RelationshipType.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        response = syncDeletedObject(serverDate, Program.class.getSimpleName());

        if (!response.isSuccessful()) {
            return response;
        }

        Set<String> programUids = getAssignedProgramUids(user);

        response = programFactory.newEndPointCall(programUids,
                serverDate).call();
        return response;
    }

    private Response<Payload<DeletedObject>> syncDeletedObject(Date serverDate, String klass) throws Exception {
        return new DeletedObjectEndPointCall(deletedObjectService, resourceStore,
                deletedObjectHandler, serverDate, klass).call();
    }

    public Response getOrganisationUnits(Date serverDate, User user) throws Exception {
        Response response;
        response = organisationUnitFactory.newEndPointCall(serverDate, user, "").call();
        return response;
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
}
