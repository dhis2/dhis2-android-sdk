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

package org.hisp.dhis.android.core.sms.data.localdbrepository.internal;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElementModule;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.fileresource.FileResourceModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

class FileResourceCleaner {
    private final DataElementModule dataElementModule;
    private final TrackedEntityModule trackedEntityModule;
    private final FileResourceModule fileResourceModule;

    @Inject
    FileResourceCleaner(DataElementModule dataElementModule,
                        TrackedEntityModule trackedEntityModule,
                        FileResourceModule fileResourceModule) {
        this.dataElementModule = dataElementModule;
        this.trackedEntityModule = trackedEntityModule;
        this.fileResourceModule = fileResourceModule;
    }

    Single<Event> removeFileDataValues(Event event) {
        if (event.trackedEntityDataValues() == null || event.trackedEntityDataValues().isEmpty()) {
            return Single.just(event);
        }

        List<String> dataElementUids = new ArrayList<>();
        for (TrackedEntityDataValue value : event.trackedEntityDataValues()) {
            dataElementUids.add(value.dataElement());
        }

        return dataElementModule.dataElements()
                .byUid().in(dataElementUids)
                .byValueType().in(ValueType.FILE_RESOURCE, ValueType.IMAGE)
                .get()
                .map(fileDataElements -> {
                    if (fileDataElements.isEmpty()) {
                        return event;
                    }

                    List<String> fileDataElementUids = UidsHelper.getUidsList(fileDataElements);
                    List<TrackedEntityDataValue> newDataValues = new ArrayList<>();
                    for (TrackedEntityDataValue value : event.trackedEntityDataValues()) {
                        if (fileDataElementUids.contains(value.dataElement()) &&
                                isExistingAndNotSyncedFileResource(value.value())) {
                            continue;
                        }
                        newDataValues.add(value);
                    }
                    return event.toBuilder().trackedEntityDataValues(newDataValues).build();
                });
    }

    Single<TrackedEntityInstance> removeFileAttributeValues(TrackedEntityInstance instance) {
        if (instance.trackedEntityAttributeValues() == null || instance.trackedEntityAttributeValues().isEmpty()) {
            return Single.just(instance);
        }

        List<String> attributeUids = new ArrayList<>();
        for (TrackedEntityAttributeValue value : instance.trackedEntityAttributeValues()) {
            attributeUids.add(value.trackedEntityAttribute());
        }

        return trackedEntityModule.trackedEntityAttributes()
                .byUid().in(attributeUids)
                .byValueType().in(ValueType.FILE_RESOURCE, ValueType.IMAGE)
                .get()
                .map(fileAttributes -> {
                    if (fileAttributes.isEmpty()) {
                        return instance;
                    }

                    List<String> fileAttributeUids = UidsHelper.getUidsList(fileAttributes);
                    List<TrackedEntityAttributeValue> newAttributeValues = new ArrayList<>();
                    for (TrackedEntityAttributeValue value : instance.trackedEntityAttributeValues()) {
                        if (fileAttributeUids.contains(value.trackedEntityAttribute()) &&
                                isExistingAndNotSyncedFileResource(value.value())) {
                            continue;
                        }
                        newAttributeValues.add(value);
                    }
                    return instance.toBuilder().trackedEntityAttributeValues(newAttributeValues).build();
                });
    }

    private boolean isExistingAndNotSyncedFileResource(String resourceUid) {
        return fileResourceModule.fileResources()
                .bySyncState().notIn(State.SYNCED)
                .uid(resourceUid)
                .blockingExists();
    }
}
