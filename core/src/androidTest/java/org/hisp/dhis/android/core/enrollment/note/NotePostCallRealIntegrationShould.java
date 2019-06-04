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

package org.hisp.dhis.android.core.enrollment.note;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.IOException;

public class NotePostCallRealIntegrationShould extends BaseRealIntegrationTest {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;

    private TrackedEntityInstanceStore trackedEntityInstanceStore;


    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter());
    }

    //@Test
    public void download_tei_add_one_note_and_sync_in_2_29() throws Exception {
        d2 = D2Factory.create(RealServerMother.url2_29, databaseAdapter());
        downloadUpdateAndSyncTei();
    }

    //@Test
    public void download_tei_add_one_note_and_sync_in_2_30_or_more() throws Exception {
        d2 = D2Factory.create(RealServerMother.url2_30, databaseAdapter());
        downloadUpdateAndSyncTei();
    }

    public void downloadUpdateAndSyncTei() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        d2.trackedEntityModule().downloadTrackedEntityInstancesByUid(Lists.newArrayList("AlvUHPP2Mes")).call();

        TrackedEntityInstance tei = trackedEntityInstanceStore.selectFirst();

        setTeiToPost(tei);

        addNote();

        d2.trackedEntityModule().trackedEntityInstances.upload().call();

        d2.wipeModule().wipeEverything();
    }

    private void setTeiToPost(TrackedEntityInstance tei) {
        trackedEntityInstanceStore.update(tei.toBuilder().state(State.TO_POST).build());
    }

    private void addNote() {
        ObjectWithoutUidStore<Note> noteStore = NoteStore.create(databaseAdapter());
        Note note = noteStore.selectFirst();
        if (note == null) {
            throw new RuntimeException("There is no stored notes.");
        } else {
            noteStore.updateOrInsertWhere(note.toBuilder()
                    .uid(new CodeGeneratorImpl().generate())
                    .value("New note").state(State.TO_POST).build());
        }
    }
}