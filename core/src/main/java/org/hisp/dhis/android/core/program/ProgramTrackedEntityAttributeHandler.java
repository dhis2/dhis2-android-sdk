/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;

public class ProgramTrackedEntityAttributeHandler extends
        IdentifiableHandlerImpl<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeModel> {

    private final TrackedEntityAttributeHandler trackedEntityAttributeHandler;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;


    ProgramTrackedEntityAttributeHandler(IdentifiableObjectStore<ProgramTrackedEntityAttributeModel> store,
                                         TrackedEntityAttributeHandler trackedEntityAttributeHandler,
                                         TrackedEntityAttributeStore trackedEntityAttributeStore) {
        super(store);
        this.trackedEntityAttributeHandler = trackedEntityAttributeHandler;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
    }

    public static ProgramTrackedEntityAttributeHandler create(DatabaseAdapter databaseAdapter) {
        return new ProgramTrackedEntityAttributeHandler(
                ProgramTrackedEntityAttributeStore.create(databaseAdapter),
                TrackedEntityAttributeHandler.create(databaseAdapter),
                new TrackedEntityAttributeStoreImpl(databaseAdapter));
    }

    @Override
    protected void afterObjectHandled(ProgramTrackedEntityAttribute programTrackedEntityAttribute,
                                      HandleAction action) {
        if (action == HandleAction.Delete) {
            this.trackedEntityAttributeStore.delete(programTrackedEntityAttribute.trackedEntityAttribute().uid());
        } else {
            trackedEntityAttributeHandler.handleTrackedEntityAttribute(
                    programTrackedEntityAttribute.trackedEntityAttribute());
        }
    }

    @Override
    protected boolean deleteIfCondition(ProgramTrackedEntityAttribute programTrackedEntityAttribute) {
        return !programTrackedEntityAttribute.trackedEntityAttribute().access().read();
    }
}