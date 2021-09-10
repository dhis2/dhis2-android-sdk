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

package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.data.database.DatabaseAssert;
import org.hisp.dhis.android.core.datastore.KeyValuePair;
import org.hisp.dhis.android.core.datastore.internal.LocalDataStoreStore;
import org.hisp.dhis.android.core.datavalue.DataValueConflict;
import org.hisp.dhis.android.core.datavalue.internal.DataValueConflictStore;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStoreImpl;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStoreImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore;
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType;
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobObject;
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobObjectStore;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher;
import org.junit.Test;

import java.util.Date;

public class WipeDBCallMockIntegrationShould extends BaseMockIntegrationTestEmptyDispatcher {

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        givenAFreshLoginInDatabase();

        givenAMetadataInDatabase();
        givenDataInDatabase();
        givenOthersInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter).isFull();

        d2.wipeModule().wipeEverything();

        DatabaseAssert.assertThatDatabase(databaseAdapter).isEmpty();
    }

    private void givenAFreshLoginInDatabase() {
        try {
            d2.userModule().logOut().blockingAwait();
        } catch (RuntimeException e) {
            // Do nothing
        } finally {
            d2.userModule().blockingLogIn("android", "Android123", dhis2MockServer.getBaseEndpoint());
        }
    }

    private void givenAMetadataInDatabase() {
        d2.metadataModule().blockingDownload();
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(1);
    }

    private void givenDataInDatabase() {
        d2.eventModule().eventDownloader().limit(1).blockingDownload();
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(1).blockingDownload();
        d2.aggregatedModule().data().blockingDownload();
    }

    private void givenOthersInDatabase() {
        D2ErrorStore.create(databaseAdapter).insert(D2Error.builder()
                .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
                .errorDescription("Sample error")
                .build());

        TrackerImportConflictStoreImpl.create(databaseAdapter).insert(TrackerImportConflict.builder().build());

        FileResourceStoreImpl.create(databaseAdapter).insert(FileResource.builder().uid("uid").build());
        TrackerJobObjectStore.create(databaseAdapter).insert(
                TrackerJobObject.builder()
                        .jobUid("uid")
                        .trackerType(TrackerImporterObjectType.EVENT)
                        .objectUid("oUid")
                        .lastUpdated(new Date())
                        .build()
        );
        DataValueConflictStore.create(databaseAdapter).insert(DataValueConflict.builder().build());
        LocalDataStoreStore.create(databaseAdapter).insert(
                KeyValuePair.builder()
                        .key("key1")
                        .value("value1")
                        .build()
        );
        LocalDataStoreStore.create(databaseAdapter).insert(
                KeyValuePair.builder()
                        .key("key2")
                        .value("value2")
                        .build()
        );
    }
}
