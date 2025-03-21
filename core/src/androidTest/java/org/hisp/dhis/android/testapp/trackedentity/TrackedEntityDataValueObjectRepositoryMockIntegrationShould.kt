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
package org.hisp.dhis.android.testapp.trackedentity

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test

class TrackedEntityDataValueObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private val sampleEvent = "event1"
    private val sampleDataElement = "bx6fsa0t90x"

    @Before
    fun setup() {
        hardDeleteDataValue()
    }

    @After
    fun tearDown() {
        hardDeleteDataValue()
    }

    @Test
    fun create_value() {
        val value1 = "new_value"
        val value2 = "other_value"

        val repository = objectRepository()

        repository.blockingSet(value1)
        assertThat(repository.blockingGet()!!.value()).isEqualTo(value1)
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)

        repository.blockingSet(value2)
        assertThat(repository.blockingGet()!!.value()).isEqualTo(value2)
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)
    }

    @Test
    fun update_value() {
        val value1 = "new_value"
        val value2 = "other_value"

        val repository = objectRepository()

        repository.blockingSet(value1)
        assertThat(repository.blockingGet()!!.value()).isEqualTo(value1)
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)

        val value = repository.blockingGet()
        setDataValueStateToError(value!!)

        repository.blockingSet(value2)
        assertThat(repository.blockingGet()!!.value()).isEqualTo(value2)
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_UPDATE)
    }

    @Test
    fun delete_value() {
        val repository = objectRepository()

        repository.blockingSet("value")
        assertThat(repository.blockingExists()).isTrue()

        repository.blockingDelete()
        assertThat(repository.blockingExists()).isFalse()
    }

    @Test
    fun return_that_a_value_exists_only_if_it_has_been_created() {
        assertThat(
            d2.trackedEntityModule().trackedEntityDataValues()
                .value("no_event", "no_data_element")
                .blockingExists()
        ).isFalse()

        assertThat(
            d2.trackedEntityModule().trackedEntityDataValues()
                .value("single1", "jDx8LZlznYu")
                .blockingExists()
        ).isTrue()
    }

    @Test
    fun mark_a_value_as_deleted_using_the_delete_method() {
        val repository = objectRepository()
        repository.blockingSet("value")
        repository.blockingDelete()

        assertThat(repository.blockingExists()).isEqualTo(false)
        assertThat(repository.blockingGet()).isNull()
    }

    @Test
    fun mark_a_value_as_deleted_when_setting_a_null() {
        val repository = objectRepository()
        repository.blockingSet("value")
        repository.blockingSet(null)

        assertThat(repository.blockingExists()).isEqualTo(false)
        assertThat(repository.blockingGet()!!.value()).isEqualTo(null)
        assertThat(repository.blockingGet()!!.deleted()).isEqualTo(true)
    }

    private fun objectRepository(): TrackedEntityDataValueObjectRepository {
        return d2.trackedEntityModule().trackedEntityDataValues()
            .value(sampleEvent, sampleDataElement)
    }

    private fun hardDeleteDataValue() {
        val store: TrackedEntityDataValueStore = TrackedEntityDataValueStoreImpl(databaseAdapter)

        store.deleteWhereIfExists(
            WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, sampleEvent)
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, sampleDataElement)
                .build()
        )
    }

    private fun setDataValueStateToError(value: TrackedEntityDataValue) {
        val store: TrackedEntityDataValueStore = TrackedEntityDataValueStoreImpl(databaseAdapter)
        store.updateWhere(value.toBuilder().syncState(State.ERROR).build())
    }
}
