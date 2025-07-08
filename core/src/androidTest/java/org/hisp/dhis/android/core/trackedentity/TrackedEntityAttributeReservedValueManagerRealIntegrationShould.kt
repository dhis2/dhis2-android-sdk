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
package org.hisp.dhis.android.core.trackedentity

import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.persistence.category.CategoryComboTableInfo
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.FUTURE_DATE
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitProgramLinkStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueHandler
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.junit.Before
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito

@SuppressWarnings("MaxLineLength")
internal class TrackedEntityAttributeReservedValueManagerRealIntegrationShould : BaseRealIntegrationTest() {
    private lateinit var store: TrackedEntityAttributeReservedValueStore
    private val organisationUnitUid = "org_unit_uid"
    private val programUid = "program_uid"
    private val categoryComboUid = "category_combo_uid"
    private val ownerUid = "xs8A6tQJY0s"
    private lateinit var organisationUnit: OrganisationUnit
    private var pattern: String? = null

    @Mock
    val trackedEntityAttributeReservedValueQueryCallFactory: QueryCallFactory<
        TrackedEntityAttributeReservedValue,
        TrackedEntityAttributeReservedValueQuery,
        >? =
        null

    @Mock
    val trackedEntityAttributeReservedValueCall: List<TrackedEntityAttributeReservedValue>? =
        null

    @Captor
    private val trackedEntityAttributeReservedValueQueryCaptor:
        ArgumentCaptor<TrackedEntityAttributeReservedValueQuery>? = null
    private var manager: TrackedEntityAttributeReservedValueManager? = null

    @Before
    override fun setUp() = runTest {
        super.setUp()
        login()
        store = koin.get()
        val organisationUnitStore: OrganisationUnitStore = koin.get()
        val trackedEntityAttributeStore: TrackedEntityAttributeStore = koin.get()
        manager = d2.trackedEntityModule().reservedValueManager()
        val handler = TrackedEntityAttributeReservedValueHandler(store)
        val trackedEntityAttributeReservedValues: MutableList<TrackedEntityAttributeReservedValue> =
            ArrayList()
        val reservedValueBuilder = TrackedEntityAttributeReservedValue.builder()
            .ownerObject("owner_obj")
            .ownerUid(ownerUid)
            .key("key")
            .created(CREATED)
            .expiryDate(FUTURE_DATE)
            .organisationUnit(organisationUnitUid)
        val reservedValue1 = reservedValueBuilder.value("value1").build()
        val reservedValue2 = reservedValueBuilder.value("value2").build()
        val reservedValue3 = reservedValueBuilder.value("value3").build()
        trackedEntityAttributeReservedValues.add(reservedValue1)
        trackedEntityAttributeReservedValues.add(reservedValue2)
        trackedEntityAttributeReservedValues.add(reservedValue3)

        organisationUnit =
            OrganisationUnit.builder().uid(organisationUnitUid).code("org_unit_code").build()
        organisationUnitStore.insert(organisationUnit)

        pattern = "CURRENT_DATE(YYYYMM) + \"-\" + CURRENT_DATE(ww) + ORG_UNIT_CODE(...)"
        trackedEntityAttributeStore.updateOrInsert(
            TrackedEntityAttribute.builder().uid(ownerUid).pattern(pattern).build(),
        )
        val categoryCombo = CategoryCombo.builder().uid(categoryComboUid).build()
        d2.databaseAdapter()
            .insert(CategoryComboTableInfo.TABLE_INFO.name(), null, categoryCombo.toContentValues())
        val program = Program.builder().uid(programUid)
            .categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
            .access(Access.create(null, null, DataAccess.create(true, true))).build()
        koin.get<ProgramStore>().insert(program)
        val programTrackedEntityAttribute = ProgramTrackedEntityAttribute.builder()
            .uid("ptea_uid")
            .trackedEntityAttribute(ObjectWithUid.create(ownerUid))
            .program(ObjectWithUid.create(programUid))
            .build()
        koin.get<ProgramTrackedEntityAttributeStore>().insert(
            programTrackedEntityAttribute,
        )
        val organisationUnitProgramLink =
            OrganisationUnitProgramLink.builder().organisationUnit(organisationUnitUid)
                .program(programUid).build()
        koin.get<OrganisationUnitProgramLinkStore>().insert(
            organisationUnitProgramLink,
        )
        runBlocking {
            Mockito.`when`(
                trackedEntityAttributeReservedValueQueryCallFactory!!.create(
                    ArgumentMatchers.any(
                        TrackedEntityAttributeReservedValueQuery::class.java,
                    ),
                ),
            )
                .thenReturn(trackedEntityAttributeReservedValueCall)
        }
        handler.handleMany(trackedEntityAttributeReservedValues)
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun get_one_reserved_value() {
        Truth.assertThat(selectAll().size).isEqualTo(3)
        val value1 = d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(value1).isEqualTo("value1")
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun get_more_than_one_reserved_value() {
        val value1 = d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        val value2 = d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        val value3 = d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(value1).isEqualTo("value1")
        Truth.assertThat(value2).isEqualTo("value2")
        Truth.assertThat(value3).isEqualTo("value3")
    }

    //    @Test
    suspend fun sync_reserved_values_for_one_tracked_entity_attribute() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, 100)
        Truth.assertThat(selectAll().size).isEqualTo(100)
    }

