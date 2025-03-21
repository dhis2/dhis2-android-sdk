/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.testapp.trackedentity

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class TrackedEntityInstanceCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_all_teis_without_children() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstances().blockingGet()
        assertThat(trackedEntityInstances.size).isEqualTo(2)

        val trackedEntityInstance = trackedEntityInstances[0]
        assertThat(trackedEntityInstance.uid()).isEqualTo("nWrB0TfWlvh")
        assertThat(trackedEntityInstance.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(trackedEntityInstance.trackedEntityAttributeValues()).isNull()
    }

    @Test
    fun allow_access_to_one_tei_without_children() {
        val tei = d2.trackedEntityModule().trackedEntityInstances()
            .uid("nWrB0TfWlvh")
            .blockingGet()

        assertThat(tei!!.uid()).isEqualTo("nWrB0TfWlvh")
        assertThat(tei.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(tei.trackedEntityAttributeValues()).isNull()
    }

    @Test
    fun include_tracked_entity_attribute_values_as_children() {
        val tei = d2.trackedEntityModule().trackedEntityInstances()
            .withTrackedEntityAttributeValues().uid("nWrB0TfWlvD")
            .blockingGet()

        assertThat(tei!!.trackedEntityAttributeValues()!!.size).isEqualTo(2)
        assertThat(tei.trackedEntityAttributeValues()!![0].trackedEntityAttribute()).isEqualTo("aejWyOfXge6")
        assertThat(tei.trackedEntityAttributeValues()!![0].value()).isEqualTo("aefryrfXge5")
    }

    @Test
    fun include_program_owners_as_children() {
        val tei = d2.trackedEntityModule().trackedEntityInstances()
            .withProgramOwners().uid("nWrB0TfWlvD").blockingGet()

        assertThat(tei!!.programOwners()!!.size).isEqualTo(1)
        assertThat(tei.programOwners()!![0].program()).isEqualTo("IpHINAT79UW")
        assertThat(tei.programOwners()!![0].trackedEntityInstance()).isEqualTo("nWrB0TfWlvD")
        assertThat(tei.programOwners()!![0].ownerOrgUnit()).isEqualTo("DiszpKrYNg8")
    }

    @Test
    @Throws(D2Error::class)
    fun add_tracked_entity_instances_to_the_repository() {
        val trackedEntityInstances1 = d2.trackedEntityModule().trackedEntityInstances().blockingGet()
        assertThat(trackedEntityInstances1.size).isEqualTo(2)

        val teiUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(
            TrackedEntityInstanceCreateProjection.create("DiszpKrYNg8", "nEenWmSyUEp"),
        )

        val trackedEntityInstances2 = d2.trackedEntityModule().trackedEntityInstances().blockingGet()
        assertThat(trackedEntityInstances2.size).isEqualTo(3)

        val trackedEntityInstance = d2.trackedEntityModule().trackedEntityInstances().uid(teiUid).blockingGet()
        assertThat(trackedEntityInstance!!.uid()).isEqualTo(teiUid)

        d2.trackedEntityModule().trackedEntityInstances().uid(teiUid).blockingDelete()
    }
}
