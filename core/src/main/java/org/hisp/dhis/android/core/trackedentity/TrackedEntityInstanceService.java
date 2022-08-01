/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeCollectionRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public class TrackedEntityInstanceService {

    private final TrackedEntityAttributeCollectionRepository trackedEntityAttributeRepository;
    private final TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValueRepository;
    private final ProgramTrackedEntityAttributeCollectionRepository programTrackedEntityAttributeRepository;

    @Inject
    TrackedEntityInstanceService(TrackedEntityAttributeCollectionRepository trackedEntityAttributeRepository,
                                 TrackedEntityAttributeValueCollectionRepository trackedEntityAttributeValueRepository,
                                 ProgramTrackedEntityAttributeCollectionRepository
                                         programTrackedEntityAttributeRepository) {
        this.trackedEntityAttributeRepository = trackedEntityAttributeRepository;
        this.trackedEntityAttributeValueRepository = trackedEntityAttributeValueRepository;
        this.programTrackedEntityAttributeRepository = programTrackedEntityAttributeRepository;
    }

    /**
     * Inherit the tracked entity attribute values from one TEI to another. It only inherits attributes that are marked
     * as "inherited=true" and that belong to program passed as parameter. This method is useful when creating new
     * relationships. Inherited values are persisted in database. Important: this is a blocking method and it should
     * not be executed in the main thread. Consider the asynchronous version
     * {@link #inheritAttributes(String, String, String)}.
     *
     * @param fromTeiUid TrackedEntityInstance to inherit values from.
     * @param toTeiUid TrackedEntityInstance that receive the inherited values.
     * @param programUid Only attributes associated to this program will be inherited.
     * @return Unit
     */
    public Unit blockingInheritAttributes(String fromTeiUid, String toTeiUid, String programUid) throws D2Error {
        List<ProgramTrackedEntityAttribute> programAttributes = programTrackedEntityAttributeRepository
                .byProgram().eq(programUid)
                .blockingGet();

        List<String> attributeUids = new ArrayList<>();
        for (ProgramTrackedEntityAttribute ptea : programAttributes) {
            attributeUids.add(UidsHelper.getUidOrNull(ptea.trackedEntityAttribute()));
        }

        List<String> inheritableAttributeUids = trackedEntityAttributeRepository
                .byUid().in(attributeUids)
                .byInherit().isTrue()
                .blockingGetUids();

        if (!inheritableAttributeUids.isEmpty()) {
            List<TrackedEntityAttributeValue> fromTeiAttributes = trackedEntityAttributeValueRepository
                    .byTrackedEntityInstance().eq(fromTeiUid)
                    .byTrackedEntityAttribute().in(inheritableAttributeUids)
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

    /**
     * Inherit the tracked entity attribute values from one TEI to another. It only inherits attributes that are marked
     * as "inherited=true" and that belong to program passed as parameter. This method is useful when creating new
     * relationships. Inherited values are persisted in database.
     *
     * @param fromTeiUid TrackedEntityInstance to inherit values from.
     * @param toTeiUid TrackedEntityInstance that receive the inherited values.
     * @param programUid Only attributes associated to this program will be inherited.
     * @return Unit
     */
    public Single<Unit> inheritAttributes(String fromTeiUid, String toTeiUid, String programUid) {
        return Single.fromCallable(() -> blockingInheritAttributes(fromTeiUid, toTeiUid, programUid));
    }
}