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

package org.hisp.dhis.android.testapp.trackedentity;

import static com.google.common.truth.Truth.assertThat;

import static org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper.getFileResourceDirectory;
import static org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.writeInputStream;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.hisp.dhis.android.core.data.fileresource.RandomGeneratedInputStream;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.InputStream;

@RunWith(D2JunitRunner.class)
public class TrackedEntityInstanceServiceMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void inherit_attributes() throws D2Error {
        File file = storeFile("to_inherit_file");

        String fromTeiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(
                TrackedEntityInstanceCreateProjection.create("DiszpKrYNg8", "nEenWmSyUEp")
        );

        d2.enrollmentModule().enrollments().blockingAdd(
                EnrollmentCreateProjection.create("DiszpKrYNg8", "IpHINAT79UW", fromTeiUid)
        );

        d2.trackedEntityModule().trackedEntityAttributeValues().value("cejWyOfXge6", fromTeiUid)
                .blockingSet("value1");

        String fileResourceUid = d2.fileResourceModule().fileResources().blockingAdd(file);

        d2.trackedEntityModule().trackedEntityAttributeValues().value("aejWyOfXge6", fromTeiUid)
                .blockingSet(fileResourceUid);


        String toTeiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(
                TrackedEntityInstanceCreateProjection.create("DiszpKrYNg8", "nEenWmSyUEp")
        );

        d2.trackedEntityModule().trackedEntityInstanceService()
                .blockingInheritAttributes(fromTeiUid, toTeiUid,"IpHINAT79UW");

        TrackedEntityInstance fromTEI = d2.trackedEntityModule().trackedEntityInstances()
                .withTrackedEntityAttributeValues()
                .uid(fromTeiUid).blockingGet();

        TrackedEntityInstance newTEI = d2.trackedEntityModule().trackedEntityInstances()
                .withTrackedEntityAttributeValues()
                .uid(toTeiUid).blockingGet();

        // NOT equal because is an attribute with IMAGE value type
        assertThat(fromTEI.trackedEntityAttributeValues().get(0).value())
                .isNotEqualTo(newTEI.trackedEntityAttributeValues().get(0).value());

        // Equal because is not FILE or IMAGE value type
        assertThat(fromTEI.trackedEntityAttributeValues().get(1).value())
                .isEqualTo(newTEI.trackedEntityAttributeValues().get(1).value());

        d2.trackedEntityModule().trackedEntityInstances().uid(fromTeiUid).blockingDeleteIfExist();
        d2.trackedEntityModule().trackedEntityInstances().uid(toTeiUid).blockingDeleteIfExist();
    }

    private File storeFile(String fileName) {
        InputStream inputStream  = new RandomGeneratedInputStream(1024);
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        File destinationFile = new File(getFileResourceDirectory(context), fileName + ".png");
        return writeInputStream(inputStream, destinationFile, 1024);
    }
}