    //    @Test
    suspend fun sync_20_reserved_values_for_one_tracked_entity_attribute() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 20)
        Truth.assertThat(selectAll().size).isEqualTo(20)
    }

    //    @Test
    suspend fun sync_100_reserved_values_when_not_number_of_values_to_reserve_is_specified() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, null)
        Truth.assertThat(selectAll().size).isEqualTo(100)
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun sync_pop_sync_again_and_have_99_reserved_values_when_not_number_of_values_to_reserve_is_specified() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, null)
        Truth.assertThat(selectAll().size).isEqualTo(100)
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(selectAll().size).isEqualTo(99)
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, null)
        Truth.assertThat(selectAll().size).isEqualTo(99)
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun fill_up_100_values_if_db_does_not_have_at_least_50_when_no_number_of_values_to_reserve_is_specified() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 50)
        Truth.assertThat(selectAll().size).isEqualTo(50)
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, null)
        Truth.assertThat(selectAll().size).isEqualTo(50)
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(selectAll().size).isEqualTo(49)
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, null)
        Truth.assertThat(selectAll().size).isEqualTo(100)
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun sync_pop_sync_again_and_have_99_reserved_values_if_less_than_existing_values_are_requested() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(ownerUid, 100)
        Truth.assertThat(selectAll().size).isEqualTo(100)
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(selectAll().size).isEqualTo(99)
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues(ownerUid, 20)
        Truth.assertThat(selectAll().size).isEqualTo(99)
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun reserve_100_new_values_and_take_one() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(selectAll().size).isEqualTo(99)
    }

    //    @Test
    @Throws(D2Error::class)
    suspend fun have_98_values_after_sync_and_take_two() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, organisationUnitUid)
        Truth.assertThat(selectAll().size).isEqualTo(98)
    }

    //    @Test
    @Throws(Exception::class)
    suspend fun sync_all_tracked_entity_instances() {
        Truth.assertThat(selectAll().size).isEqualTo(3)
        d2.metadataModule().blockingDownload()
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(null)

        /* 100 Reserved values by default * 2 TEA with generated property true on server = 200 */
        Truth.assertThat(
            selectAll().size,
        ).isEqualTo(200)
    }

    //    @Test
    suspend fun create_the_right_query_when_nothing_is_passed() {
        manager!!.blockingDownloadAllReservedValues(null)
        assertQueryIsCreatedRight(97)
    }

    //    @Test
    suspend fun create_the_right_query_when_only_an_attribute_is_passed() {
        manager!!.blockingDownloadReservedValues(ownerUid, null)
        assertQueryIsCreatedRight(97)
    }

    //    @Test
    suspend fun create_the_right_query_when_only_a_organisation_unit_is_passed() {
        manager!!.blockingDownloadAllReservedValues(null)
        assertQueryIsCreatedRight(97)
    }

    //    @Test
    suspend fun create_the_right_query_when_an_attribute_and_a_organisation_unit_is_passed() {
        manager!!.blockingDownloadReservedValues(ownerUid, null)
        assertQueryIsCreatedRight(97)
    }

    //    @Test
    suspend fun create_the_right_query_when_a_number_of_values_to_fill_up_is_passed() {
        manager!!.blockingDownloadReservedValues("", 20)
        assertQueryIsCreatedRight(17)
    }

    //    @Test
    suspend fun create_the_right_query_when_a_number_of_values_to_fill_up_and_an_attribute_is_passed() {
        manager!!.blockingDownloadReservedValues(ownerUid, 20)
        assertQueryIsCreatedRight(17)
    }

    //    @Test
    suspend fun create_the_right_query_when_a_number_of_values_to_fill_up_and_a_organisation_unit_is_passed() {
        manager!!.blockingDownloadAllReservedValues(20)
        assertQueryIsCreatedRight(17)
    }

    //    @Test
    suspend fun create_the_right_query_when_all_arguments_are_passed() {
        manager!!.blockingDownloadReservedValues(ownerUid, 20)
        assertQueryIsCreatedRight(17)
    }

    //    @Test (expected = D2Error.class)
    @Throws(D2Error::class)
    suspend fun return_d2_call_exception_if_no_valid_org_unit() {
        d2.trackedEntityModule().reservedValueManager()
            .blockingGetValue(ownerUid, "not_stored_organisation_unit_uid")
    }

    private suspend fun selectAll(): List<TrackedEntityAttributeReservedValue> {
        return store.selectAll()
    }

    private fun login() {
        try {
            if (!d2.userModule().isLogged().blockingGet()) {
                d2.userModule().logIn(username, password, url).blockingGet()
            }
        } catch (ignored: Exception) {
        }
    }

    /*
     * This method stopped working because QueryCallFactory mock instance differs from Dagger's injected one,
     * so the code is calling .create() on Dagger's instance and .verify() is trying to catch the call from Mockito's instace.
     */
    private suspend fun assertQueryIsCreatedRight(numberOfValuesExpected: Int) {
        Mockito.verify(trackedEntityAttributeReservedValueQueryCallFactory)!!.create(
            trackedEntityAttributeReservedValueQueryCaptor!!.capture(),
        )
        val query = trackedEntityAttributeReservedValueQueryCaptor.value
        Truth.assertThat(query.organisationUnit!!.uid()).isEqualTo(organisationUnit.uid())
        Truth.assertThat(query.numberToReserve)
            .isEqualTo(numberOfValuesExpected) // values expected - 3 that it had before.
        Truth.assertThat(query.trackedEntityAttributePattern).isEqualTo(pattern)
        Truth.assertThat(query.trackedEntityAttributeUid).isEqualTo(ownerUid)
    }
}
