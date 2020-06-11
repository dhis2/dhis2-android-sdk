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
package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueCollectionRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public class RelationshipDomain {

    private TrackedEntityAttributeCollectionRepository trackedEntityAttributeRepository;
    private TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValueRepository;
    private ProgramTrackedEntityAttributeCollectionRepository programTrackedEntityAttributeRepository;

    @Inject
    RelationshipDomain(TrackedEntityAttributeCollectionRepository trackedEntityAttributeRepository,
                       TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValueRepository,
                       ProgramTrackedEntityAttributeCollectionRepository programTrackedEntityAttributeRepository) {
        this.trackedEntityAttributeRepository = trackedEntityAttributeRepository;
        this.trackedEntityAttributeValueRepository = trackedEntityAttributeValueRepository;
        this.programTrackedEntityAttributeRepository = programTrackedEntityAttributeRepository;
    }

    public Unit blockingInheritAttributesFrom(String fromTeiUid, String toTeiUid, String programUid) throws D2Error {
        List<ProgramTrackedEntityAttribute> programAttributes = programTrackedEntityAttributeRepository
                .byProgram().eq(programUid)
                .blockingGet();

        List<TrackedEntityAttribute> inheritableAttributes = trackedEntityAttributeRepository
                .byUid().in(UidsHelper.getUids(programAttributes))
                .byInherit().isTrue()
                .blockingGet();

        if (!inheritableAttributes.isEmpty()) {
            List<TrackedEntityAttributeValue> fromTeiAttributes = trackedEntityAttributeValueRepository
                    .byTrackedEntityInstance().eq(fromTeiUid)
                    .byTrackedEntityAttribute().in(UidsHelper.getUids(inheritableAttributes))
                    .blockingGet();

            if (!fromTeiAttributes.isEmpty()) {
                for (TrackedEntityAttributeValue attributeValue : fromTeiAttributes) {
                    trackedEntityAttributeValueRepository
                            .value(attributeValue.trackedEntityAttribute(), toTeiUid)
                            .blockingSet(attributeValue.value());
                }
            }
        }

        return new Unit();
    }

    public Single<Unit> inheritAttributesFrom(String fromTeiUid, String toTeiUid, String programUid) {
        return Single.fromCallable(() -> blockingInheritAttributesFrom(fromTeiUid, toTeiUid, programUid));
    }
}