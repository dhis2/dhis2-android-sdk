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

package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public class ProgramStageDataElementService implements IProgramStageDataElementService {
    private IProgramStageDataElementStore programStageDataElementStore;

    public ProgramStageDataElementService(IProgramStageDataElementStore programStageDataElementStore) {
        this.programStageDataElementStore = programStageDataElementStore;
    }

    @Override
    public List<ProgramStageDataElement> list(ProgramStage programStage) {
        Preconditions.isNull(programStage, "Object must not be null");
        return programStageDataElementStore.query(programStage);
    }

    @Override
    public List<ProgramStageDataElement> list(ProgramStageSection programStageSection) {
        Preconditions.isNull(programStageSection, "Object must not be null");
        return programStageDataElementStore.query(programStageSection);
    }

    @Override
    public ProgramStageDataElement query(ProgramStage programStage, DataElement dataElement) {
        Preconditions.isNull(programStage, "Object must not be null");
        Preconditions.isNull(dataElement, "Object must not be null");
        return programStageDataElementStore.query(programStage, dataElement);
    }

    @Override
    public ProgramStageDataElement get(long id) {
        return programStageDataElementStore.queryById(id);
    }

    @Override
    public List<ProgramStageDataElement> list() {
        return programStageDataElementStore.queryAll();
    }

    @Override
    public boolean remove(ProgramStageDataElement object) {
        Preconditions.isNull(object, "Object must not be null");
        return programStageDataElementStore.delete(object);
    }

    @Override
    public boolean save(ProgramStageDataElement object) {
        Preconditions.isNull(object, "Object must not be null");
        return programStageDataElementStore.save(object);
    }
}
