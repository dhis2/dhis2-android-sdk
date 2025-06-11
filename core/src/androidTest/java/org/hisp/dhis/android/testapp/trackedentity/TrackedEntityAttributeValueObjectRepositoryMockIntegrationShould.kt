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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceObjectRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test

class TrackedEntityAttributeValueObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private var teiRepository: TrackedEntityInstanceObjectRepository? = null
    private var attributeRepository: TrackedEntityAttributeValueObjectRepository? = null
    private var attribute: String = "aejWyOfXge6"

    @Before
    @Throws(D2Error::class)
    fun setup() {
        val projection = TrackedEntityInstanceCreateProjection
            .create("DiszpKrYNg8", "nEenWmSyUEp")

        val uid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(projection)
        attributeRepository = objectRepository(uid)
        teiRepository = d2.trackedEntityModule().trackedEntityInstances().uid(uid)
    }

    @After
    @Throws(D2Error::class)
    fun teardown() {
        teiRepository!!.blockingDelete()
    }

    @Test
    fun create_value() {
        val value1 = "new_value"
        val value2 = "other_value"

        attributeRepository!!.blockingSet(value1)
        assertThat(attributeRepository!!.blockingGet()!!.value()).isEqualTo(value1)
        assertThat(attributeRepository!!.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)

        attributeRepository!!.blockingSet(value2)
        assertThat(attributeRepository!!.blockingGet()!!.value()).isEqualTo(value2)
        assertThat(attributeRepository!!.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)
    }

    @Test
    fun update_value() = runTest {
        val value1 = "new_value"
        val value2 = "other_value"

        attributeRepository!!.blockingSet(value1)
        assertThat(attributeRepository!!.blockingGet()!!.value()).isEqualTo(value1)
        assertThat(attributeRepository!!.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)

        val value = attributeRepository!!.blockingGet()
        setDataValueState(value!!, State.ERROR)

        attributeRepository!!.blockingSet(value2)
        assertThat(attributeRepository!!.blockingGet()!!.value()).isEqualTo(value2)
        assertThat(attributeRepository!!.blockingGet()!!.syncState()).isEqualTo(State.TO_UPDATE)
    }

    @Test
    fun delete_value() {
        attributeRepository!!.blockingSet("value")
        assertThat(attributeRepository!!.blockingExists()).isTrue()

        attributeRepository!!.blockingDelete()
        assertThat(attributeRepository!!.blockingExists()).isFalse()
    }

    @Test
    fun return_that_a_value_exists_only_if_it_has_been_created() {
        assertThat(
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .value("no_attribute", "no_instance")
                .blockingExists(),
        ).isFalse()

        assertThat(
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .value("cejWyOfXge6", "nWrB0TfWlvh")
                .blockingExists(),
        ).isTrue()
    }

    @Test
    fun mark_a_value_as_deleted_using_the_delete_method() {
        attributeRepository!!.blockingSet("value")
        attributeRepository!!.blockingDelete()

        assertThat(attributeRepository!!.blockingExists()).isEqualTo(false)
        assertThat(attributeRepository!!.blockingGet()).isNull()
    }

    @Test
    fun mark_a_value_as_deleted_when_setting_a_null() {
        attributeRepository!!.blockingSet("value")
        attributeRepository!!.blockingSet(null)

        assertThat(attributeRepository!!.blockingExists()).isEqualTo(false)
        assertThat(attributeRepository!!.blockingGet()!!.value()).isEqualTo(null)
        assertThat(attributeRepository!!.blockingGet()!!.deleted()).isEqualTo(true)
    }

    private fun objectRepository(teiUid: String): TrackedEntityAttributeValueObjectRepository {
        return d2.trackedEntityModule().trackedEntityAttributeValues()
            .value(attribute, teiUid)
    }

    companion object {
        suspend fun setDataValueState(value: TrackedEntityAttributeValue, syncState: State?) {
            val store: TrackedEntityAttributeValueStore = TrackedEntityAttributeValueStoreImpl(databaseAdapter)
            store.updateWhere(value.toBuilder().syncState(syncState).build())
        }
    }
}
