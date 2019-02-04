/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class ProgramTrackedEntityAttributeHandler extends IdentifiableSyncHandlerImpl<ProgramTrackedEntityAttribute> {

    private final SyncHandler<TrackedEntityAttribute> trackedEntityAttributeHandler;
    private final IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;

    @Inject
    ProgramTrackedEntityAttributeHandler(
            IdentifiableObjectStore<ProgramTrackedEntityAttribute> store,
            SyncHandler<TrackedEntityAttribute> trackedEntityAttributeHandler,
            IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore) {
        super(store);
        this.trackedEntityAttributeHandler = trackedEntityAttributeHandler;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
    }

    @Override
    protected void afterObjectHandled(ProgramTrackedEntityAttribute programTrackedEntityAttribute,
                                      HandleAction action) {
        if (action == HandleAction.Delete) {
            this.trackedEntityAttributeStore.delete(programTrackedEntityAttribute.trackedEntityAttribute().uid());
        } else {
            trackedEntityAttributeHandler.handle(programTrackedEntityAttribute.trackedEntityAttribute());
        }
    }

    @Override
    protected boolean deleteIfCondition(ProgramTrackedEntityAttribute programTrackedEntityAttribute) {
        return !programTrackedEntityAttribute.trackedEntityAttribute().access().read();
    }
}