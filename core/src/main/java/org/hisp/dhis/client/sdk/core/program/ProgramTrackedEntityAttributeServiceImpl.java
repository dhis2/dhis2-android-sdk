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

import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;


public class ProgramTrackedEntityAttributeServiceImpl
        implements ProgramTrackedEntityAttributeService {
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    public ProgramTrackedEntityAttributeServiceImpl(ProgramTrackedEntityAttributeStore
                                                        programTrackedEntityAttributeStore) {
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
    }

    @Override
    public List<ProgramTrackedEntityAttribute> list(Program program) {
        Preconditions.isNull(program, "Object must not be null");
        return programTrackedEntityAttributeStore.query(program);
    }

    @Override
    public ProgramTrackedEntityAttribute list(Program program, TrackedEntityAttribute
            trackedEntityAttribute) {
        Preconditions.isNull(program, "Object must not be null");
        Preconditions.isNull(trackedEntityAttribute, "Object must not be null");
        return programTrackedEntityAttributeStore.query(program, trackedEntityAttribute);
    }

    @Override
    public ProgramTrackedEntityAttribute get(long id) {
        ProgramTrackedEntityAttribute programTrackedEntityAttribute =
                programTrackedEntityAttributeStore.queryById(id);
        return programTrackedEntityAttribute;
    }

    @Override
    public List<ProgramTrackedEntityAttribute> list() {
        return programTrackedEntityAttributeStore.queryAll();
    }

    @Override
    public boolean remove(ProgramTrackedEntityAttribute object) {
        Preconditions.isNull(object, "Object must not be null");
        return programTrackedEntityAttributeStore.delete(object);
    }

    @Override
    public boolean save(ProgramTrackedEntityAttribute object) {
        Preconditions.isNull(object, "Object must not be null");
        return programTrackedEntityAttributeStore.save(object);
    }
}
