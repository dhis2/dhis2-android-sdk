/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.controllers.metadata;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.ApiEndpointContainer;
import org.hisp.dhis.android.sdk.controllers.LoadingController;
import org.hisp.dhis.android.sdk.controllers.ResourceController;
import org.hisp.dhis.android.sdk.controllers.wrappers.AssignedProgramsWrapper;
import org.hisp.dhis.android.sdk.controllers.wrappers.AttributeValuesWrapper;
import org.hisp.dhis.android.sdk.controllers.wrappers.OptionSetWrapper;
import org.hisp.dhis.android.sdk.controllers.wrappers.ProgramWrapper;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.Attribute;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.Constant$Table;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataElement$Table;
import org.hisp.dhis.android.sdk.persistence.models.DataElementAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.DataElementAttributeValue$Table;
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
import org.hisp.dhis.android.sdk.persistence.models.ProgramAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.ProgramAttributeValue$Table;
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
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType;
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType$Table;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.User;
import org.hisp.dhis.android.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.DbUtils;
import org.hisp.dhis.android.sdk.utils.UiUtils;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

import static org.hisp.dhis.android.sdk.utils.NetworkUtils.unwrapResponse;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public final class MetaDataController extends ResourceController {
    private final static String CLASS_TAG = "MetaDataController";

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
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.ASSIGNEDPROGRAMS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.OPTIONSETS)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.OPTIONSETS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.TRACKEDENTITYATTRIBUTES)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.CONSTANTS)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.CONSTANTS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULES)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.PROGRAMRULES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEVARIABLES)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.PROGRAMRULEVARIABLES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEACTIONS)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.PROGRAMRULEACTIONS) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.ATTRIBUTES)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.ATTRIBUTES) == null) {
                return false;
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.RELATIONSHIPTYPES)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.RELATIONSHIPTYPES) == null) {
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

    public static List<Attribute> getAttributes() {
        return new Select().from(Attribute.class).queryList();
    }

    public static List<AttributeValue> getAttributeValues() {
        return new Select().from(AttributeValue.class).queryList();
    }

    public static List<AttributeValue> getAttributeValues(DataElement dataElement){
        if (dataElement == null) return null;
        List<AttributeValue> values = new ArrayList<>();
        List<AttributeValue> attributeValues;
        List<DataElementAttributeValue> dataElementAttributeValues = getDataElementAttributeValues(dataElement.getUid());

        for (DataElementAttributeValue dataElementAttributeValue: dataElementAttributeValues){
            attributeValues = new Select().from(AttributeValue.class).where(Condition.column(AttributeValue$Table.ID).is(dataElementAttributeValue.getAttributeValue().getId())).queryList();
            if (attributeValues != null) values.addAll(attributeValues);
        }

        return values;
    }

    public static List<DataElementAttributeValue> getDataElementAttributeValues(String dataElementId){
        if (dataElementId == null) return null;
        return new Select().from(DataElementAttributeValue.class)
                .where(Condition.column(DataElementAttributeValue$Table.DATAELEMENTID).is(dataElementId)).queryList();
    }

    public static List<AttributeValue> getAttributeValues(Program program){
        if (program == null) return null;
        List<AttributeValue> values = new ArrayList<>();
        List<AttributeValue> attributeValues;
        List<ProgramAttributeValue> programAttributeValues = getProgramAttributeValues(program.getUid());

        for (ProgramAttributeValue programAttributeValue: programAttributeValues){
            attributeValues = new Select().from(AttributeValue.class).where(Condition.column(AttributeValue$Table.ID).is(programAttributeValue.getAttributeValue().getId())).queryList();
            if (attributeValues != null) values.addAll(attributeValues);
        }

        return values;
    }

    public static AttributeValue getAttributeValue(Long id){
        if (id == null) return null;
        List<AttributeValue> attributeValues = new Select().from(AttributeValue.class)
                .where(Condition.column(AttributeValue$Table.ID).is(id)).queryList();
        if (attributeValues.size() != 1) return null;
        return attributeValues.get(0);
    }

    public static List<ProgramAttributeValue> getProgramAttributeValues(String programId){
        if (programId == null) return null;
        return new Select().from(ProgramAttributeValue.class)
                .where(Condition.column(ProgramAttributeValue$Table.PROGRAM_PROGRAMID).is(programId)).queryList();
    }

    public static String getAttributeName(String attributeId){
        if (attributeId == null) return null;
        List<Attribute> attributes = new Select().from(Attribute.class)
                .where(Condition.column(AttributeValue$Table.ID).is(attributeId)).queryList();
        if (attributes.size() != 1) return null;
        return attributes.get(0).getName();
    }

    public static String getAttributeType(String attributeId){
        if (attributeId == null) return null;
        List<Attribute> attributes = new Select().from(Attribute.class)
                .where(Condition.column(AttributeValue$Table.ID).is(attributeId)).queryList();
        if (attributes.size() != 1) return null;
        return attributes.get(0).getValueType();
    }

    public static Object getAttributeValueValue(Long attributeValueId){
        if (attributeValueId == null) return null;
        List<AttributeValue> attributeValues = new Select().from(AttributeValue.class)
                .where(Condition.column(AttributeValue$Table.ID).is(attributeValueId)).queryList();
        if (attributeValues.size() != 1) return null;
        return attributeValues.get(0).getValue();
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
                                                               Program.ProgramType... kinds) {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships =
                new Select().from(OrganisationUnitProgramRelationship.class).where(
                        Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).
                                is(organisationUnitId)).queryList();

        List<Program> programs = new ArrayList<Program>();
        for (OrganisationUnitProgramRelationship oupr : organisationUnitProgramRelationships) {
            if (kinds != null) {
                for (Program.ProgramType kind : kinds) {
                    List<Program> plist = new Select().from(Program.class).where(
                            Condition.column(Program$Table.ID).is(oupr.getProgramId())).and(
                            Condition.column(Program$Table.KIND).is(kind.toString())).queryList();
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
        List<OrganisationUnit> organisationUnits = new Select().from(OrganisationUnit.class).queryList();
        return organisationUnits;
    }

    public static List<OrganisationUnitProgramRelationship> getOrganisationUnitProgramRelationships() {
        return new Select().from(OrganisationUnitProgramRelationship.class).queryList();
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
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.CONSTANTS);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAMRULES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAMRULEVARIABLES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.PROGRAMRULEACTIONS);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.RELATIONSHIPTYPES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.ATTRIBUTES);
        DateTimeManager.getInstance().deleteLastUpdated(ResourceType.ATTRIBUTEVALUES);
    }

    /**
     * Deletes all meta data from local database
     */
    public static void wipe() {
        Delete.tables(Constant.class,
                DataElement.class,
                Option.class,
                OptionSet.class,
                OrganisationUnit.class,
                OrganisationUnitProgramRelationship.class,
                Program.class,
                ProgramIndicator.class,
                ProgramIndicatorToSectionRelationship.class,
                ProgramStage.class,
                ProgramStageDataElement.class,
                ProgramStageSection.class,
                ProgramTrackedEntityAttribute.class,
                SystemInfo.class,
                TrackedEntity.class,
                TrackedEntityAttribute.class,
                TrackedEntityInstance.class,
                User.class,
                ProgramRule.class,
                ProgramRuleVariable.class,
                ProgramRuleAction.class,
                RelationshipType.class,
                Attribute.class,
                AttributeValue.class,
                DataElementAttributeValue.class,
                ProgramAttributeValue.class);
    }

    /**
     * Loads metaData from the server and stores it in local persistence.
     */
    public static void loadMetaData(Context context, DhisApi dhisApi) throws APIException {
        Log.d(CLASS_TAG, "loadMetaData");
        UiUtils.postProgressMessage(context.getString(R.string.loading_metadata));
        updateMetaDataItems(context, dhisApi);
    }

    /**
     * Loads a metadata item that is scheduled to be loaded but has not yet been.
     */
    private static void updateMetaDataItems(Context context, DhisApi dhisApi) throws APIException{
        SystemInfo serverSystemInfo = dhisApi.getSystemInfo();
        DateTime serverDateTime = serverSystemInfo.getServerDate();
        //some items depend on each other. Programs depend on AssignedPrograms because we need
        //the ids of programs to load.
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.ASSIGNEDPROGRAMS)) {
            if ( shouldLoad(dhisApi, ResourceType.ASSIGNEDPROGRAMS) ) {
                getAssignedProgramsDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMS)) {
            List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
            if (assignedPrograms != null) {
                for (String program : assignedPrograms) {
                    if ( shouldLoad(dhisApi, ResourceType.PROGRAMS, program) ) {
                        getProgramDataFromServer(dhisApi, program, serverDateTime);
                    }
                }
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.OPTIONSETS)) {
            if ( shouldLoad(dhisApi, ResourceType.OPTIONSETS) ) {
                getOptionSetDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.TRACKEDENTITYATTRIBUTES)) {
            if ( shouldLoad(dhisApi, ResourceType.TRACKEDENTITYATTRIBUTES) ) {
                getTrackedEntityAttributeDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.CONSTANTS)) {
            if ( shouldLoad(dhisApi, ResourceType.CONSTANTS) ) {
                getConstantsDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULES)) {
            if ( shouldLoad(dhisApi, ResourceType.PROGRAMRULES) ) {
                getProgramRulesDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEVARIABLES)) {
            if ( shouldLoad(dhisApi, ResourceType.PROGRAMRULEVARIABLES) ) {
                getProgramRuleVariablesDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.PROGRAMRULEACTIONS)) {
            if ( shouldLoad(dhisApi, ResourceType.PROGRAMRULEACTIONS) ) {
                getProgramRuleActionsDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.RELATIONSHIPTYPES)) {
            if ( shouldLoad(dhisApi, ResourceType.RELATIONSHIPTYPES) ) {
                getRelationshipTypesDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.ATTRIBUTES)) {
            if ( shouldLoad(dhisApi, ResourceType.ATTRIBUTES) ) {
                getAttributesDataFromServer(dhisApi, serverDateTime);
            }
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.ATTRIBUTEVALUES)) {
            if ( shouldLoad(dhisApi, ResourceType.ATTRIBUTEVALUES) ) {
                getAttributeValuesDataFromServer(dhisApi, serverDateTime);
            }
        }
    }

    private static void getAssignedProgramsDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getAssignedProgramsDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ASSIGNEDPROGRAMS);
        Response response = dhisApi.getAssignedPrograms(getBasicQueryMap(lastUpdated));

        List<OrganisationUnit> organisationUnits;
        try {
            organisationUnits = new AssignedProgramsWrapper().deserialize(response);
        } catch (ConversionException e) {
            e.printStackTrace();
            return; //todo: handle
        } catch (IOException e) {
            e.printStackTrace();
            return; //todo: handle
        }
        List<DbOperation> operations = AssignedProgramsWrapper.getOperations(organisationUnits);

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ASSIGNEDPROGRAMS, serverDateTime);
    }

    private static void getProgramDataFromServer(DhisApi dhisApi, String uid, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getProgramDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.PROGRAM, uid);

        Program program = updateProgram(dhisApi, uid, lastUpdated);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.PROGRAM, uid, serverDateTime);
    }

    private static Program updateProgram(DhisApi dhisApi, String uid, DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_FULL.put("fields",
                "*,programStages[*,!dataEntryForm,program[id],programIndicators[*]," +
                        "programStageSections[*,programStageDataElements[*,programStage[id]," +
                        "dataElement[*,optionSet[id]],attributes],programIndicators[*]],programStageDataElements" +
                        "[*,programStage[id],dataElement[*,optionSet[id]]]],programTrackedEntityAttributes" +
                        "[*,trackedEntityAttribute[*]],!organisationUnits)");

        if (lastUpdated != null) {
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
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.OPTIONSETS);
        List<OptionSet> optionSets = unwrapResponse(dhisApi
                .getOptionSets(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.OPTION_SETS);
        List<DbOperation> operations = OptionSetWrapper.getOperations(optionSets);
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.OPTIONSETS, serverDateTime);
    }

    private static void getTrackedEntityAttributeDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getTrackedEntityAttributeDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.TRACKEDENTITYATTRIBUTES);
        List<TrackedEntityAttribute> trackedEntityAttributes = unwrapResponse(dhisApi
                .getTrackedEntityAttributes(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.TRACKED_ENTITY_ATTRIBUTES);
        saveResourceDataFromServer(ResourceType.TRACKEDENTITYATTRIBUTES, dhisApi, trackedEntityAttributes, getTrackedEntityAttributes(), serverDateTime);
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

    private static void getAttributesDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getAttributesDataFromServer");
        ResourceType resource = ResourceType.ATTRIBUTES;
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource);
        List<Attribute> attributes = unwrapResponse(dhisApi
                .getAttributes(getBasicQueryMap(lastUpdated)), ApiEndpointContainer.ATTRIBUTES);
        saveResourceDataFromServer(resource, dhisApi, attributes, getAttributes(), serverDateTime);
    }

    private static void getAttributeValuesDataFromServer(DhisApi dhisApi, DateTime serverDateTime) throws APIException {
        Log.d(CLASS_TAG, "getAttributeValuesDataFromServer");
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ATTRIBUTEVALUES);
        Response response = dhisApi.getAttributeValues(getBasicQueryMap(lastUpdated));

        List<DataElementAttributeValue> attributeValues;
        try {
            attributeValues = new AttributeValuesWrapper().deserialize(response);
        } catch (ConversionException e) {
            e.printStackTrace();
            return; //todo: handle
        } catch (IOException e) {
            e.printStackTrace();
            return; //todo: handle
        }
        List<DbOperation> operations = AttributeValuesWrapper.getOperations(attributeValues);

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ATTRIBUTEVALUES, serverDateTime);
    }
}
