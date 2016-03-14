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

@Table(database = DbDhis.class)
public final class ProgramStageSectionFlow extends BaseIdentifiableObjectFlow {

    static final String PROGRAM_STAGE_KEY = "programstage";

    @Column
    int sortOrder;

    @Column
    boolean externalAccess;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_KEY,
                            columnType = String.class, foreignKeyColumnName = "uId"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStageFlow programStage;

    List<ProgramStageDataElementFlow> programStageDataElements;

    List<ProgramIndicatorFlow> programIndicators;

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public ProgramStageFlow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStageFlow programStage) {
        this.programStage = programStage;
    }

    public List<ProgramStageDataElementFlow> getProgramStageDataElements() {
        return programStageDataElements;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElementFlow> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public List<ProgramIndicatorFlow> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicatorFlow> programIndicators) {
        this.programIndicators = programIndicators;
    }

    public ProgramStageSectionFlow() {
        // empty constructor
    }
}
