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

package org.hisp.dhis.android.core.wipe;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.data.database.DatabaseAssert;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventCallFactory;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class WipeDBCallRealIntegrationShould extends BaseRealIntegrationTest {

    //@Test
    public void have_empty_database_when_wipe_db_after_sync_metadata() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeEverything();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isEmpty();
    }

    //@Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        Callable<List<Event>> eventCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0, Collections.emptyList());

        eventCall.call();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeEverything();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isEmpty();
    }

    //@Test
    public void do_not_have_metadata_when_wipe_metadata_after_sync_metadata() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeMetadata();

        DatabaseAssert.assertThatDatabase(d2.databaseAdapter()).isEmpty();
    }

    //@Test
    public void do_not_have_data_when_wipe_data_after_sync() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(5).blockingDownload();

        TrackedEntityInstanceStore trackedEntityInstanceStore =
                TrackedEntityInstanceStoreImpl.create(d2.databaseAdapter());

        boolean hasTrackedEntities = trackedEntityInstanceStore.count() > 0;

        assertThat(hasTrackedEntities).isTrue();

        d2.wipeModule().wipeData();

        hasTrackedEntities = trackedEntityInstanceStore.count() > 0;

        assertThat(hasTrackedEntities).isFalse();
    }
}