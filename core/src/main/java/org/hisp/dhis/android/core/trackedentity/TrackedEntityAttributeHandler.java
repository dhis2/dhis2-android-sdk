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
package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.ObjectStyleHandler;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class TrackedEntityAttributeHandler {

    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final ObjectStyleHandler styleHandler;

    public TrackedEntityAttributeHandler(TrackedEntityAttributeStore trackedEntityAttributeStore,
                                         ObjectStyleHandler styleHandler) {
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.styleHandler = styleHandler;
    }

    public void handleTrackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        deleteOrPersistTrackedEntityAttributes(trackedEntityAttribute);
    }

    private void deleteOrPersistTrackedEntityAttributes(TrackedEntityAttribute trackedEntityAttribute) {
        if (trackedEntityAttribute == null) {
            return;
        }

        if (isDeleted(trackedEntityAttribute)) {
            trackedEntityAttributeStore.delete(trackedEntityAttribute.uid());
        } else {
            String optionSetUid = null;
            if (trackedEntityAttribute.optionSet() != null) {
                optionSetUid = trackedEntityAttribute.optionSet().uid();
            }

            int updatedRow = trackedEntityAttributeStore.update(
                    trackedEntityAttribute.uid(), trackedEntityAttribute.code(),
                    trackedEntityAttribute.name(), trackedEntityAttribute.displayName(),
                    trackedEntityAttribute.created(), trackedEntityAttribute.lastUpdated(),
                    trackedEntityAttribute.shortName(), trackedEntityAttribute.displayShortName(),
                    trackedEntityAttribute.description(), trackedEntityAttribute.displayDescription(),
                    trackedEntityAttribute.pattern(), trackedEntityAttribute.sortOrderInListNoProgram(),
                    optionSetUid,
                    trackedEntityAttribute.valueType(), trackedEntityAttribute.expression(),
                    trackedEntityAttribute.searchScope(),
                    trackedEntityAttribute.programScope(), trackedEntityAttribute.displayInListNoProgram(),
                    trackedEntityAttribute.generated(), trackedEntityAttribute.displayOnVisitSchedule(),
                    trackedEntityAttribute.orgUnitScope(), trackedEntityAttribute.unique(),
                    trackedEntityAttribute.inherit(), trackedEntityAttribute.uid());

            if (updatedRow <= 0) {
                trackedEntityAttributeStore.insert(
                        trackedEntityAttribute.uid(), trackedEntityAttribute.code(),
                        trackedEntityAttribute.name(), trackedEntityAttribute.displayName(),
                        trackedEntityAttribute.created(), trackedEntityAttribute.lastUpdated(),
                        trackedEntityAttribute.shortName(), trackedEntityAttribute.displayShortName(),
                        trackedEntityAttribute.description(), trackedEntityAttribute.displayDescription(),
                        trackedEntityAttribute.pattern(), trackedEntityAttribute.sortOrderInListNoProgram(),
                        optionSetUid,
                        trackedEntityAttribute.valueType(), trackedEntityAttribute.expression(),
                        trackedEntityAttribute.searchScope(), trackedEntityAttribute.programScope(),
                        trackedEntityAttribute.displayInListNoProgram(),
                        trackedEntityAttribute.generated(), trackedEntityAttribute.displayOnVisitSchedule(),
                        trackedEntityAttribute.orgUnitScope(), trackedEntityAttribute.unique(),
                        trackedEntityAttribute.inherit());
            }
        }

        styleHandler.handle(trackedEntityAttribute.style(), trackedEntityAttribute.uid(),
                TrackedEntityAttributeModel.TABLE);
    }
}
