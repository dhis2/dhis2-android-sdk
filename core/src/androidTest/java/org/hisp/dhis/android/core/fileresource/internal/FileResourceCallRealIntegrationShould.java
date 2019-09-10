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

package org.hisp.dhis.android.core.fileresource.internal;

import androidx.test.InstrumentationRegistry;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.d2manager.D2Factory;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FileResourceCallRealIntegrationShould extends BaseRealIntegrationTest {

    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url2_33, databaseAdapter());
    }

    //@Test
    public void download_and_write_files_successfully() throws Exception {
        syncDataAndMetadata();

        d2.fileResourceModule().blockingDownload();

        List<FileResource> fileResources = d2.fileResourceModule().fileResources.blockingGet();

        assertThat(fileResources.size(), is(2));

        File file = new File(FileResourceUtil.getFileResourceDirectory(
                InstrumentationRegistry.getTargetContext().getApplicationContext()), fileResources.get(0).uid());

        assertThat(file.exists(), is(true));
    }

    //@Test
    public void write_files_and_upload() throws Exception {
        syncDataAndMetadata();

        d2.fileResourceModule().blockingDownload();

        List<FileResource> fileResources = d2.fileResourceModule().fileResources.blockingGet();

        File file = new File(FileResourceUtil.getFileResourceDirectory(
                InstrumentationRegistry.getTargetContext().getApplicationContext()), fileResources.get(0).uid());
        assertThat(file.exists(), is(true));

        String valueUid = d2.fileResourceModule().fileResources.blockingAdd(file);

        TrackedEntityAttribute trackedEntityAttribute =
                d2.trackedEntityModule().trackedEntityAttributes.byValueType().eq(ValueType.IMAGE).one().blockingGet();

        TrackedEntityInstance trackedEntityInstance =
                d2.trackedEntityModule().trackedEntityInstances.blockingGet().get(0);

        d2.trackedEntityModule().trackedEntityAttributeValues
                .value(trackedEntityAttribute.uid(), trackedEntityInstance.uid()).blockingSet(valueUid);

        List<FileResource> fileResources2 = d2.fileResourceModule().fileResources.blockingGet();

        d2.fileResourceModule().fileResources.upload().blockingSubscribe();

        List<FileResource> fileResources3 = d2.fileResourceModule().fileResources.blockingGet();

        File file2 = new File(fileResources3.get(2).path(), fileResources3.get(2).uid());

        assertThat(file2.exists(), is(true));
    }

    private void syncDataAndMetadata() throws Exception {
        d2.userModule().logIn("android", "Android123").blockingGet();

        d2.metadataModule().blockingDownload();

        d2.trackedEntityModule().trackedEntityInstanceDownloader
                .byProgramUid("uy2gU8kT1jF").limit(20).download().blockingSubscribe();
    }
}
