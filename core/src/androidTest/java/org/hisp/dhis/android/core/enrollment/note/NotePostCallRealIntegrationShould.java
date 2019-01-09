package org.hisp.dhis.android.core.enrollment.note;

import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class NotePostCallRealIntegrationShould extends AbsStoreTestCase {
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

    @Test
    public void stub() throws Exception {

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

        d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList("AlvUHPP2Mes")).call();

        TrackedEntityInstance tei = trackedEntityInstanceStore.selectFirst();

        setTeiToPost(tei);

        addNote();

        d2.syncTrackedEntityInstances().call();

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