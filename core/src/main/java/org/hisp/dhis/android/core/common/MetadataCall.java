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
package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElementHandler;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.option.OptionHandler;
import org.hisp.dhis.android.core.option.OptionSetCall;
import org.hisp.dhis.android.core.option.OptionSetHandler;
import org.hisp.dhis.android.core.option.OptionSetService;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramCall;
import org.hisp.dhis.android.core.program.ProgramHandler;
import org.hisp.dhis.android.core.program.ProgramIndicatorHandler;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramIndicatorStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleActionHandler;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleHandler;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleStoreImpl;
import org.hisp.dhis.android.core.program.ProgramRuleVariableHandler;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModelStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModelStoreImpl;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementHandler;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageHandler;
import org.hisp.dhis.android.core.program.ProgramStageSectionHandler;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStageStoreImpl;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramStoreImpl;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeHandler;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipTypeHandler;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;
import org.hisp.dhis.android.core.user.UserCredentialsHandler;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.user.UserHandler;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserRoleHandler;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkStore;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserRoleStore;
import org.hisp.dhis.android.core.user.UserRoleStoreImpl;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("PMD.ExcessiveImports")
public class MetadataCall {
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;


    public MetadataCall(DatabaseAdapter databaseAdapter, Retrofit retrofit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
    }

