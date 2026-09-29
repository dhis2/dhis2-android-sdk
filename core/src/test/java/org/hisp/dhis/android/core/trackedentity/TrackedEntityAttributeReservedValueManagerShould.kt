/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitProgramLinkStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.settings.GeneralSettingObjectRepository
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.trackedentity.internal.ReservedValueSettingStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueEndpointCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueQuery
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackedEntityAttributeReservedValueManagerShould {

    private val store: TrackedEntityAttributeReservedValueStore = mock()
    private val organisationUnitStore: OrganisationUnitStore = mock()
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore = mock()
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore = mock()
    private val organisationUnitProgramLinkStore: OrganisationUnitProgramLinkStore = mock()
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val generalSettingObjectRepository: GeneralSettingObjectRepository = mock()
    private val reservedValueSettingStore: ReservedValueSettingStore = mock()
    private val reservedValueQueryCallFactory: TrackedEntityAttributeReservedValueEndpointCallFactory = mock()

    private val ouDependentPattern = "ORG_UNIT_CODE() + SEQUENTIAL(###)"
    private val plainPattern = "SEQUENTIAL(###)"

    private val ouDependentAttribute = attribute("ouAttribute", ouDependentPattern)
    private val plainAttribute = attribute("plainAttribute", plainPattern)

    private val orgUnit1 = OrganisationUnit.builder().uid("ou1").code("OU1").build()
    private val orgUnit2 = OrganisationUnit.builder().uid("ou2").code("OU2").build()
    private val orgUnitWithoutCode = OrganisationUnit.builder().uid("ou3").build()

    private lateinit var manager: TrackedEntityAttributeReservedValueManager

    @Before
    fun setUp() = runTest {
        manager = TrackedEntityAttributeReservedValueManager(
            store,
            organisationUnitStore,
            trackedEntityAttributeStore,
            programTrackedEntityAttributeStore,
            organisationUnitProgramLinkStore,
            userOrganisationUnitLinkStore,
            generalSettingObjectRepository,
            reservedValueSettingStore,
            reservedValueQueryCallFactory,
        )

        whenever(trackedEntityAttributeStore.selectByUid(ouDependentAttribute.uid())) doReturn ouDependentAttribute
        whenever(trackedEntityAttributeStore.selectByUid(plainAttribute.uid())) doReturn plainAttribute
        whenever(organisationUnitStore.selectByUid(orgUnit1.uid())) doReturn orgUnit1
    }

    @Test
    fun `Should return value popped from store`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 100)
        whenever(store.popOne(plainAttribute.uid(), null)) doReturn reservedValue("value1")

        assertThat(manager.suspendGetValue(plainAttribute.uid(), orgUnit1.uid())).isEqualTo("value1")
        assertThat(manager.blockingGetValue(plainAttribute.uid(), orgUnit1.uid())).isEqualTo("value1")
        assertThat(manager.rxGetValue(plainAttribute.uid(), orgUnit1.uid()).blockingGet()).isEqualTo("value1")
        @Suppress("DEPRECATION")
        assertThat(manager.getValue(plainAttribute.uid(), orgUnit1.uid()).blockingGet()).isEqualTo("value1")
    }

    @Test
    fun `Should pop value with org unit when pattern is ORG_UNIT_CODE dependent`() = runTest {
        givenRemainingValues(ouDependentAttribute.uid(), orgUnit1.uid(), 100)
        whenever(store.popOne(ouDependentAttribute.uid(), orgUnit1.uid())) doReturn reservedValue("OU1-001")

        val value = manager.suspendGetValue(ouDependentAttribute.uid(), orgUnit1.uid())

        assertThat(value).isEqualTo("OU1-001")
        verify(store).count(ouDependentAttribute.uid(), orgUnit1.uid(), ouDependentPattern)
        verify(store).popOne(ouDependentAttribute.uid(), orgUnit1.uid())
    }

    @Test
    fun `Should pop value without org unit when pattern is not org unit dependent`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 100)
        whenever(store.popOne(plainAttribute.uid(), null)) doReturn reservedValue("001")

        manager.suspendGetValue(plainAttribute.uid(), orgUnit1.uid())

        verify(store).count(plainAttribute.uid(), null, plainPattern)
        verify(store).popOne(plainAttribute.uid(), null)
    }

    @Test
    fun `Should pop value without org unit when pattern is null`() = runTest {
        val nullPatternAttribute = attribute("nullPattern", null)
        whenever(trackedEntityAttributeStore.selectByUid(nullPatternAttribute.uid())) doReturn nullPatternAttribute
        givenRemainingValues(nullPatternAttribute.uid(), null, 100)
        whenever(store.popOne(nullPatternAttribute.uid(), null)) doReturn reservedValue("001")

        manager.suspendGetValue(nullPatternAttribute.uid(), orgUnit1.uid())

        verify(store).popOne(nullPatternAttribute.uid(), null)
    }

    @Test
    fun `Should throw NO_RESERVED_VALUES when store is empty`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 100)
        whenever(store.popOne(plainAttribute.uid(), null)) doReturn null

        try {
            manager.suspendGetValue(plainAttribute.uid(), orgUnit1.uid())
            fail("D2Error expected")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.NO_RESERVED_VALUES)
        }
    }

    @Test
    fun `Should download values when remaining are below threshold`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 10)
        whenever(store.popOne(plainAttribute.uid(), null)) doReturn reservedValue("001")

        manager.suspendGetValue(plainAttribute.uid(), orgUnit1.uid())

        verify(store).deleteExpired(any())
        verify(reservedValueQueryCallFactory).create(
            TrackedEntityAttributeReservedValueQuery(plainAttribute.uid(), 90, orgUnit1, plainPattern, false),
        )
    }

    @Test
    fun `Should not download when remaining are above threshold`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 50)
        whenever(store.popOne(plainAttribute.uid(), null)) doReturn reservedValue("001")

        manager.suspendGetValue(plainAttribute.uid(), orgUnit1.uid())

        verify(reservedValueQueryCallFactory, never()).create(any())
        verify(store, never()).deleteIfOutdatedPattern(any(), any())
    }

    @Test
    fun `Should use explicit numberOfValuesToFillUp as threshold and persist it`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 15)

        manager.flowDownloadReservedValues(plainAttribute.uid(), 20).toList()

        verify(reservedValueSettingStore).updateOrInsert(
            ReservedValueSetting.builder().uid(plainAttribute.uid()).numberOfValuesToReserve(20).build(),
        )
        verify(reservedValueQueryCallFactory).create(
            TrackedEntityAttributeReservedValueQuery(plainAttribute.uid(), 5, null, plainPattern, true),
        )
    }

    @Test
    fun `Should resolve fillUpTo from attribute setting`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)
        whenever(reservedValueSettingStore.selectByUid(plainAttribute.uid())) doReturn
            ReservedValueSetting.builder().uid(plainAttribute.uid()).numberOfValuesToReserve(30).build()
        whenever(generalSettingObjectRepository.getInternal()) doReturn generalSettings(40)

        manager.flowDownloadReservedValues(plainAttribute.uid(), null).toList()

        verify(reservedValueQueryCallFactory).create(
            TrackedEntityAttributeReservedValueQuery(plainAttribute.uid(), 30, null, plainPattern, true),
        )
    }

    @Test
    fun `Should resolve fillUpTo from general settings`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)
        whenever(generalSettingObjectRepository.getInternal()) doReturn generalSettings(40)

        manager.flowDownloadReservedValues(plainAttribute.uid(), null).toList()

        verify(reservedValueQueryCallFactory).create(
            TrackedEntityAttributeReservedValueQuery(plainAttribute.uid(), 40, null, plainPattern, true),
        )
    }

    @Test
    fun `Should resolve fillUpTo to default value`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)

        manager.flowDownloadReservedValues(plainAttribute.uid(), null).toList()

        verify(reservedValueQueryCallFactory).create(
            TrackedEntityAttributeReservedValueQuery(plainAttribute.uid(), 100, null, plainPattern, true),
        )
        verify(reservedValueSettingStore, never()).updateOrInsert(any<ReservedValueSetting>())
    }

    @Test
    fun `Should delete outdated pattern values after download`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)

        manager.flowDownloadReservedValues(plainAttribute.uid(), null).toList()

        verify(store).deleteIfOutdatedPattern(plainAttribute.uid(), plainPattern)
    }

    @Test
    fun `Should not delete outdated pattern values if pattern is null`() = runTest {
        val nullPatternAttribute = attribute("nullPattern", null)
        whenever(trackedEntityAttributeStore.selectByUid(nullPatternAttribute.uid())) doReturn nullPatternAttribute
        givenRemainingValues(nullPatternAttribute.uid(), null, 0)

        manager.flowDownloadReservedValues(nullPatternAttribute.uid(), null).toList()

        verify(reservedValueQueryCallFactory).create(any())
        verify(store, never()).deleteIfOutdatedPattern(any(), any())
    }

    @Test
    fun `Should swallow exceptions in download`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)
        whenever(reservedValueQueryCallFactory.create(any())) doThrow RuntimeException("Network error")

        val progress = manager.flowDownloadReservedValues(plainAttribute.uid(), null).toList()

        assertThat(progress).hasSize(1)
        verify(store, never()).deleteIfOutdatedPattern(any(), any())
    }

    @Test
    fun `Should emit one progress per org unit with code for org-unit-dependent attribute`() = runTest {
        givenLinkedOrgUnits(
            linked = listOf("ou1", "ou2", "ou3"),
            capture = listOf("ou1", "ou2", "ou3"),
            selected = listOf(orgUnit1, orgUnit2, orgUnitWithoutCode),
        )
        givenRemainingValues(ouDependentAttribute.uid(), orgUnit1.uid(), 0)
        givenRemainingValues(ouDependentAttribute.uid(), orgUnit2.uid(), 0)

        val progress = manager.flowDownloadReservedValues(ouDependentAttribute.uid(), null).toList()

        assertThat(progress).hasSize(2)
        assertThat(progress.last().doneCalls()).hasSize(2)
        listOf(orgUnit1, orgUnit2).forEach { orgUnit ->
            verify(reservedValueQueryCallFactory).create(
                TrackedEntityAttributeReservedValueQuery(
                    ouDependentAttribute.uid(),
                    100,
                    orgUnit,
                    ouDependentPattern,
                    true,
                ),
            )
        }
    }

    @Test
    fun `Should only keep capture org units linked to attribute programs`() = runTest {
        givenLinkedOrgUnits(
            linked = listOf("ou1", "ou2"),
            capture = listOf("ou2", "ou3"),
            selected = listOf(orgUnit2),
        )
        givenRemainingValues(ouDependentAttribute.uid(), orgUnit2.uid(), 0)

        manager.flowDownloadReservedValues(ouDependentAttribute.uid(), null).toList()

        val whereCaptor = argumentCaptor<String>()
        verify(organisationUnitStore).selectWhere(whereCaptor.capture())
        assertThat(whereCaptor.firstValue).contains("'ou2'")
        assertThat(whereCaptor.firstValue).doesNotContain("'ou1'")
        assertThat(whereCaptor.firstValue).doesNotContain("'ou3'")
    }

    @Test
    fun `Should emit single progress for non org-unit-dependent attribute`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)

        val progress = manager.flowDownloadReservedValues(plainAttribute.uid(), null).toList()

        assertThat(progress).hasSize(1)
        verify(organisationUnitStore, never()).selectWhere(any())
    }

    @Test
    fun `Should keep emitting progress when download fails for an org unit`() = runTest {
        givenLinkedOrgUnits(
            linked = listOf("ou1", "ou2"),
            capture = listOf("ou1", "ou2"),
            selected = listOf(orgUnit1, orgUnit2),
        )
        whenever(store.deleteExpired(any())) doThrow RuntimeException("Database error")

        val progress = manager.flowDownloadReservedValues(ouDependentAttribute.uid(), null).toList()

        assertThat(progress).hasSize(2)
        verify(reservedValueQueryCallFactory, never()).create(any())
    }

    @Test
    fun `Should download values for all generated attributes`() = runTest {
        whenever(trackedEntityAttributeStore.selectWhere(any<String>())) doReturn listOf(plainAttribute)
        givenRemainingValues(plainAttribute.uid(), null, 0)

        val progress = manager.flowDownloadAllReservedValues(null).toList()
        manager.blockingDownloadAllReservedValues(null)
        manager.rxDownloadAllReservedValues(null).toList().blockingGet()
        @Suppress("DEPRECATION")
        manager.downloadAllReservedValues(null).toList().blockingGet()

        assertThat(progress).hasSize(1)
        val whereCaptor = argumentCaptor<String>()
        verify(trackedEntityAttributeStore, times(4)).selectWhere(whereCaptor.capture())
        assertThat(whereCaptor.firstValue).contains("generated = 1")
        verify(reservedValueQueryCallFactory, times(4)).create(any())
    }

    @Test
    fun `Should expose download through blocking and rx variants`() = runTest {
        givenRemainingValues(plainAttribute.uid(), null, 0)

        manager.blockingDownloadReservedValues(plainAttribute.uid(), null)
        val rxProgress = manager.rxDownloadReservedValues(plainAttribute.uid(), null).toList().blockingGet()
        @Suppress("DEPRECATION")
        val deprecatedProgress = manager.downloadReservedValues(plainAttribute.uid(), null).toList().blockingGet()

        assertThat(rxProgress).hasSize(1)
        assertThat(deprecatedProgress).hasSize(1)
        verify(reservedValueQueryCallFactory, times(3)).create(any())
    }

    @Test
    fun `Should count values`() = runTest {
        whenever(store.count(plainAttribute.uid(), orgUnit1.uid(), null)) doReturn 7

        assertThat(manager.countInternal(plainAttribute.uid(), orgUnit1.uid())).isEqualTo(7)
        assertThat(manager.suspendCount(plainAttribute.uid(), orgUnit1.uid())).isEqualTo(7)
        assertThat(manager.blockingCount(plainAttribute.uid(), orgUnit1.uid())).isEqualTo(7)
        assertThat(manager.rxCount(plainAttribute.uid(), orgUnit1.uid()).blockingGet()).isEqualTo(7)
        @Suppress("DEPRECATION")
        assertThat(manager.count(plainAttribute.uid(), orgUnit1.uid()).blockingGet()).isEqualTo(7)
    }

    @Test
    fun `Should build summaries per org unit for dependent attributes and a single one otherwise`() = runTest {
        whenever(trackedEntityAttributeStore.selectWhere(any(), anyOrNull())) doReturn
            listOf(ouDependentAttribute, plainAttribute)
        givenLinkedOrgUnits(
            linked = listOf("ou1", "ou2"),
            capture = listOf("ou1", "ou2"),
            selected = listOf(orgUnit1, orgUnit2),
        )
        whenever(store.count(ouDependentAttribute.uid(), orgUnit1.uid(), null)) doReturn 3
        whenever(store.count(ouDependentAttribute.uid(), orgUnit2.uid(), null)) doReturn 4
        whenever(store.count(plainAttribute.uid(), null, null)) doReturn 5
        whenever(generalSettingObjectRepository.getInternal()) doReturn generalSettings(40)

        val summaries = manager.suspendGetReservedValueSummaries()

        assertThat(summaries).containsExactly(
            summary(ouDependentAttribute, orgUnit1, 3, 40),
            summary(ouDependentAttribute, orgUnit2, 4, 40),
            summary(plainAttribute, null, 5, 40),
        ).inOrder()
        assertThat(manager.blockingGetReservedValueSummaries()).isEqualTo(summaries)
        assertThat(manager.rxGetReservedValueSummaries().blockingGet()).isEqualTo(summaries)
        @Suppress("DEPRECATION")
        assertThat(manager.getReservedValueSummaries().blockingGet()).isEqualTo(summaries)

        val orderCaptor = argumentCaptor<String>()
        verify(trackedEntityAttributeStore, times(4)).selectWhere(any(), orderCaptor.capture())
        assertThat(orderCaptor.firstValue).contains("displayName ASC")
    }

    private suspend fun givenRemainingValues(attributeUid: String, orgUnitUid: String?, count: Int) {
        whenever(store.count(eq(attributeUid), eq(orgUnitUid), anyOrNull())) doReturn count
    }

    private suspend fun givenLinkedOrgUnits(
        linked: List<String>,
        capture: List<String>,
        selected: List<OrganisationUnit>,
    ) {
        whenever(programTrackedEntityAttributeStore.selectStringColumnsWhereClause(any(), any())) doReturn
            listOf("program1")
        whenever(organisationUnitProgramLinkStore.selectStringColumnsWhereClause(any(), any())) doReturn linked
        whenever(
            userOrganisationUnitLinkStore.queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE),
        ) doReturn capture
        whenever(organisationUnitStore.selectWhere(any<String>())) doReturn selected
    }

    private fun attribute(uid: String, pattern: String?): TrackedEntityAttribute =
        TrackedEntityAttribute.builder().uid(uid).pattern(pattern).generated(true).build()

    private fun reservedValue(value: String): TrackedEntityAttributeReservedValue =
        TrackedEntityAttributeReservedValue.builder()
            .ownerObject("TRACKEDENTITYATTRIBUTE")
            .ownerUid("attribute")
            .value(value)
            .build()

    private fun generalSettings(reservedValues: Int): GeneralSettings =
        GeneralSettings.builder().encryptDB(false).reservedValues(reservedValues).build()

    private fun summary(
        attribute: TrackedEntityAttribute,
        orgUnit: OrganisationUnit?,
        count: Int,
        fillUpTo: Int,
    ): ReservedValueSummary = ReservedValueSummary.builder()
        .trackedEntityAttribute(attribute)
        .organisationUnit(orgUnit)
        .count(count)
        .numberOfValuesToFillUp(fillUpTo)
        .build()
}
