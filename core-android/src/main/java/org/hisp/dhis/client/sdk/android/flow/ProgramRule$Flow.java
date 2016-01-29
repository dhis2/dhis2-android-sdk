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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ProgramRule$Flow extends BaseIdentifiableObject$Flow {

    private static final String PROGRAM_STAGE_KEY = "programstage";
    private static final String PROGRAM_KEY = "program";

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    ProgramStage$Flow programStage;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_KEY, columnType = String.class, foreignColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.NO_ACTION
    )
    Program$Flow program;

    @Column
    String condition;

    @Column
    String description;

    @Column
    Integer priority;

    @Column
    boolean externalAction;

    List<ProgramRuleAction$Flow> programRuleActions;

    public ProgramStage$Flow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage$Flow programStage) {
        this.programStage = programStage;
    }

    public Program$Flow getProgram() {
        return program;
    }

    public void setProgram(Program$Flow program) {
        this.program = program;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isExternalAction() {
        return externalAction;
    }

    public void setExternalAction(boolean externalAction) {
        this.externalAction = externalAction;
    }

    public List<ProgramRuleAction$Flow> getProgramRuleActions() {
        return programRuleActions;
    }

    public void setProgramRuleActions(List<ProgramRuleAction$Flow> programRuleActions) {
        this.programRuleActions = programRuleActions;
    }

    public ProgramRule$Flow() {
        // empty constructor
    }
}