    public void call() throws Exception {
        ResourceHandler resourceHandler = initializeResourceHandler();
        OrganisationUnitHandler organisationUnitHandler = initializeOrganisationUnitHandler();

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            // initialize SystemInfoCall and call the api
            SystemInfoCall systemInfoCall = initializeSystemInfoCall();
            Response<SystemInfo> systemInfoResponse = systemInfoCall.call();
            SystemInfo systemInfo = systemInfoResponse.body();
            Date serverDate = systemInfo.serverDate();

            // initialize userCall and call the api
            UserCall userCall = initializeUserCall(organisationUnitHandler, resourceHandler, serverDate);
            Response<User> userResponse = userCall.call();
            User user = userResponse.body();

            // get assigned program uids from user roles and user's data capture organisation units
            Set<String> programUids = new HashSet<>();
            if (user.userCredentials() != null && user.userCredentials().userRoles() != null &&
                    user.organisationUnits() != null) {

                programUids = getAssignedProgramUids(
                        user.userCredentials().userRoles(), user.organisationUnits()
                );
            }

            OrganisationUnitCall organisationUnitCall = initializeOrganisationUnitCall(
                    organisationUnitHandler, resourceHandler, user, serverDate
            );

            organisationUnitCall.call();

            ProgramCall programCall = initializeProgramCall(programUids, resourceHandler, serverDate);
            Response<Payload<Program>> programResponse = programCall.call();
            List<Program> programs = programResponse.body().items;

            Set<String> trackedEntityUids = new HashSet<>();
            Set<String> optionSetUids = new HashSet<>();

            // get assigned tracked entity uids and option set uids
            if (programs != null) {
                trackedEntityUids = getAssignedTrackedEntityUids(programs);
                optionSetUids = getAssignedOptionSetUids(programs);
            }

            TrackedEntityCall trackedEntityCall = initializeTrackedEntityCall(
                    trackedEntityUids, resourceHandler, serverDate
            );

            trackedEntityCall.call();

            OptionSetCall optionSetCall = initializeOptionSetCall(resourceHandler, optionSetUids, serverDate);
            optionSetCall.call();

            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    private Set<String> getAssignedOptionSetUids(List<Program> programs) {
        Set<String> uids = new HashSet<>();
        int size = programs.size();
        for (int i = 0; i < size; i++) {
            Program program = programs.get(i);
            int programTrackedEntityAttributeSize = program.programTrackedEntityAttributes().size();
            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                    program.programTrackedEntityAttributes();

            for (int j = 0; j < programTrackedEntityAttributeSize; j++) {
                ProgramTrackedEntityAttribute programTrackedEntityAttribute = programTrackedEntityAttributes.get(j);

                if (programTrackedEntityAttribute.trackedEntityAttribute() != null &&
                        programTrackedEntityAttribute.trackedEntityAttribute().optionSet() != null) {
                    uids.add(programTrackedEntityAttribute.trackedEntityAttribute().optionSet().uid());
                }
            }

            List<ProgramStage> programStages = program.programStages();
            int programStagesSize = programStages.size();

            for (int j = 0; j < programStagesSize; j++) {
                ProgramStage programStage = programStages.get(j);
                List<ProgramStageDataElement> programStageDataElements = programStage.programStageDataElements();
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

        return uids;
    }

    private Set<String> getAssignedTrackedEntityUids(List<Program> programs) {
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

    private Set<String> getAssignedProgramUids(List<UserRole> userRoles, List<OrganisationUnit> organisationUnits) {
        Set<String> programUids = new HashSet<>();

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

        return programUids;
    }

    private ResourceHandler initializeResourceHandler() {
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter);
        return new ResourceHandler(resourceStore);
    }

    private OrganisationUnitHandler initializeOrganisationUnitHandler() {
        OrganisationUnitStore organisationUnitStore = new OrganisationUnitStoreImpl(databaseAdapter);
        UserOrganisationUnitLinkStore userOrganisationUnitStore =
                new UserOrganisationUnitLinkStoreImpl(databaseAdapter);

        return new OrganisationUnitHandler(
                organisationUnitStore, userOrganisationUnitStore
        );
    }


    private SystemInfoCall initializeSystemInfoCall() {
        SystemInfoService systemInfoService = retrofit.create(SystemInfoService.class);
        SystemInfoStore systemInfoStore = new SystemInfoStoreImpl(databaseAdapter);
        SystemInfoHandler systemInfoHandler = new SystemInfoHandler(systemInfoStore);
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter);
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        return new SystemInfoCall(
                databaseAdapter, systemInfoHandler, systemInfoService, resourceHandler
        );
    }


    private UserCall initializeUserCall(OrganisationUnitHandler organisationUnitHandler,
                                        ResourceHandler resourceHandler,
                                        Date serverDate) {
        UserService userService = retrofit.create(UserService.class);

        UserCredentialsStore userCredentialsStore = new UserCredentialsStoreImpl(databaseAdapter);
        UserRoleStore userRoleStore = new UserRoleStoreImpl(databaseAdapter);
        UserStore userStore = new UserStoreImpl(databaseAdapter);
        UserRoleProgramLinkStore userRoleProgramLinkStore = new UserRoleProgramLinkStoreImpl(databaseAdapter);


        UserHandler userHandler = new UserHandler(userStore);
        UserCredentialsHandler userCredentialsHandler = new UserCredentialsHandler(userCredentialsStore);
        UserRoleHandler userRoleHandler = new UserRoleHandler(userRoleStore, userRoleProgramLinkStore);


        return new UserCall(userService, databaseAdapter, organisationUnitHandler,
                userHandler, userCredentialsHandler, userRoleHandler, resourceHandler, serverDate);
    }

    private OrganisationUnitCall initializeOrganisationUnitCall(OrganisationUnitHandler organisationUnitHandler,
                                                                ResourceHandler resourceHandler,
                                                                User user,
                                                                Date serverDate) {
        OrganisationUnitService organisationUnitService = retrofit.create(OrganisationUnitService.class);


        return new OrganisationUnitCall(user, organisationUnitService, databaseAdapter,
                organisationUnitHandler, resourceHandler, serverDate);
    }

    private ProgramCall initializeProgramCall(Set<String> uids, ResourceHandler resourceHandler, Date serverDate) {
        TrackedEntityAttributeStore trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter);
        TrackedEntityAttributeHandler trackedEntityAttributeHandler =
                new TrackedEntityAttributeHandler(trackedEntityAttributeStore);

        ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore =
                new ProgramTrackedEntityAttributeStoreImpl(databaseAdapter);

        ProgramTrackedEntityAttributeHandler programTrackedEntityAttributeHandler =
                new ProgramTrackedEntityAttributeHandler(
                        programTrackedEntityAttributeStore,
                        trackedEntityAttributeHandler
                );

        ProgramRuleVariableModelStore programRuleVariableStore =
                new ProgramRuleVariableModelStoreImpl(databaseAdapter);
        ProgramRuleVariableHandler programRuleVariableHandler =
                new ProgramRuleVariableHandler(programRuleVariableStore);

        ProgramIndicatorStore programIndicatorStore = new ProgramIndicatorStoreImpl(databaseAdapter);
        ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore =
                new ProgramStageSectionProgramIndicatorLinkStoreImpl(databaseAdapter);
        ProgramIndicatorHandler programIndicatorHandler = new ProgramIndicatorHandler(
                programIndicatorStore,
                programStageSectionProgramIndicatorLinkStore
        );

        ProgramRuleActionStore programRuleActionStore = new ProgramRuleActionStoreImpl(databaseAdapter);
        ProgramRuleActionHandler programRuleActionHandler = new ProgramRuleActionHandler(programRuleActionStore);
        ProgramRuleStore programRuleStore = new ProgramRuleStoreImpl(databaseAdapter);
        ProgramRuleHandler programRuleHandler = new ProgramRuleHandler(programRuleStore, programRuleActionHandler);

        OptionStore optionStore = new OptionStoreImpl(databaseAdapter);
        OptionHandler optionHandler = new OptionHandler(optionStore);

        OptionSetStore optionSetStore = new OptionSetStoreImpl(databaseAdapter);
        OptionSetHandler optionSetHandler = new OptionSetHandler(optionSetStore, optionHandler);


        DataElementStore dataElementStore = new DataElementStoreImpl(databaseAdapter);
        DataElementHandler dataElementHandler = new DataElementHandler(dataElementStore, optionSetHandler);
        ProgramStageDataElementStore programStageDataElementStore =
                new ProgramStageDataElementStoreImpl(databaseAdapter);

        ProgramStageDataElementHandler programStageDataElementHandler = new ProgramStageDataElementHandler(
                programStageDataElementStore, dataElementHandler
        );


        ProgramStageSectionStore programStageSectionStore = new ProgramStageSectionStoreImpl(databaseAdapter);
        ProgramStageSectionHandler programStageSectionHandler = new ProgramStageSectionHandler(
                programStageSectionStore,
                programStageDataElementHandler,
                programIndicatorHandler

        );

        ProgramStageStore programStageStore = new ProgramStageStoreImpl(databaseAdapter);
        ProgramStageHandler programStageHandler = new ProgramStageHandler(
                programStageStore,
                programStageSectionHandler,
                programStageDataElementHandler
        );

        RelationshipTypeStore relationshipStore = new RelationshipTypeStoreImpl(databaseAdapter);
        RelationshipTypeHandler relationshipTypeHandler = new RelationshipTypeHandler(relationshipStore);
        ProgramService programService = retrofit.create(ProgramService.class);
        ProgramStore programStore = new ProgramStoreImpl(databaseAdapter);


        ProgramHandler programHandler = new ProgramHandler(
                programStore,
                programRuleVariableHandler,
                programStageHandler,
                programIndicatorHandler,
                programRuleHandler,
                programTrackedEntityAttributeHandler,
                relationshipTypeHandler);


        return new ProgramCall(
                programService, databaseAdapter, resourceHandler, uids, programHandler, serverDate
        );
    }

    private TrackedEntityCall initializeTrackedEntityCall(Set<String> uids,
                                                          ResourceHandler resourceHandler,
                                                          Date serverDate) {
        TrackedEntityService service = retrofit.create(TrackedEntityService.class);
        TrackedEntityStore trackedEntityStore = new TrackedEntityStoreImpl(databaseAdapter);
        TrackedEntityHandler trackedEntityHandler = new TrackedEntityHandler(trackedEntityStore);

        return new TrackedEntityCall(
                uids, databaseAdapter, trackedEntityHandler, resourceHandler, service, serverDate
        );
    }

    private OptionSetCall initializeOptionSetCall(ResourceHandler resourceHandler, Set<String> uids, Date serverDate) {
        OptionSetService optionSetService = retrofit.create(OptionSetService.class);
        OptionSetStore optionSetStore = new OptionSetStoreImpl(databaseAdapter);
        OptionStore optionStore = new OptionStoreImpl(databaseAdapter);
        OptionHandler optionHandler = new OptionHandler(optionStore);
        OptionSetHandler optionSetHandler = new OptionSetHandler(optionSetStore, optionHandler);

        return new OptionSetCall(
                optionSetService, optionSetHandler, databaseAdapter, resourceHandler, uids, serverDate
        );
    }


}
