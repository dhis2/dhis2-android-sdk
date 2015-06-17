/*
 *  Copyright (c) 2015, University of Oslo
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

import android.content.Context;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.Constant$Table;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataElement$Table;
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
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public class MetaDataController {
    private final static String CLASS_TAG = "MetaDataController";


    private MetaDataLoader metaDataLoader;

    public MetaDataController() {
        Dhis2Application.bus.register(this);
        metaDataLoader = new MetaDataLoader();
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
        if(section==null) return null;
        return new Select().from(ProgramStageDataElement.class).where(Condition.column
                (ProgramStageDataElement$Table.PROGRAMSTAGESECTION).is(section.id)).orderBy
                (ProgramStageDataElement$Table.SORTORDER).queryList();
    }

    public static List<ProgramStageDataElement> getProgramStageDataElements(ProgramStage programStage) {
        if(programStage==null) return null;
        return new Select().from(ProgramStageDataElement.class).where(Condition.column
                (ProgramStageDataElement$Table.PROGRAMSTAGE).is(programStage.id)).orderBy
                (ProgramStageDataElement$Table.SORTORDER).queryList();
    }

    /**
     * returns a tracked Entity object for the given ID
     * @param trackedEntity
     * @return
     */
    public static TrackedEntity getTrackedEntity(String trackedEntity) {
        return new Select().from(TrackedEntity.class).where(Condition.column
                (TrackedEntity$Table.ID).is(trackedEntity)).querySingle();
    }

    /**
     * Returns a list of ProgramTrackedEntityAttributes for the given program.
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
     * @param organisationUnitId
     * @param kinds set to null to get all programs. Else get kinds Strings from Program.
     * @return
     */
    public static List<Program> getProgramsForOrganisationUnit(String organisationUnitId,
                                                               String... kinds) {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships =
                new Select().from(OrganisationUnitProgramRelationship.class).where(
                        Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).
                                is(organisationUnitId)).queryList();

        List<Program> programs = new ArrayList<Program>();
        for(OrganisationUnitProgramRelationship oupr: organisationUnitProgramRelationships ) {
            if(kinds!=null) {
                for(String kind: kinds)
                {
                    List<Program> plist = new Select().from(Program.class).where(
                            Condition.column(Program$Table.ID).is(oupr.getProgramId())).and(
                            Condition.column(Program$Table.KIND).is(kind)).queryList();
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
     * @param programStageUid
     * @return
     */
    public static ProgramStage getProgramStage(String programStageUid) {
        return new Select().from(ProgramStage.class).where(
                Condition.column(ProgramStage$Table.ID).is(programStageUid)).querySingle();
    }

    /**
     * todo: programTrackedEntityAttributes is not necessarily unique for a trackedEntityAttribute.
     * todo: implement program parameter
     * @param trackedEntityAttribute
     * @return
     */
    public static ProgramTrackedEntityAttribute getProgramTrackedEntityAttribute(String trackedEntityAttribute) {
        return new Select().from(ProgramTrackedEntityAttribute.class).where
                (Condition.column(ProgramTrackedEntityAttribute$Table.TRACKEDENTITYATTRIBUTE).is
                        (trackedEntityAttribute)).querySingle();
    }

    public static TrackedEntityAttribute getTrackedEntityAttribute(String trackedEntityAttributeId) {
        return new Select().from(TrackedEntityAttribute.class).where(Condition.column
                (TrackedEntityAttribute$Table.ID).is(trackedEntityAttributeId)).querySingle();
    }

    /**
     * Returns a constant with the given uid
     * @param id
     * @return
     */
    public static Constant getConstant(String id) {
        return new Select().from(Constant.class).where
                (Condition.column(Constant$Table.ID).is(id)).querySingle();
    }

    /**
     * returns a list of all constants
     * @return
     */
    public static List<Constant> getConstants() {
        return new Select().from(Constant.class).queryList();
    }

    public static ProgramRuleVariable getProgramRuleVariable(String id) {
        return new Select().from(ProgramRuleVariable.class).where(Condition.column(ProgramRuleVariable$Table.ID).is(id)).querySingle();
    }

    public static ProgramRuleVariable getProgramRuleVariableByName(String name) {
        return new Select().from(ProgramRuleVariable.class).where(Condition.column(ProgramRuleVariable$Table.NAME).is(name)).querySingle();
    }

    /**
     * Returns a list of IDs for all assigned programs.
     * @return
     */
    public static List<String> getAssignedPrograms() {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships = new Select().from(OrganisationUnitProgramRelationship.class).queryList();
        List<String> assignedPrograms = new ArrayList<>();
        for(OrganisationUnitProgramRelationship relationship: organisationUnitProgramRelationships) {
            if(!assignedPrograms.contains(relationship.getProgramId())) assignedPrograms.add(relationship.getProgramId());
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
        return new Select().from(Program.class).where(Condition.column(Program$Table.ID).
                is(programId)).querySingle();
    }

    /**
     * Returns a list of organisation units assigned to the current user
     * @return
     */
    public static List<OrganisationUnit> getAssignedOrganisationUnits() {
        List<OrganisationUnit> organisationUnits = new Select().from(OrganisationUnit.class).queryList();
        return organisationUnits;
    }

    /**
     * Returns the data element for the given uid or null if the dataElement does not exist
     * @param dataElementId
     * @return
     */
    public static DataElement getDataElement(String dataElementId) {
        return new Select().from(DataElement.class).where(Condition.column(DataElement$Table.ID).
                is(dataElementId)).querySingle();
    }

    /**
     * Returns a User object for the currently logged in user.
     * @return
     */
    public static User getUser() {
        return new Select().from(User.class).querySingle();
    }

    /**
     * Returns an option set for the given Id or null of the option set doesn't exist.
     * @param optionSetId
     * @return
     */
    public static OptionSet getOptionSet(String optionSetId) {
        return new Select().from(OptionSet.class).where(Condition.column(OptionSet$Table.ID).
                is(optionSetId)).querySingle();
    }

    public static List<ProgramIndicator> getProgramIndicatorsByProgram(String program) {
        return new Select().from(ProgramIndicator.class).where(Condition.column(ProgramIndicator$Table.PROGRAM).is(program)).queryList();
    }

    public static List<ProgramIndicator> getProgramIndicatorsByProgramStage(String programStage) {
        return new Select().from(ProgramIndicator.class).where(Condition.column(ProgramIndicator$Table.PROGRAMSTAGE).is(programStage)).queryList();
    }

    public static List<ProgramIndicator> getProgramIndicatorsBySection(String section) {
        return new Select().from(ProgramIndicator.class).where(
                Condition.column(ProgramIndicator$Table.SECTION).is(section)).queryList();
    }

    /**
     * Synchronizes meta data by downloading newly changed data from the server.
     * @param context
     * @param callback
     */
    public void synchronizeMetaData(Context context, ApiRequestCallback callback) {
        metaDataLoader.synchronizeMetaData(context, callback);
    }

    /**
     * Initiates loading of metadata from the server. To update existing data, rather use
     * synchronizeMetaData to save data.
     * @param context
     */
    public void loadMetaData(Context context, ApiRequestCallback callback) {
        metaDataLoader.loadMetaData(context, callback);
    }

    /**
     * This method checks if all models that all UID references to other models are valid
     */
    public void metaDataIntegrityCheck()
    {
        metaDataLoader.metaDataIntegrityCheck();
    }

    /**
     * Resets the value set for last updated
     * @param context
     */
    public void resetLastUpdated(Context context) {
        metaDataLoader.resetLastUpdated(context);
    }

    /**
     * Resets the loaded status of all meta data. This status is used to know whether or not
     * to update the data, or to load all of it.
     * @param context
     */
    public void clearMetaDataLoadedFlags(Context context) {
        metaDataLoader.clearMetaDataLoadedFlags(context);
    }

    /**
     * Deletes all meta data from local database
     */
    public void wipeMetaData() {
        Delete.tables(Constant.class,
                DataElement.class,
                Option.class,
                OptionSet.class,
                OrganisationUnit.class,
                OrganisationUnitProgramRelationship.class,
                Program.class,
                ProgramIndicator.class,
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
                ProgramRuleAction.class);
    }

    public boolean isLoading() {
        return metaDataLoader.loading;
    }

    public boolean isSynchronizing() {
        return metaDataLoader.synchronizing;
    }
}
