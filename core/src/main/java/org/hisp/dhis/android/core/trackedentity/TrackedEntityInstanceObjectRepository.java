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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteWithUidDataObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.Date;
import java.util.Map;

public final class TrackedEntityInstanceObjectRepository
        extends ReadWriteWithUidDataObjectRepositoryImpl<TrackedEntityInstance, TrackedEntityInstanceObjectRepository>
        implements ReadWriteObjectRepository<TrackedEntityInstance> {

    TrackedEntityInstanceObjectRepository(final TrackedEntityInstanceStore store,
                                          final String uid,
                                          final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders,
                                          final RepositoryScope scope) {
        super(store, childrenAppenders, scope,
                s -> new TrackedEntityInstanceObjectRepository(store, uid, childrenAppenders, s));
    }

    public Unit setOrganisationUnitUid(String organisationUnitUid) throws D2Error {
        return updateObject(updateBuilder().organisationUnit(organisationUnitUid).build());
    }

    public Unit setCoordinates(String coordinates) throws D2Error {
        return updateObject(updateBuilder().coordinates(coordinates).build());
    }

    private TrackedEntityInstance.Builder updateBuilder() throws D2Error {
        TrackedEntityInstance trackedEntityInstance = getWithoutChildren();
        State state = trackedEntityInstance.state();
        if (state == State.RELATIONSHIP) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.RELATIONSHIPS_CANT_BE_UPDATED)
                    .errorDescription("Relationships can't be updated")
                    .build();
        }
        Date updateDate = new Date();
        state = state == State.TO_POST || state == State.TO_DELETE ? state : State.TO_UPDATE;

        return trackedEntityInstance.toBuilder()
                .state(state)
                .lastUpdated(updateDate)
                .lastUpdatedAtClient(BaseIdentifiableObject.dateToDateStr(updateDate));
    }
}