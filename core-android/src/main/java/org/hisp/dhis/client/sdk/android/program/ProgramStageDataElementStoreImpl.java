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

package org.hisp.dhis.client.sdk.android.program;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageDataElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageDataElementFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.List;

public final class ProgramStageDataElementStoreImpl
        extends AbsIdentifiableObjectStore<ProgramStageDataElement, ProgramStageDataElementFlow>
        implements ProgramStageDataElementStore {

    public ProgramStageDataElementStoreImpl() {
        super(ProgramStageDataElementFlow.MAPPER);
    }

    @Override
    public List<ProgramStageDataElement> query(ProgramStage programStage) {
        List<ProgramStageDataElementFlow> programStageDataElementFlows = new Select()
                .from(ProgramStageDataElementFlow.class)
                .where(ProgramStageDataElementFlow_Table
                        .programStage.is(programStage.getUId())).queryList();
        return getMapper().mapToModels(programStageDataElementFlows);
    }

    @Override
    public List<ProgramStageDataElement> query(ProgramStageSection programStageSection) {
        List<ProgramStageDataElementFlow> programStageDataElementFlows = new Select()
                .from(ProgramStageDataElementFlow.class)
                .where(ProgramStageDataElementFlow_Table
                        .programStageSection.is(programStageSection.getUId())).queryList();
        return getMapper().mapToModels(programStageDataElementFlows);
    }

    @Override
    public ProgramStageDataElement query(ProgramStage programStage, DataElement dataElement) {
        ProgramStageDataElementFlow programStageDataElementFlow = new Select()
                .from(ProgramStageDataElementFlow.class)
                .where(ProgramStageDataElementFlow_Table
                        .programStage.is(programStage.getUId()))
                .and(ProgramStageDataElementFlow_Table
                        .dataElement.is(dataElement.getUId())).querySingle();
        return getMapper().mapToModel(programStageDataElementFlow);
    }
}
