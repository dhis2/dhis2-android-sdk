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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.program.Program;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Program$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String trackedEntity;

    @Column
    int type;

    @Column
    String kind;

    @Column
    int version;

    @Column
    String dateOfEnrollmentDescription;

    @Column
    String description;

    @Column
    boolean onlyEnrollOnce;

    @Column
    boolean extenalAccess;

    @Column
    boolean displayIncidentDate;

    @Column
    String dateOfIncidentDescription;

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

    private List<ProgramStage$Flow> programStages;

    private List<ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributes;

    public String getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(String trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDateOfEnrollmentDescription() {
        return dateOfEnrollmentDescription;
    }

    public void setDateOfEnrollmentDescription(String dateOfEnrollmentDescription) {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
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

    public boolean isExtenalAccess() {
        return extenalAccess;
    }

    public void setExtenalAccess(boolean extenalAccess) {
        this.extenalAccess = extenalAccess;
    }

    public boolean isDisplayIncidentDate() {
        return displayIncidentDate;
    }

    public void setDisplayIncidentDate(boolean displayIncidentDate) {
        this.displayIncidentDate = displayIncidentDate;
    }

    public String getDateOfIncidentDescription() {
        return dateOfIncidentDescription;
    }

    public void setDateOfIncidentDescription(String dateOfIncidentDescription) {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
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

    public List<ProgramStage$Flow> getProgramStages() {
        return programStages;
    }

    public void setProgramStages(List<ProgramStage$Flow> programStages) {
        this.programStages = programStages;
    }

    public List<ProgramTrackedEntityAttribute$Flow> getProgramTrackedEntityAttributes() {
        return programTrackedEntityAttributes;
    }

    public void setProgramTrackedEntityAttributes(List<ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributes) {
        this.programTrackedEntityAttributes = programTrackedEntityAttributes;
    }

    public Program$Flow() {
        // empty constructor
    }

    public static Program toModel(Program$Flow programFlow) {
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
        program.setTrackedEntity(programFlow.getTrackedEntity());
        program.setType(programFlow.getType());
        program.setKind(programFlow.getKind());
        program.setVersion(programFlow.getVersion());
        program.setDateOfEnrollmentDescription(programFlow.getDateOfEnrollmentDescription());
        program.setDescription(programFlow.getDescription());
        program.setOnlyEnrollOnce(programFlow.isOnlyEnrollOnce());
        program.setExtenalAccess(programFlow.isExtenalAccess());
        program.setDisplayIncidentDate(programFlow.isDisplayIncidentDate());
        program.setDateOfIncidentDescription(programFlow.getDateOfIncidentDescription());
        program.setRegistration(programFlow.isRegistration());
        program.setSelectEnrollmentDatesInFuture(programFlow.isSelectEnrollmentDatesInFuture());
        program.setDataEntryMethod(programFlow.isDataEntryMethod());
        program.setSingleEvent(programFlow.isSingleEvent());
        program.setIgnoreOverdueEvents(programFlow.isIgnoreOverdueEvents());
        program.setRelationshipFromA(programFlow.isRelationshipFromA());
        program.setSelectIncidentDatesInFuture(programFlow.isSelectIncidentDatesInFuture());
        program.setProgramStages(ProgramStage$Flow.toModels(programFlow.getProgramStages()));
        program.setProgramTrackedEntityAttributes(ProgramTrackedEntityAttribute$Flow.toModels(programFlow.getProgramTrackedEntityAttributes()));
        return program;
    }

    public static Program$Flow fromModel(Program program) {
        if (program == null) {
            return null;
        }

        Program$Flow programFlow = new Program$Flow();
        programFlow.setId(program.getId());
        programFlow.setUId(program.getUId());
        programFlow.setCreated(program.getCreated());
        programFlow.setLastUpdated(program.getLastUpdated());
        programFlow.setName(program.getName());
        programFlow.setDisplayName(program.getDisplayName());
        programFlow.setAccess(program.getAccess());
        programFlow.setTrackedEntity(program.getTrackedEntity());
        programFlow.setType(program.getType());
        programFlow.setKind(program.getKind());
        programFlow.setVersion(program.getVersion());
        programFlow.setDateOfEnrollmentDescription(program.getDateOfEnrollmentDescription());
        programFlow.setDescription(program.getDescription());
        programFlow.setOnlyEnrollOnce(program.isOnlyEnrollOnce());
        programFlow.setExtenalAccess(program.isExtenalAccess());
        programFlow.setDisplayIncidentDate(program.isDisplayIncidentDate());
        programFlow.setDateOfIncidentDescription(program.getDateOfIncidentDescription());
        programFlow.setRegistration(program.isRegistration());
        programFlow.setSelectEnrollmentDatesInFuture(program.isSelectEnrollmentDatesInFuture());
        programFlow.setDataEntryMethod(program.isDataEntryMethod());
        programFlow.setSingleEvent(program.isSingleEvent());
        programFlow.setIgnoreOverdueEvents(program.isIgnoreOverdueEvents());
        programFlow.setRelationshipFromA(program.isRelationshipFromA());
        programFlow.setSelectIncidentDatesInFuture(program.isSelectIncidentDatesInFuture());
        programFlow.setProgramStages(ProgramStage$Flow.fromModels(program.getProgramStages()));
        programFlow.setProgramTrackedEntityAttributes(ProgramTrackedEntityAttribute$Flow.fromModels(program.getProgramTrackedEntityAttributes()));
        return programFlow;
    }

    public static List<Program> toModels(List<Program$Flow> programFlows) {
        List<Program> programs = new ArrayList<>();

        if (programFlows != null && !programFlows.isEmpty()) {
            for (Program$Flow programFlow : programFlows) {
                programs.add(toModel(programFlow));
            }
        }

        return programs;
    }

    public static List<Program$Flow> fromModels(List<Program> programs) {
        List<Program$Flow> programFlows = new ArrayList<>();

        if (programs != null && !programs.isEmpty()) {
            for (Program program : programs) {
                programFlows.add(fromModel(program));
            }
        }

        return programFlows;
    }
}
