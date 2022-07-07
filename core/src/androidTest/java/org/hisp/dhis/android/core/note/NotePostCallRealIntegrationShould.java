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

package org.hisp.dhis.android.core.note;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.Enrollment;

public class NotePostCallRealIntegrationShould extends BaseRealIntegrationTest {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */

    //@Test
    public void download_tei_add_one_note_and_sync_in_2_29() throws Exception {
        downloadUpdateAndSyncTei(RealServerMother.url2_29);
    }

    //@Test
    public void download_tei_add_one_note_and_sync_in_2_30_or_more() throws Exception {
        downloadUpdateAndSyncTei(RealServerMother.url2_31);
    }

    private void downloadUpdateAndSyncTei(String serverUrl) throws Exception {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut();
        }

        d2.userModule().blockingLogIn(username, password, serverUrl);

        d2.metadataModule().blockingDownload();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(100).blockingDownload();

        addNote();

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        d2.wipeModule().wipeEverything();
    }

    private void addNote() {
        Enrollment enrollment = d2.enrollmentModule().enrollments().one().blockingGet();
        try {
            d2.noteModule().notes().blockingAdd(NoteCreateProjection.create(
                    Note.NoteType.ENROLLMENT_NOTE, enrollment.uid(), "New note"));
        } catch (Exception ignored) {
        }
    }
}