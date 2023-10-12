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

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(D2JunitRunner.class)
public class TrackedEntityInstanceServiceMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void inherit_attributes() throws D2Error {
        String fromTeiUid = "nWrB0TfWlvh";

        String newTeiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(
                TrackedEntityInstanceCreateProjection.create("DiszpKrYNg8", "nEenWmSyUEp")
        );

        d2.trackedEntityModule().trackedEntityInstanceService()
                .blockingInheritAttributes(fromTeiUid, newTeiUid,"IpHINAT79UW");

        TrackedEntityInstance fromTEI = d2.trackedEntityModule().trackedEntityInstances().withTrackedEntityAttributeValues()
                .uid(fromTeiUid).blockingGet();

        TrackedEntityInstance newTEI = d2.trackedEntityModule().trackedEntityInstances().withTrackedEntityAttributeValues()
                .uid(newTeiUid).blockingGet();

        // Not equal because is an attribute with IMAGE value type
        assertThat(fromTEI.trackedEntityAttributeValues().get(0).value())
                .isNotEqualTo(newTEI.trackedEntityAttributeValues().get(0).value());
        // Equal because is not FILE or IMAGE value type
        assertThat(fromTEI.trackedEntityAttributeValues().get(1).value())
                .isEqualTo(newTEI.trackedEntityAttributeValues().get(1).value());


        // Remove TEI and attribute values
        for (TrackedEntityAttributeValue teaValue : d2.trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(newTeiUid).blockingGet()) {
            d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(teaValue.trackedEntityAttribute(), teaValue.trackedEntityInstance())
                    .blockingDeleteIfExist();
        }

        d2.trackedEntityModule().trackedEntityInstances().uid(newTeiUid).blockingDeleteIfExist();
    }
}