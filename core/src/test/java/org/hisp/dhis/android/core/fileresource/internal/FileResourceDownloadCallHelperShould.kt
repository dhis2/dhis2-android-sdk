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
package org.hisp.dhis.android.core.fileresource.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboCategoryOptionLinkStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.dataset.internal.DataSetElementStore
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.icon.internal.CustomIconStore
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
internal class FileResourceDownloadCallHelperShould {

    private val dataElementStore: DataElementStore = mock()
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore = mock()
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore = mock()
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore = mock()
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventStore: EventStore = mock()
    private val dataSetElementStore: DataSetElementStore = mock()
    private val dataValueStore: DataValueStore = mock()
    private val customIconStore: CustomIconStore = mock()
    private val categoryOptionComboStore: CategoryOptionComboStore = mock()
    private val categoryOptionComboCategoryOptionLinkStore: CategoryOptionComboCategoryOptionLinkStore = mock()
    private val dhisVersionManager: DHISVersionManagerImpl = mock()

    private lateinit var helper: FileResourceDownloadCallHelper

    @Before
    fun setUp() = runTest {
        whenever(dhisVersionManager.isGreaterOrEqualThanInternal(any())).doReturn(true)
        whenever(trackedEntityAttributeStore.selectWhere(any())).doReturn(listOf(imageAttribute))
        whenever(trackedEntityInstanceStore.selectUidsWhere(any())).doReturn(listOf(TEI_UID))
        whenever(trackedEntityAttributeValueStore.selectWhere(any())).doReturn(listOf(attributeValue))

        helper = FileResourceDownloadCallHelper(
            dataElementStore,
            trackedEntityAttributeValueStore,
            trackedEntityAttributeStore,
            trackedEntityDataValueStore,
            trackedEntityInstanceStore,
            programTrackedEntityAttributeStore,
            enrollmentStore,
            eventStore,
            dataSetElementStore,
            dataValueStore,
            customIconStore,
            categoryOptionComboStore,
            categoryOptionComboCategoryOptionLinkStore,
            dhisVersionManager,
        )
    }

    @Test
    fun not_resolve_a_program_for_tracked_entity_type_attributes() = runTest {
        givenProgramsForAttribute()
        givenEnrolledPrograms(PROGRAM_1)

        val values = helper.getMissingTrackerAttributeValues(paramsFor(programUid = PROGRAM_1), emptyList())

        assertThat(values.single().program).isNull()
    }

    @Test
    fun use_the_program_the_download_is_scoped_to_when_the_attribute_is_assigned_to_it() = runTest {
        givenProgramsForAttribute(PROGRAM_1, PROGRAM_2)
        givenEnrolledPrograms(PROGRAM_1, PROGRAM_2)

        // The enrollment inference would pick PROGRAM_1, so this only passes if the single program acts as context.
        val values = helper.getMissingTrackerAttributeValues(paramsFor(programUid = PROGRAM_2), emptyList())

        assertThat(values.single().program).isEqualTo(PROGRAM_2)
    }

    @Test
    fun fall_back_to_an_enrolled_program_when_there_is_no_context_program() = runTest {
        givenProgramsForAttribute(PROGRAM_1, PROGRAM_2)
        givenEnrolledPrograms(PROGRAM_2)

        val values = helper.getMissingTrackerAttributeValues(paramsFor(programUid = null), emptyList())

        assertThat(values.single().program).isEqualTo(PROGRAM_2)
    }

    @Test
    fun fall_back_to_an_enrolled_program_when_the_context_program_does_not_have_the_attribute() = runTest {
        givenProgramsForAttribute(PROGRAM_2)
        givenEnrolledPrograms(PROGRAM_1, PROGRAM_2)

        val values = helper.getMissingTrackerAttributeValues(paramsFor(programUid = PROGRAM_1), emptyList())

        assertThat(values.single().program).isEqualTo(PROGRAM_2)
    }

    @Test
    fun not_use_several_programs_as_the_context_program() = runTest {
        givenProgramsForAttribute(PROGRAM_1, PROGRAM_2)
        givenEnrolledPrograms(PROGRAM_2)

        val params = FileResourceDownloadParams(programUids = listOf(PROGRAM_1, PROGRAM_2))
        val values = helper.getMissingTrackerAttributeValues(params, emptyList())

        assertThat(values.single().program).isEqualTo(PROGRAM_2)
    }

    @Test
    fun fall_back_to_any_program_with_the_attribute_when_the_tracked_entity_is_not_enrolled() = runTest {
        givenProgramsForAttribute(PROGRAM_2)
        givenEnrolledPrograms()

        val values = helper.getMissingTrackerAttributeValues(paramsFor(programUid = null), emptyList())

        assertThat(values.single().program).isEqualTo(PROGRAM_2)
    }

    private suspend fun givenProgramsForAttribute(vararg programUids: String) {
        val links = programUids.map { programUid ->
            ProgramTrackedEntityAttribute.builder()
                .uid("$programUid-$ATTRIBUTE_UID")
                .program(ObjectWithUid.create(programUid))
                .trackedEntityAttribute(ObjectWithUid.create(ATTRIBUTE_UID))
                .build()
        }
        whenever(programTrackedEntityAttributeStore.selectWhere(any())).doReturn(links)
    }

    private suspend fun givenEnrolledPrograms(vararg programUids: String) {
        val enrollments = programUids.map { programUid ->
            mock<Enrollment> {
                on { trackedEntityInstance() } doReturn TEI_UID
                on { program() } doReturn programUid
            }
        }
        whenever(enrollmentStore.selectWhere(any())).doReturn(enrollments)
    }

    private fun paramsFor(programUid: String?) = FileResourceDownloadParams(
        trackedEntityUids = listOf(TEI_UID),
        programUids = listOfNotNull(programUid),
    )

    private val imageAttribute = TrackedEntityAttribute.builder()
        .uid(ATTRIBUTE_UID)
        .valueType(ValueType.IMAGE)
        .build()

    private val attributeValue = TrackedEntityAttributeValue.builder()
        .trackedEntityAttribute(ATTRIBUTE_UID)
        .trackedEntityInstance(TEI_UID)
        .value("fileResourceUid")
        .build()

    companion object {
        private const val ATTRIBUTE_UID = "attributeUid"
        private const val TEI_UID = "teiUid"
        private const val PROGRAM_1 = "program1"
        private const val PROGRAM_2 = "program2"
    }
}
