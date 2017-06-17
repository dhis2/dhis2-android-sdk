/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.controllers.metadata;

import static org.hisp.dhis.android.sdk.utils.NetworkUtils.unwrapResponse;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.ApiEndpointContainer;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.LoadingController;
import org.hisp.dhis.android.sdk.controllers.ResourceController;
import org.hisp.dhis.android.sdk.controllers.SyncStrategy;
import org.hisp.dhis.android.sdk.controllers.wrappers.AssignedProgramsWrapper;
import org.hisp.dhis.android.sdk.controllers.wrappers.OptionSetWrapper;
import org.hisp.dhis.android.sdk.controllers.wrappers.ProgramWrapper;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.Attribute;
import org.hisp.dhis.android.sdk.persistence.models.Attribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.Constant$Table;
import org.hisp.dhis.android.sdk.persistence.models.Dashboard;
import org.hisp.dhis.android.sdk.persistence.models.DashboardElement;
import org.hisp.dhis.android.sdk.persistence.models.DashboardItem;
import org.hisp.dhis.android.sdk.persistence.models.DashboardItemContent;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataElement$Table;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportCount;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.Interpretation;
import org.hisp.dhis.android.sdk.persistence.models.InterpretationComment;
import org.hisp.dhis.android.sdk.persistence.models.InterpretationElement;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.Option$Table;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet$Table;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit$Table;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship$Table;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicatorToSectionRelationship;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicatorToSectionRelationship$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType;
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType$Table;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeGeneratedValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeGeneratedValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeGroup;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.persistence.models.User;
import org.hisp.dhis.android.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.DbUtils;
import org.hisp.dhis.android.sdk.utils.UiUtils;
import org.hisp.dhis.android.sdk.utils.Utils;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public final class MetaDataController extends ResourceController {
    private final static String CLASS_TAG = "MetaDataController";
    private final static long TRACKED_ENTITY_ATTRITBUTE_GENERATED_VALUE_THRESHOLD = 100;

    private MetaDataController() {
    }

    /**
     * Returns false if some meta data flags that have been enabled have not been downloaded.
     *
     * @param context
     * @return
     */
    public static boolean isDataLoaded(Context context) {
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.ASSIGNEDPROGRAMS)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.ASSIGNEDPROGRAMS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.OPTIONSETS)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.OPTIONSETS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.TRACKEDENTITYATTRIBUTEGROUPS)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTEGROUPS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.TRACKEDENTITYATTRIBUTES)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.CONSTANTS)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.CONSTANTS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULES)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.PROGRAMRULES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEVARIABLES)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.PROGRAMRULEVARIABLES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEACTIONS)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.PROGRAMRULEACTIONS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.RELATIONSHIPTYPES)) {
            if (DateTimeManager.getInstance().getLastUpdated(ResourceType.RELATIONSHIPTYPES) == null) {
                return false;
            }
        }
        Log.d(CLASS_TAG, "Meta data is loaded!");
        return true;
    }

    public static List<RelationshipType> getRelationshipTypes() {
        return new Select().from(RelationshipType.class).queryList();
    }

    public static RelationshipType getRelationshipType(String relation) {
        return new Select().from(RelationshipType.class).where(Condition.column(RelationshipType$Table.ID).is(relation)).querySingle();
    }

    public static List<Option> getOptions(String optionSetId) {
        return new Select().from(Option.class).where(Condition.column(Option$Table.OPTIONSET).is(optionSetId)).orderBy(Option$Table.SORTINDEX).queryList();
    }

    public static List<ProgramStageSection> getProgramStageSections(String programStageId) {
        return new Select().from(ProgramStageSection.class).where(Condition.column
                (ProgramStageSection$Table.PROGRAMSTAGE).is(programStageId)).
                orderBy(true, ProgramStageSection$Table.SORTORDER).queryList();
    }

    public static List<ProgramStageDataElement> getProgramStageDataElements(ProgramStageSection section) {
        if (section == null) return null;
        return new Select().from(ProgramStageDataElement.class).where(Condition.column
                (ProgramStageDataElement$Table.PROGRAMSTAGESECTION).is(section.getUid())).orderBy
                (ProgramStageDataElement$Table.SORTORDER).queryList();
    }

    public static List<ProgramStageDataElement> getProgramStageDataElements(ProgramStage programStage) {
        if (programStage == null) return null;
        return new Select().from(ProgramStageDataElement.class).where(Condition.column
                (ProgramStageDataElement$Table.PROGRAMSTAGE).is(programStage.getUid())).orderBy
                (ProgramStageDataElement$Table.SORTORDER).queryList();
    }

    /**
     * Get every entry in Attribute table as a List of Attribute objects
     *
     * @return List of Attribute objects with all the Attribute table content
     */
    public static List<Attribute> getAttributes() {
        return new Select().from(Attribute.class)
                .orderBy(Attribute$Table.ID).queryList();
    }

    /**
     * Get every entry in AttributeValue table as a List of AttributeValue objects
     *
     * @return List of AttributeValue objects with all the AttributeValue content
     */
    public static List<AttributeValue> getAttributeValues() {
        return new Select().from(AttributeValue.class)
                .orderBy(AttributeValue$Table.ID).queryList();
    }

    /**
     * Get all the AttributeValues that belongs to a given DataElement
     *
     * @param dataElement to get the Attributes from
     * @return List of AttributeValue objects that belongs to the given DataElement
     */
    public static List<AttributeValue> getAttributeValues(DataElement dataElement) {
        if (dataElement == null) return null;
        return new Select().from(AttributeValue.class)
                .where(Condition.column(AttributeValue$Table.DATAELEMENT).is(dataElement.getUid()))
                .orderBy(AttributeValue$Table.ID).queryList();
    }

    /**
     * Get a concrete AttributeValue entry given its id
     *
     * @param id PK of the AttributeValue table
     * @return The AttributeValue object or null if not found
     */
    public static AttributeValue getAttributeValue(Long id) {
        if (id == null) return null;
        return new Select().from(AttributeValue.class)
                .where(Condition.column(AttributeValue$Table.ID).is(id)).querySingle();
    }

    /**
     * Get a concrete Attribute entry given its string ID
     *
     * @param attributeId The ID used in DHIS2 to identify the Attribute
     * @return The Attribute object or null if not found
     */
    public static Attribute getAttribute(String attributeId) {
        if (attributeId == null) return null;
        return new Select().from(Attribute.class)
                .where(Condition.column(Attribute$Table.ID).is(attributeId)).querySingle();
    }

    /**
     * returns a tracked Entity object for the given ID
     *
     * @param trackedEntity
     * @return
     */
    public static TrackedEntity getTrackedEntity(String trackedEntity) {
        return new Select().from(TrackedEntity.class).where(Condition.column
                (TrackedEntity$Table.ID).is(trackedEntity)).querySingle();
    }

    /**
     * Returns a list of ProgramTrackedEntityAttributes for the given program.
     *
     * @param program
     * @return
     */
    public static List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes(String program) {
        return new Select().from(ProgramTrackedEntityAttribute.class).where(Condition.column
                (ProgramTrackedEntityAttribute$Table.PROGRAM).is(program)).orderBy(true,
                ProgramTrackedEntityAttribute$Table.SORTORDER).queryList();
    }

    /**
     * Returns a list of programs assigned to the given organisation unit id
     *
     * @param organisationUnitId
     * @param kinds              set to null to get all programs. Else get kinds Strings from Program.
     * @return
     */
    public static List<Program> getProgramsForOrganisationUnit(String organisationUnitId,
                                                               ProgramType... kinds) {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships =
                new Select().from(OrganisationUnitProgramRelationship.class).where(
                        Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).
                                is(organisationUnitId)).queryList();

        List<Program> programs = new ArrayList<Program>();
        for (OrganisationUnitProgramRelationship oupr : organisationUnitProgramRelationships) {
            if (kinds != null) {
                for (ProgramType kind : kinds) {
                    List<Program> plist = new Select().from(Program.class).where(
                            Condition.column(Program$Table.ID).is(oupr.getProgramId())).and(
                            Condition.column(Program$Table.PROGRAMTYPE).is(kind.toString())).queryList();
                    programs.addAll(plist);
                }
            }
        }
        return programs;
    }

    public static List<ProgramStage> getProgramStages(String program) {
        return new Select().from(ProgramStage.class).where(
                Condition.column(ProgramStage$Table.PROGRAM).is(program)).orderBy(
                ProgramStage$Table.SORTORDER).queryList();
    }

    /**
     * Returns a program stage for a given program stage uid
     *
     * @param programStageUid
     * @return
     */
    public static ProgramStage getProgramStage(String programStageUid) {
        return new Select().from(ProgramStage.class).where(
                Condition.column(ProgramStage$Table.ID).is(programStageUid)).querySingle();
    }

    public static TrackedEntityAttribute getTrackedEntityAttribute(String trackedEntityAttributeId) {
        return new Select().from(TrackedEntityAttribute.class).where(Condition.column
                (TrackedEntityAttribute$Table.ID).is(trackedEntityAttributeId)).querySingle();
    }

    public static List<TrackedEntityAttribute> getTrackedEntityAttributes() {
        return new Select().from(TrackedEntityAttribute.class).queryList();
    }

    public static List<TrackedEntityInstance> getTrackedEntityInstancesFromServer() {
        return new Select().from(TrackedEntityInstance.class).where(Condition.column(
                TrackedEntityInstance$Table.FROMSERVER).is(true)).queryList();
    }

    public static List<TrackedEntityAttributeGroup> getTrackedEntityAttributeGroups() {
        return new Select().from(TrackedEntityAttributeGroup.class).queryList();
    }

    public static List<TrackedEntityAttributeGeneratedValue> getTrackedEntityAttributeGeneratedValues() {
        return new Select().from(TrackedEntityAttributeGeneratedValue.class).queryList();
    }

    /**
     * Returns a constant with the given uid
     *
     * @param id
     * @return
     */
    public static Constant getConstant(String id) {
        return new Select().from(Constant.class).where
                (Condition.column(Constant$Table.ID).is(id)).querySingle();
    }

    /**
     * returns a list of all constants
     *
     * @return
     */
    public static List<Constant> getConstants() {
        return new Select().from(Constant.class).queryList();
    }

    public static List<ProgramRule> getProgramRules() {
        return new Select().from(ProgramRule.class).queryList();
    }

    public static List<ProgramRuleVariable> getProgramRuleVariables() {
        return new Select().from(ProgramRuleVariable.class).queryList();
    }

    public static List<ProgramRuleAction> getProgramRuleActions() {
        return new Select().from(ProgramRuleAction.class).queryList();
    }

    public static ProgramRuleVariable getProgramRuleVariable(String id) {
        return new Select().from(ProgramRuleVariable.class).where(Condition.column(ProgramRuleVariable$Table.ID).is(id)).querySingle();
    }

    public static ProgramRuleVariable getProgramRuleVariableByName(String name) {
        return new Select().from(ProgramRuleVariable.class).where(Condition.column(ProgramRuleVariable$Table.NAME).is(name)).querySingle();
    }

    /**
     * Returns a list of IDs for all assigned programs.
     *
     * @return
     */
    public static List<String> getAssignedPrograms() {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships = new Select().from(OrganisationUnitProgramRelationship.class).queryList();
        List<String> assignedPrograms = new ArrayList<>();
        for (OrganisationUnitProgramRelationship relationship : organisationUnitProgramRelationships) {
            if (!assignedPrograms.contains(relationship.getProgramId()))
                assignedPrograms.add(relationship.getProgramId());
        }
        return assignedPrograms;
    }

    public static OrganisationUnit getOrganisationUnit(String id) {
        return new Select().from(OrganisationUnit.class).where(Condition.column(OrganisationUnit$Table.ID).is(id)).querySingle();
    }

    public static SystemInfo getSystemInfo() {
        return new Select().from(SystemInfo.class).querySingle();
    }

    public static Program getProgram(String programId) {
        if (programId == null) return null;
        return new Select().from(Program.class).where(Condition.column(Program$Table.ID).
                is(programId)).querySingle();
    }

    /**
     * Returns a list of organisation units assigned to the current user
     *
     * @return
     */
    public static List<OrganisationUnit> getAssignedOrganisationUnits() {
        List<OrganisationUnit> organisationUnits = new Select().from(OrganisationUnit.class)
                .where(Condition.column(OrganisationUnit$Table.TYPE).eq(OrganisationUnit.TYPE.ASSIGNED))
                .queryList();
        return organisationUnits;
    }

    public static List<OrganisationUnitProgramRelationship> getOrganisationUnitProgramRelationships() {
        return new Select().from(OrganisationUnitProgramRelationship.class).queryList();
    }

    public static List<DataElement> getDataElements() {
        return new Select().from(DataElement.class).queryList();
    }

    /**
     * Returns the data element for the given uid or null if the dataElement does not exist
     *
     * @param dataElementId
     * @return
     */
    public static DataElement getDataElement(String dataElementId) {
        return new Select().from(DataElement.class).where(Condition.column(DataElement$Table.ID).
                is(dataElementId)).querySingle();
    }

    /**
     * Returns a User object for the currently logged in user.
     *
     * @return
     */
    public static User getUser() {
        return new Select().from(User.class).querySingle();
    }

    /**
     * Returns a UserAccount object for the currently logged in user.
     *
     * @return
     */
    public static UserAccount getUserAccount() {
        return new Select().from(UserAccount.class).querySingle();
    }

    /**
     * Returns an option set for the given Id or null of the option set doesn't exist.
     *
     * @param optionSetId
     * @return
     */
    public static OptionSet getOptionSet(String optionSetId) {
        return new Select().from(OptionSet.class).where(Condition.column(OptionSet$Table.ID).
                is(optionSetId)).querySingle();
    }

    public static List<OptionSet> getOptionSets() {
        return new Select().from(OptionSet.class).queryList();
    }

    public static List<ProgramIndicator> getProgramIndicatorsByProgram(String program) {
        return new Select()
                .from(ProgramIndicator.class)
                .where(Condition.column(ProgramIndicator$Table
                        .PROGRAM).is(program))
                .queryList();
    }

    public static List<ProgramIndicator> getProgramIndicatorsByProgramStage(String programStage) {
        List<ProgramIndicatorToSectionRelationship> relations = new Select()
                .from(ProgramIndicatorToSectionRelationship.class)
                .where(Condition.column(ProgramIndicatorToSectionRelationship$Table
                        .PROGRAMSECTION).is(programStage))
                .queryList();
        List<ProgramIndicator> indicators = new ArrayList<>();
        if (relations != null && !relations.isEmpty()) {
            for (ProgramIndicatorToSectionRelationship relation : relations) {
                indicators.add(relation.getProgramIndicator());
            }
        }
        return indicators;
    }

    public static List<ProgramIndicator> getProgramIndicatorsBySection(String section) {
        List<ProgramIndicatorToSectionRelationship> relations = new Select()
                .from(ProgramIndicatorToSectionRelationship.class)
                .where(Condition.column(ProgramIndicatorToSectionRelationship$Table
                        .PROGRAMSECTION).is(section))
                .queryList();
        List<ProgramIndicator> indicators = new ArrayList<>();
        if (relations != null && !relations.isEmpty()) {
            for (ProgramIndicatorToSectionRelationship relation : relations) {
                indicators.add(relation.getProgramIndicator());
            }
        }
        return indicators;
    }

    /**
     * Clears status and time of loaded meta data items
     */
    public static void clearMetaDataLoadedFlags() {
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.ASSIGNEDPROGRAMS);
        List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
        for (String program : assignedPrograms) {
            DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAM, program);
        }
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.OPTIONSETS);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTEGROUPS);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.CONSTANTS);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAMRULES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAMRULEVARIABLES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAMRULEACTIONS);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.RELATIONSHIPTYPES);
    }

    /**
     * Deletes all meta data from local database
     */
    public static void wipe() {
        Delete.tables(
                Attribute.class,
                AttributeValue.class,
                Conflict.class,
                Constant.class,
                Dashboard.class,
                DashboardElement.class,
                DashboardItem.class,
                DashboardItemContent.class,
                DataElement.class,
                DataValue.class,
                Enrollment.class,
                Event.class,
                FailedItem.class,
                ImportCount.class,
                ImportSummary.class,
                Interpretation.class,
                InterpretationComment.class,
                InterpretationElement.class,
                Option.class,
                OptionSet.class,
                OrganisationUnit.class,
                OrganisationUnitProgramRelationship.class,
                Program.class,
                ProgramIndicator.class,
                ProgramIndicatorToSectionRelationship.class,
                ProgramRule.class,
                ProgramRuleAction.class,
                ProgramRuleVariable.class,
                ProgramStage.class,
                ProgramStageDataElement.class,
                ProgramStageSection.class,
                ProgramTrackedEntityAttribute.class,
                Relationship.class,
                RelationshipType.class,
                SystemInfo.class,
                TrackedEntityAttributeGeneratedValue.class,
                TrackedEntityAttributeValue.class,
                TrackedEntityAttribute.class,
                TrackedEntityInstance.class,
                TrackedEntity.class,
                User.class,
                UserAccount.class);
    }

    /**
     * Loads metaData from the server and stores it in local persistence.
     */
    public static void loadMetaData(Context context, DhisApi dhisApi, SyncStrategy syncStrategy) throws APIException {
        Log.d(CLASS_TAG, "loadMetaData");
        UiUtils.postProgressMessage(context.getString(R.string.loading_metadata),
                LoadingMessageEvent.EventType.METADATA);
        updateMetaDataItems(context, dhisApi, syncStrategy);
    }

    private static void updateTrackedDataItems(Context context, DhisApi dhisApi, DateTime serverDateTime) {
        if (dhisApi == null) {
            dhisApi = DhisController.getInstance().getDhisApi();
            if (dhisApi == null) {
                return;
            }

        }


    }

    /**
     * Loads a metadata item that is scheduled to be loaded but has not yet been.
     */
    private static void updateMetaDataItems(Context context, DhisApi dhisApi, SyncStrategy syncStrategy) throws APIException {
        if (dhisApi == null) {
            dhisApi = DhisController.getInstance().getDhisApi();
            if (dhisApi == null) {
                return;
            }

        }
        SystemInfo serverSystemInfo = dhisApi.getSystemInfo();
        DateTime serverDateTime = serverSystemInfo.getServerDate();
        //some items depend on each other. Programs depend on AssignedPrograms because we need
        //the ids of programs to load.
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.ASSIGNEDPROGRAMS)) {
            if (shouldLoad(serverDateTime, ResourceType.ASSIGNEDPROGRAMS)) {
                getAssignedProgramsDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMS)) {
            List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
            if (assignedPrograms != null) {
                for (String program : assignedPrograms) {
                    if (shouldLoad(serverDateTime, ResourceType.PROGRAMS, program)) {
                        getProgramDataFromServer(dhisApi, program, serverDateTime, syncStrategy);
                    }
                }
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.OPTIONSETS)) {
            if (shouldLoad(serverDateTime, ResourceType.OPTIONSETS)) {
                getOptionSetDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.TRACKEDENTITYATTRIBUTES)) {
            if (shouldLoad(serverDateTime, ResourceType.TRACKEDENTITYATTRIBUTES)) {
                getTrackedEntityAttributeDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.TRACKEDENTITYATTRIBUTEGROUPS)) {
            if (shouldLoad(serverDateTime, ResourceType.TRACKEDENTITYATTRIBUTEGROUPS)) {
                getTrackedEntityAttributeGroupDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.CONSTANTS)) {
            if (shouldLoad(serverDateTime, ResourceType.CONSTANTS)) {
                getConstantsDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULES)) {
            if (shouldLoad(serverDateTime, ResourceType.PROGRAMRULES)) {
                getProgramRulesDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEVARIABLES)) {
            if (shouldLoad(serverDateTime, ResourceType.PROGRAMRULEVARIABLES)) {
                getProgramRuleVariablesDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEACTIONS)) {
            if (shouldLoad(serverDateTime, ResourceType.PROGRAMRULEACTIONS)) {
                getProgramRuleActionsDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.RELATIONSHIPTYPES)) {
            if (shouldLoad(serverDateTime, ResourceType.RELATIONSHIPTYPES)) {
                getRelationshipTypesDataFromServer(dhisApi, serverDateTime);
            }
        }
        List<TrackedEntityAttribute> trackedEntityAttributes = getTrackedEntityAttributes();
        if (trackedEntityAttributes != null && !trackedEntityAttributes.isEmpty()) {
            getTrackedEntityAttributeGeneratedValuesFromServer(dhisApi, getTrackedEntityAttributes(), serverDateTime);
        }
    }

    private static void getAssignedProgramsDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getAssignedProgramsDataFromServer");
        UserAccount userAccount = dhisApi.getUserAccount();
        Map<String, Program> programMap = new HashMap<>();
        List<Program> assignedProgramUids = userAccount.getPrograms();

        for (Program program : assignedProgramUids) {
            programMap.put(program.getUid(), program);
        }

        List<OrganisationUnit> organisationUnitList = userAccount.getOrganisationUnits();
        for (OrganisationUnit organisationUnit : organisationUnitList) {
            organisationUnit.setType(OrganisationUnit.TYPE.ASSIGNED);
        }

        Set<String> teiSearchOrganisationUnitUids = null;
        if (userAccount.getTeiSearchOrganisationUnits() != null) {
            if (!userAccount.getTeiSearchOrganisationUnits().isEmpty()) {
                teiSearchOrganisationUnitUids = new HashSet<>();
                List<OrganisationUnit> teiSearchOrganisatonUnits = userAccount.getTeiSearchOrganisationUnits();
                for (OrganisationUnit organisationUnit : teiSearchOrganisatonUnits) {
                    teiSearchOrganisationUnitUids.add(organisationUnit.getId());
                }
            }
        }
        Map<String, List<OrganisationUnit>> teiSearchOrganisationUnitMap = null;
        List<OrganisationUnit> teiSearchOrganisationUnits = null;
        if (teiSearchOrganisationUnitUids != null) {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("fields", "[id,displayName,programs]");
            String filter = "id:in:[";

            for (String orgUnitUid : teiSearchOrganisationUnitUids) {
                if (!queryMap.containsKey("filter")) {
                    queryMap.put("filter", filter + orgUnitUid);
                } else {
                    String currentFilter = queryMap.get("filter");
                    queryMap.put("filter", currentFilter + "," + orgUnitUid);
                }
            }
            String currentFilter = queryMap.get("filter");
            queryMap.put("filter", currentFilter + "]");
            teiSearchOrganisationUnitMap = dhisApi.getOrganisationUnits(queryMap);
            teiSearchOrganisationUnits = teiSearchOrganisationUnitMap.get("organisationUnits");
        }
        if (teiSearchOrganisationUnits != null) {
            for (OrganisationUnit searchOrgUnit : teiSearchOrganisationUnits) {
                boolean isOrgUnitAssigned = false;
                for (OrganisationUnit assignedOrganisationUnit : organisationUnitList) {
                    if (searchOrgUnit.getId().equals(assignedOrganisationUnit)) {
                        isOrgUnitAssigned = true;
                        break;
                    }
                }
                if (!isOrgUnitAssigned) {
                    searchOrgUnit.setType(OrganisationUnit.TYPE.SEARCH);
                }

            }
            organisationUnitList.addAll(teiSearchOrganisationUnits);
        }

        for (OrganisationUnit organisationUnit : organisationUnitList) {

            if (organisationUnit.getPrograms() != null && !organisationUnit.getPrograms().isEmpty()) {
                List<Program> assignedProgramToUnit = new ArrayList<>();
                for (Program program : organisationUnit.getPrograms()) {
                    if (programMap.containsKey(program.getUid())) {
                        assignedProgramToUnit.add(programMap.get(program.getUid()));
                    }
                }
                organisationUnit.setPrograms(assignedProgramToUnit);
            }
        }


        List<DbOperation> operations = AssignedProgramsWrapper.getOperations(organisationUnitList);

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ASSIGNEDPROGRAMS, serverDateTime);
    }

    private static void getProgramDataFromServer(DhisApi dhisApi, String uid, DateTime serverDateTime, SyncStrategy syncStrategy) throws APIException {
        Log.d(CLASS_TAG, "getProgramDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.PROGRAM, uid);

        Program program = updateProgram(dhisApi, uid, lastUpdated, syncStrategy);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.PROGRAM, uid, serverDateTime);

    }

    private static Program updateProgram(DhisApi dhisApi, String uid, DateTime lastUpdated, SyncStrategy syncStrategy) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_FULL.put("fields",
                "*,trackedEntity[*],programIndicators[*],programStages[*,!dataEntryForm,program[id],programIndicators[*]," +
                        "programStageSections[*,programStageDataElements[*,programStage[id]," +
                        "dataElement[*,id,attributeValues[*,attribute[*]],optionSet[id]]],programIndicators[*]],programStageDataElements" +
                        "[*,programStage[id],dataElement[*,optionSet[id]]]],programTrackedEntityAttributes" +
                        "[*,trackedEntityAttribute[*]],!organisationUnits");

        if (syncStrategy == SyncStrategy.DOWNLOAD_ONLY_NEW && lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        // program with content.
        Program updatedProgram = dhisApi.getProgram(uid, QUERY_MAP_FULL);
        List<DbOperation> operations = ProgramWrapper.setReferences(updatedProgram);
        DbUtils.applyBatch(operations);


        return updatedProgram;
    }

    private static void getOptionSetDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getOptionSetDataFromServer");
        Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        QUERY_MAP_FULL.put("fields", "*,options[*]");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.OPTIONSETS);

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<OptionSet> optionSets = unwrapResponse(dhisApi
                .getOptionSets(QUERY_MAP_FULL), ApiEndpointContainer.OPTION_SETS);
        List<DbOperation> operations = OptionSetWrapper.getOperations(optionSets);
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.OPTIONSETS, serverDateTime);
    }

    private static void getTrackedEntityAttributeGroupDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getTrackedEntityAttributeDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTEGROUPS);
        List<TrackedEntityAttributeGroup> trackedEntityAttributeGroups = unwrapResponse(dhisApi
                .getTrackedEntityAttributeGroups(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.TRACKED_ENTITY_ATTRIBUTE_GROUPS);

        saveResourceDataFromServer(ResourceType.TRACKEDENTITYATTRIBUTEGROUPS, dhisApi, trackedEntityAttributeGroups, getTrackedEntityAttributeGroups(), serverDateTime);
    }

    private static void getTrackedEntityAttributeDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getTrackedEntityAttributeDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTES);
        List<TrackedEntityAttribute> trackedEntityAttributes = unwrapResponse(dhisApi
                .getTrackedEntityAttributes(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.TRACKED_ENTITY_ATTRIBUTES);


        saveResourceDataFromServer(ResourceType.TRACKEDENTITYATTRIBUTES, dhisApi, trackedEntityAttributes, getTrackedEntityAttributes(), serverDateTime);
    }

    /**
     * @param dhisApi
     * @param trackedEntityAttributes
     * @param serverDateTime          This method tries to get trackedEntityAttributeGeneratedValues from server if we need it
     */
    public static void getTrackedEntityAttributeGeneratedValuesFromServer(DhisApi dhisApi, List<TrackedEntityAttribute> trackedEntityAttributes, DateTime serverDateTime) {
        // After fetching trackedEntityAttributes from server, we want to go through all of them and fetch IDs for generation
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTEGENERATEDVALUES);

        for (TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributes) {
            if (trackedEntityAttribute.isGenerated()) {
                long numberOfGeneratedTrackedEntityAttributesToFetch = shouldFetchGeneratedTrackedEntityAttributeValues(trackedEntityAttribute, serverDateTime);
                if (numberOfGeneratedTrackedEntityAttributesToFetch > 0) {
                    List<TrackedEntityAttributeGeneratedValue> trackedEntityAttributeGeneratedValues =
                            dhisApi.getTrackedEntityAttributeGeneratedValues(trackedEntityAttribute.getUid(), numberOfGeneratedTrackedEntityAttributesToFetch); // Downloading x generated IDs per trackedEntityAttribute

                    saveBaseValueDataFromServer(ResourceType.TRACKEDENTITYATTRIBUTEGENERATEDVALUES, "", trackedEntityAttributeGeneratedValues, getTrackedEntityAttributeGeneratedValues(), serverDateTime, false);
                }
            }
        }
    }

    /**
     * @param trackedEntityAttribute
     * @return number of trackedEntityAttributeGeneratedValues to fetch
     */
    private static long shouldFetchGeneratedTrackedEntityAttributeValues(TrackedEntityAttribute trackedEntityAttribute, DateTime serverDateTime) {

        checkIfGeneratedTrackedEntityAttributeValuesHasExpired(serverDateTime);

        long numberOfTrackedEntityAttributeGeneratedValues = new Select().
                from(TrackedEntityAttributeGeneratedValue.class)
                .where(Condition.column(TrackedEntityAttributeGeneratedValue$Table.TRACKEDENTITYATTRIBUTE_TRACKEDENTITYATTRIBUTE)
                        .eq(trackedEntityAttribute.getUid()))
                .queryList().size();

        if (numberOfTrackedEntityAttributeGeneratedValues < TRACKED_ENTITY_ATTRITBUTE_GENERATED_VALUE_THRESHOLD) {

            return (TRACKED_ENTITY_ATTRITBUTE_GENERATED_VALUE_THRESHOLD - numberOfTrackedEntityAttributeGeneratedValues);
        }

        return 0;
    }

    public static void checkIfGeneratedTrackedEntityAttributeValuesHasExpired(DateTime serverDateTime) {
        List<TrackedEntityAttributeGeneratedValue> generatedValuesThatIsExpired = new Select()
                .from(TrackedEntityAttributeGeneratedValue.class)
                .where(Condition.column(TrackedEntityAttributeGeneratedValue$Table.EXPIRYDATE)
                        .lessThan(serverDateTime)).queryList();

        for (TrackedEntityAttributeGeneratedValue trackedEntityAttributeGeneratedValue : generatedValuesThatIsExpired) {

            trackedEntityAttributeGeneratedValue.delete();
        }

    }

    private static void getConstantsDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getConstantsDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.CONSTANTS);
        List<Constant> constants = unwrapResponse(dhisApi
                .getConstants(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.CONSTANTS);
        saveResourceDataFromServer(ResourceType.CONSTANTS, dhisApi, constants, getConstants(), serverDateTime);
    }

    private static void getProgramRulesDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getProgramRulesDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.PROGRAMRULES);
        List<ProgramRule> programRules = unwrapResponse(dhisApi
                .getProgramRules(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.PROGRAMRULES);
        saveResourceDataFromServer(ResourceType.PROGRAMRULES, dhisApi, programRules, getProgramRules(), serverDateTime);
    }

    private static void getProgramRuleVariablesDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getProgramRuleVariablesDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.PROGRAMRULEVARIABLES);
        List<ProgramRuleVariable> programRuleVariables = unwrapResponse(dhisApi
                .getProgramRuleVariables(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.PROGRAMRULEVARIABLES);
        saveResourceDataFromServer(ResourceType.PROGRAMRULEVARIABLES, dhisApi, programRuleVariables, getProgramRuleVariables(), serverDateTime);
    }

    private static void getProgramRuleActionsDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getProgramRuleActionsDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.PROGRAMRULEACTIONS);
        List<ProgramRuleAction> programRuleActions = unwrapResponse(dhisApi
                .getProgramRuleActions(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.PROGRAMRULEACTIONS);
        saveResourceDataFromServer(ResourceType.PROGRAMRULEACTIONS, dhisApi, programRuleActions, getProgramRuleActions(), serverDateTime);
    }

    private static void getRelationshipTypesDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getRelationshipTypesDataFromServer");
        ResourceType resource = ResourceType.RELATIONSHIPTYPES;
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource);
        List<RelationshipType> relationshipTypes = unwrapResponse(dhisApi
                .getRelationshipTypes(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.RELATIONSHIPTYPES);
        saveResourceDataFromServer(resource, dhisApi, relationshipTypes, getRelationshipTypes(), serverDateTime);
    }

    public static TrackedEntityAttributeGeneratedValue getTrackedEntityAttributeGeneratedValue(TrackedEntityAttribute trackedEntityAttribute) {
        List<TrackedEntityAttributeGeneratedValue> trackedEntityAttributeGeneratedValues = new Select().from(TrackedEntityAttributeGeneratedValue.class)
                .where(Condition.column(TrackedEntityAttributeGeneratedValue$Table.TRACKEDENTITYATTRIBUTE_TRACKEDENTITYATTRIBUTE).eq(trackedEntityAttribute.getUid())).queryList();

        if (trackedEntityAttributeGeneratedValues != null && !trackedEntityAttributeGeneratedValues.isEmpty()) {
            TrackedEntityAttributeGeneratedValue trackedEntityAttributeGeneratedValue = trackedEntityAttributeGeneratedValues.get(0);

            //trackedEntityAttributeGeneratedValue.delete(); // Deleting it so it cannot be re-used

            return trackedEntityAttributeGeneratedValue;
        }

        return null;
    }

    public static TrackedEntityAttributeGeneratedValue getTrackedEntityAttributeGeneratedValue(String trackedEntityAttributeGeneratedValue) {
        List<TrackedEntityAttributeGeneratedValue> trackedEntityAttributeGeneratedValues = new Select().from(TrackedEntityAttributeGeneratedValue.class)
                .where(Condition.column(TrackedEntityAttributeGeneratedValue$Table.VALUE).eq(trackedEntityAttributeGeneratedValue)).queryList();

        if (trackedEntityAttributeGeneratedValues != null && !trackedEntityAttributeGeneratedValues.isEmpty()) {
            TrackedEntityAttributeGeneratedValue generatedValue = trackedEntityAttributeGeneratedValues.get(0);

            return generatedValue;
        }

        return null;
    }

    public static boolean performSearchBeforeEnrollment() {
        return getSearchAttributeGroup() != null;
    }

    public static TrackedEntityAttributeGroup getSearchAttributeGroup() {
        List<TrackedEntityAttributeGroup> attributeGroups = getTrackedEntityAttributeGroups();
        for (TrackedEntityAttributeGroup attributeGroup : attributeGroups) {
            // TODO: put in proper logic here when backend solution is in place
            // either use a AttributeGroup.TYPE enum or put a configuration flag somewhere
            if (attributeGroup.getDescription().equals("SEARCH_GROUP")) {
                return attributeGroup;
            }
        }
        return null;
    }

    public static Hashtable<String, List<Program>> getAssignedProgramsByOrganisationUnit() {
        List<OrganisationUnit> assignedOrganisationUnits = getAssignedOrganisationUnits();
        Hashtable<String, List<Program>> programsForOrganisationUnits = new Hashtable<>();

        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if (organisationUnit.getId() == null
                    || organisationUnit.getId().length() == Utils.randomUUID.length()) {
                continue;
            }

            List<Program> programsForOrgUnit = new ArrayList<>();
            List<Program> programsForOrgUnitSEWoR = getProgramsForOrganisationUnit
                    (organisationUnit.getId(), ProgramType.WITHOUT_REGISTRATION, ProgramType.WITH_REGISTRATION);

            if (programsForOrgUnitSEWoR != null) {
                programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
                if (programsForOrgUnitSEWoR.size() > 0) {
                    programsForOrganisationUnits.put(organisationUnit.getId(),
                            programsForOrgUnit);
                }
            }
        }

        return programsForOrganisationUnits;
    }
}
