/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.api.persistence.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

@Table(database = DbDhis.class)
public final class ProgramFlow extends BaseIdentifiableObjectFlow {
    private static final String TRACKED_ENTITY_KEY = "trackedEntity";
    public static Mapper<Program, ProgramFlow> MAPPER = new ProgramMapper();
    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = TRACKED_ENTITY_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = true, onDelete = ForeignKeyAction.NO_ACTION
    )
    TrackedEntityFlow trackedEntity;

    @Column
    ProgramType programType;

    @Column
    int version;

    @Column
    String enrollmentDateLabel;

    @Column
    String description;

    @Column
    boolean onlyEnrollOnce;

    @Column
    boolean externalAccess;

    @Column
    boolean displayIncidentDate;

    @Column
    String incidentDateLabel;

    @Column
    boolean registration;

    @Column
    boolean selectEnrollmentDatesInFuture;

    @Column
    boolean dataEntryMethod;

    @Column
    boolean singleEvent;

    @Column
    boolean ignoreOverdueEvents;

    @Column
    boolean relationshipFromA;

    @Column
    boolean selectIncidentDatesInFuture;

    @Column
    boolean isAssignedToUser;

    public ProgramFlow() {
        // empty constructor
    }

    public TrackedEntityFlow getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntityFlow trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEnrollmentDateLabel() {
        return enrollmentDateLabel;
    }

    public void setEnrollmentDateLabel(String enrollmentDateLabel) {
        this.enrollmentDateLabel = enrollmentDateLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOnlyEnrollOnce() {
        return onlyEnrollOnce;
    }

    public void setOnlyEnrollOnce(boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public boolean isDisplayIncidentDate() {
        return displayIncidentDate;
    }

    public void setDisplayIncidentDate(boolean displayIncidentDate) {
        this.displayIncidentDate = displayIncidentDate;
    }

    public String getIncidentDateLabel() {
        return incidentDateLabel;
    }

    public void setIncidentDateLabel(String incidentDateLabel) {
        this.incidentDateLabel = incidentDateLabel;
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public boolean isSelectEnrollmentDatesInFuture() {
        return selectEnrollmentDatesInFuture;
    }

    public void setSelectEnrollmentDatesInFuture(boolean selectEnrollmentDatesInFuture) {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    public boolean isDataEntryMethod() {
        return dataEntryMethod;
    }

    public void setDataEntryMethod(boolean dataEntryMethod) {
        this.dataEntryMethod = dataEntryMethod;
    }

    public boolean isSingleEvent() {
        return singleEvent;
    }

    public void setSingleEvent(boolean singleEvent) {
        this.singleEvent = singleEvent;
    }

    public boolean isIgnoreOverdueEvents() {
        return ignoreOverdueEvents;
    }

    public void setIgnoreOverdueEvents(boolean ignoreOverdueEvents) {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    public boolean isRelationshipFromA() {
        return relationshipFromA;
    }

    public void setRelationshipFromA(boolean relationshipFromA) {
        this.relationshipFromA = relationshipFromA;
    }

    public boolean isSelectIncidentDatesInFuture() {
        return selectIncidentDatesInFuture;
    }

    public void setSelectIncidentDatesInFuture(boolean selectIncidentDatesInFuture) {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    public boolean isAssignedToUser() {
        return isAssignedToUser;
    }

    public void setIsAssignedToUser(boolean isAssignedToUser) {
        this.isAssignedToUser = isAssignedToUser;
    }

    private static class ProgramMapper extends AbsMapper<Program, ProgramFlow> {

        @Override
        public ProgramFlow mapToDatabaseEntity(Program program) {
            if (program == null) {
                return null;
            }

            ProgramFlow programFlow = new ProgramFlow();
            programFlow.setId(program.getId());
            programFlow.setUId(program.getUId());
            programFlow.setCreated(program.getCreated());
            programFlow.setLastUpdated(program.getLastUpdated());
            programFlow.setName(program.getName());
            programFlow.setDisplayName(program.getDisplayName());
            programFlow.setAccess(program.getAccess());
            programFlow.setTrackedEntity(MapperModuleProvider.getInstance()
                    .getTrackedEntityMapper().mapToDatabaseEntity(program.getTrackedEntity()));
            programFlow.setProgramType(program.getProgramType());
            programFlow.setVersion(program.getVersion());
            programFlow.setEnrollmentDateLabel(program.getEnrollmentDateLabel());
            programFlow.setDescription(program.getDescription());
            programFlow.setOnlyEnrollOnce(program.isOnlyEnrollOnce());
            programFlow.setExternalAccess(program.isExternalAccess());
            programFlow.setDisplayIncidentDate(program.isDisplayIncidentDate());
            programFlow.setIncidentDateLabel(program.getIncidentDateLabel());
            programFlow.setRegistration(program.isRegistration());
            programFlow.setSelectEnrollmentDatesInFuture(program.isSelectEnrollmentDatesInFuture());
            programFlow.setDataEntryMethod(program.isDataEntryMethod());
            programFlow.setSingleEvent(program.isSingleEvent());
            programFlow.setIgnoreOverdueEvents(program.isIgnoreOverdueEvents());
            programFlow.setRelationshipFromA(program.isRelationshipFromA());
            programFlow.setSelectIncidentDatesInFuture(program.isSelectIncidentDatesInFuture());
            programFlow.setIsAssignedToUser(program.isAssignedToUser());
            return programFlow;
        }

        @Override
        public Program mapToModel(ProgramFlow programFlow) {
            if (programFlow == null) {
                return null;
            }

            Program program = new Program();
            program.setId(programFlow.getId());
            program.setUId(programFlow.getUId());
            program.setCreated(programFlow.getCreated());
            program.setLastUpdated(programFlow.getLastUpdated());
            program.setName(programFlow.getName());
            program.setDisplayName(programFlow.getDisplayName());
            program.setAccess(programFlow.getAccess());
            program.setTrackedEntity(MapperModuleProvider.getInstance()
                    .getTrackedEntityMapper().mapToModel(programFlow.getTrackedEntity()));
            program.setProgramType(programFlow.getProgramType());
            program.setVersion(programFlow.getVersion());
            program.setEnrollmentDateLabel(programFlow.getEnrollmentDateLabel());
            program.setDescription(programFlow.getDescription());
            program.setOnlyEnrollOnce(programFlow.isOnlyEnrollOnce());
            program.setExternalAccess(programFlow.isExternalAccess());
            program.setDisplayIncidentDate(programFlow.isDisplayIncidentDate());
            program.setIncidentDateLabel(programFlow.getIncidentDateLabel());
            program.setRegistration(programFlow.isRegistration());
            program.setSelectEnrollmentDatesInFuture(programFlow.isSelectEnrollmentDatesInFuture());
            program.setDataEntryMethod(programFlow.isDataEntryMethod());
            program.setSingleEvent(programFlow.isSingleEvent());
            program.setIgnoreOverdueEvents(programFlow.isIgnoreOverdueEvents());
            program.setRelationshipFromA(programFlow.isRelationshipFromA());
            program.setSelectIncidentDatesInFuture(programFlow.isSelectIncidentDatesInFuture());
            program.setIsAssignedToUser(programFlow.isAssignedToUser());
            return program;
        }

        @Override
        public Class<Program> getModelTypeClass() {
            return Program.class;
        }

        @Override
        public Class<ProgramFlow> getDatabaseEntityTypeClass() {
            return ProgramFlow.class;
        }
    }
}
