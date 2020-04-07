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
                .byValueType().in(ValueType.FILE_RESOURCE, ValueType.FILE_RESOURCE)
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
                .byState().notIn(State.SYNCED)
                .uid(resourceUid)
                .blockingExists();
    }
}
