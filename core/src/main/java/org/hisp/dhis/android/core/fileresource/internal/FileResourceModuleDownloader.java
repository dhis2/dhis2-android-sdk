/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.fileresource.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.modules.internal.MetadataModuleDownloader;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class FileResourceModuleDownloader implements MetadataModuleDownloader<Unit> {

    private final FileResourceCallFactory fileResourceCallFactory;
    private final IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;
    private final IdentifiableObjectStore<DataElement> dataElementStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IdentifiableDataObjectStore<FileResource> fileResourceStore;

    @Inject
    FileResourceModuleDownloader(FileResourceCallFactory fileResourceCallFactory,
                                 IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore,
                                 IdentifiableObjectStore<DataElement> dataElementStore,
                                 TrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                                 TrackedEntityDataValueStore trackedEntityDataValueStore,
                                 IdentifiableDataObjectStore<FileResource> fileResourceStore) {
        this.fileResourceCallFactory = fileResourceCallFactory;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.dataElementStore = dataElementStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.fileResourceStore = fileResourceStore;
    }

    public Callable<Unit> downloadMetadata() {
        return () -> {
            fileResourceCallFactory.create(
                    getTrackedEntityAttributeValues(),
                    getTrackedEntityDataValues())
                    .call();
            return new Unit();
        };
    }

    private List<String> getExistingFileResources() {
        return fileResourceStore.selectUids();
    }

    private List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues() {
        String attributeUidsWhereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityAttributeTableInfo.Columns.VALUE_TYPE, ValueType.IMAGE).build();

        List<String> trackedEntityAttributeUids = trackedEntityAttributeStore.selectUidsWhere(attributeUidsWhereClause);

        String attributeValuesWhereClause = new WhereClauseBuilder()
                .appendInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                        trackedEntityAttributeUids)
                .appendNotInKeyStringValues(TrackedEntityAttributeValueTableInfo.Columns.VALUE,
                        getExistingFileResources())
                .build();

        return trackedEntityAttributeValueStore.selectWhere(attributeValuesWhereClause);
    }

    private List<TrackedEntityDataValue> getTrackedEntityDataValues() {
        String dataElementUidsWhereClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataElementTableInfo.Columns.VALUE_TYPE, ValueType.IMAGE).build();

        List<String> dataElementUids = dataElementStore.selectUidsWhere(dataElementUidsWhereClause);

        String dataValuesWhereClause = new WhereClauseBuilder()
                .appendInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
                .appendNotInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.VALUE, getExistingFileResources())
                .build();

        return trackedEntityDataValueStore.selectWhere(dataValuesWhereClause);
    }
}