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
package org.hisp.dhis.android.testapp.datavalue

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValueObjectRepository
import org.hisp.dhis.android.core.datavalue.internal.DataValueStoreImpl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataValueObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun update_value() {
        val value = "new_value"
        val repository = objectRepository()
        repository.blockingSet(value)

        assertThat(repository.blockingGet()!!.value()).isEqualTo(value)
        repository.blockingDelete()
    }

    @Test
    fun update_follow_up() {
        val followUp = true
        val repository = objectRepository()
        repository.setFollowUp(followUp)

        assertThat(repository.blockingGet()!!.followUp()).isEqualTo(followUp)
        repository.blockingDelete()
    }

    @Test
    fun update_comment() {
        val comment = "comment"
        val repository = objectRepository()
        repository.setComment(comment)

        assertThat(repository.blockingGet()!!.comment()).isEqualTo(comment)
        repository.blockingDelete()
    }

    @Test
    fun delete_value_if_state_to_post() {
        val repository = objectRepository()
        repository.blockingSet("value")

        assertThat(repository.blockingExists()).isTrue()
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_POST)

        repository.blockingDelete()
        assertThat(repository.blockingExists()).isFalse()
    }

    @Test
    fun set_state_to_delete_if_state_is_not_to_post() = runTest {
        val repository = objectRepository()
        repository.blockingSet("value")
        DataValueStoreImpl(databaseAdapter).setState(repository.blockingGet()!!, State.ERROR)

        assertThat(repository.blockingExists()).isTrue()
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.ERROR)

        repository.blockingDelete()
        assertThat(repository.blockingExists()).isTrue()
        assertThat(repository.blockingGet()!!.deleted()).isTrue()
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_UPDATE)
    }

    @Test
    fun set_not_deleted_when_updating_deleted_value() = runTest {
        val repository = objectRepository()
        repository.blockingSet("value")
        DataValueStoreImpl(databaseAdapter).setState(repository.blockingGet()!!, State.TO_UPDATE)
        repository.blockingDelete()

        assertThat(repository.blockingGet()!!.deleted()).isTrue()
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_UPDATE)

        repository.blockingSet("new_value")
        assertThat(repository.blockingGet()!!.deleted()).isFalse()
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_UPDATE)
    }

    @Test
    fun return_that_a_value_exists_only_if_it_has_been_created() {
        assertThat(
            d2.dataValueModule().dataValues()
                .value(
                    "no_period",
                    "no_org_unit",
                    "no_data_element",
                    "no_category",
                    "no_attribute",
                ).blockingExists(),
        ).isFalse()

        assertThat(
            d2.dataValueModule().dataValues()
                .value(
                    "2018",
                    "DiszpKrYNg8",
                    "g9eOBujte1U",
                    "Gmbgme7z9BF",
                    "bRowv6yZOF2",
                ).blockingExists(),
        ).isTrue()
    }

    @Test
    @Throws(D2Error::class)
    fun not_update_status_when_passing_same_value() = runTest {
        val repository = objectRepository()
        repository.blockingSet("test_value")
        repository.setComment("Hey!")
        DataValueStoreImpl(databaseAdapter).setState(repository.blockingGet()!!, State.SYNCED)

        repository.setComment("Hey!")
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.SYNCED)

        repository.setComment("Hello!")
        assertThat(repository.blockingGet()!!.syncState()).isEqualTo(State.TO_UPDATE)

        // Set to TO_POST state to truly delete de data value
        DataValueStoreImpl(databaseAdapter).setState(repository.blockingGet()!!, State.TO_POST)
        repository.blockingDelete()
        assertThat(repository.blockingExists()).isFalse()
    }

    private fun objectRepository(): DataValueObjectRepository {
        return d2.dataValueModule().dataValues()
            .value(
                "201905",
                "DiszpKrYNg8",
                "g9eOBujte1U",
                "Gmbgme7z9BF",
                "bRowv6yZOF2",
            )
    }
}
