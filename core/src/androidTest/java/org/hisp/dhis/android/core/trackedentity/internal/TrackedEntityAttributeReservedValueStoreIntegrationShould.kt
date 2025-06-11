/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.*
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseIntegrationTestWithDatabase
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStoreImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.util.Date

@RunWith(JUnit4::class)
class TrackedEntityAttributeReservedValueStoreIntegrationShould : BaseIntegrationTestWithDatabase() {
    private lateinit var expiredValue: TrackedEntityAttributeReservedValue
    private lateinit var notExpiredValue: TrackedEntityAttributeReservedValue
    private lateinit var temporalValidityExpiredValue: TrackedEntityAttributeReservedValue
    private lateinit var notExpiredTemporalValidityExpiredValue: TrackedEntityAttributeReservedValue

    private lateinit var serverDate: Date
    private val orgUnitUid = "orgu1"
    private val ownerUid = "owUid"

    // object to test
    private lateinit var store: TrackedEntityAttributeReservedValueStore
    private lateinit var organisationUnitStore: OrganisationUnitStore

    @Before
    @Throws(IOException::class)
    override fun setUp() = runTest {
        super.setUp()
        store = TrackedEntityAttributeReservedValueStoreImpl(databaseAdapter())

        serverDate = parseDate("2018-05-13T12:35:36.743")
        val expiredDate = parseDate("2018-05-12T12:35:36.743")
        val notExpiredDate = parseDate("2018-05-17T12:35:36.743")

        val organisationUnit = OrganisationUnit.builder().uid(orgUnitUid).build()
        organisationUnitStore = OrganisationUnitStoreImpl(databaseAdapter())
        organisationUnitStore.insert(organisationUnit)

        val builder = TrackedEntityAttributeReservedValue.builder()
            .ownerObject("owObj")
            .ownerUid(ownerUid)
            .key("key")
            .organisationUnit(orgUnitUid)
            .created(Date())

        expiredValue = builder.expiryDate(expiredDate).temporalValidityDate(null).value("v1").build()
        notExpiredValue = builder.id(1L).expiryDate(notExpiredDate).temporalValidityDate(null).value("v2").build()
        temporalValidityExpiredValue = builder.expiryDate(notExpiredDate).temporalValidityDate(expiredDate).value("v3")
            .build()
        notExpiredTemporalValidityExpiredValue =
            builder.id(1L).expiryDate(notExpiredDate).temporalValidityDate(notExpiredDate)
                .value("v3").build()
    }

    @After
    override fun tearDown() = runTest {
        store.delete()
        organisationUnitStore.delete()
        super.tearDown()
    }

    @Test
    fun delete_expired_reserved_values() = runTest {
        store.insert(expiredValue)
        store.deleteExpired(serverDate)
        storeContains(expiredValue, false)
    }

    @Test
    fun delete_temporal_validity_expired_reserved_values() = runTest {
        store.insert(temporalValidityExpiredValue)
        store.deleteExpired(serverDate)
        storeContains(temporalValidityExpiredValue, false)
    }

    @Test
    fun not_delete_temporal_validity_not_expired_reserved_values() = runTest {
        store.insert(notExpiredTemporalValidityExpiredValue)
        store.deleteExpired(serverDate)
        storeContains(notExpiredTemporalValidityExpiredValue, true)
    }

    @Test
    fun not_delete_not_expired_reserved_values() = runTest {
        store.insert(notExpiredValue)
        store.deleteExpired(serverDate)
        storeContains(notExpiredValue, true)
    }

    @Test
    fun pop_inserted_value() = runTest {
        store.insert(notExpiredValue)
        val returnedValue = store.popOne(ownerUid, orgUnitUid)
        assertThat(returnedValue?.value()).isEqualTo(notExpiredValue.value())
    }

    @Test
    fun leave_store_empty_after_pop_only_value() = runTest {
        store.insert(notExpiredValue)
        val value = store.popOne(ownerUid, orgUnitUid)
        storeContains(value!!, false)
    }

    private suspend fun storeContains(value: TrackedEntityAttributeReservedValue, contains: Boolean) {
        val values: List<TrackedEntityAttributeReservedValue> = store.selectAll()
        assertThat(values.contains(value)).isEqualTo(contains)
    }
}