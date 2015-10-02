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

package org.hisp.dhis.android.sdk.core.persistence.models.program;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageDataElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageDataElement$Flow$Table;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;
import org.hisp.dhis.android.sdk.models.program.IProgramStageDataElementStore;
import org.hisp.dhis.android.sdk.models.program.ProgramStage;
import org.hisp.dhis.android.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.models.program.ProgramStageSection;

import java.util.List;

public final class ProgramStageDataElementStore implements IProgramStageDataElementStore {

    public ProgramStageDataElementStore() {
        //empty constructor
    }

    @Override
    public void insert(ProgramStageDataElement object) {
        ProgramStageDataElement$Flow programStageDataElementFlow = ProgramStageDataElement$Flow.fromModel(object);
        programStageDataElementFlow.insert();
    }

    @Override
    public void update(ProgramStageDataElement object) {
        ProgramStageDataElement$Flow.fromModel(object).update();
    }

    @Override
    public void save(ProgramStageDataElement object) {
        ProgramStageDataElement$Flow programStageDataElementFlow =
                ProgramStageDataElement$Flow.fromModel(object);
        programStageDataElementFlow.save();
    }

    @Override
    public void delete(ProgramStageDataElement object) {
        ProgramStageDataElement$Flow.fromModel(object).delete();
    }

    @Override
    public List<ProgramStageDataElement> queryAll() {
        List<ProgramStageDataElement$Flow> programStageDataElementFlows = new Select()
                .from(ProgramStageDataElement$Flow.class)
                .queryList();
        return ProgramStageDataElement$Flow.toModels(programStageDataElementFlows);
    }

    @Override
    public List<ProgramStageDataElement> query(ProgramStage programStage) {
        List<ProgramStageDataElement$Flow> programStageDataElementFlows = new Select()
                .from(ProgramStageDataElement$Flow.class)
                .where(Condition.column(ProgramStageDataElement$Flow$Table.PROGRAMSTAGE)
                        .is(programStage.getUId())).queryList();
        return ProgramStageDataElement$Flow.toModels(programStageDataElementFlows);
    }

    @Override
    public List<ProgramStageDataElement> query(ProgramStageSection programStageSection) {
        List<ProgramStageDataElement$Flow> programStageDataElementFlows = new Select()
                .from(ProgramStageDataElement$Flow.class)
                .where(Condition.column(ProgramStageDataElement$Flow$Table.PROGRAMSTAGESECTION)
                        .is(programStageSection.getUId())).queryList();
        return ProgramStageDataElement$Flow.toModels(programStageDataElementFlows);
    }

    @Override
    public ProgramStageDataElement query(ProgramStage programStage, DataElement dataElement) {
        ProgramStageDataElement$Flow programStageDataElementFlow = new Select()
                .from(ProgramStageDataElement$Flow.class)
                .where(Condition.column(ProgramStageDataElement$Flow$Table.PROGRAMSTAGE)
                        .is(programStage.getUId())).and(Condition
                        .column(ProgramStageDataElement$Flow$Table.DATAELEMENT)
                        .is(dataElement.getUId())).querySingle();
        return ProgramStageDataElement$Flow.toModel(programStageDataElementFlow);
    }
}
