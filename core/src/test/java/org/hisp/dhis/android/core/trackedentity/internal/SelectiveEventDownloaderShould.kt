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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventHandler
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.TrackerPostParentCallHelper
import org.hisp.dhis.android.core.tracker.exporter.EventsPage
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerEventsMode
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterNetworkHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class SelectiveEventDownloaderShould {

    private val parentCallHelper: TrackerPostParentCallHelper = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventStore: EventStore = mock()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val networkHandler: TrackerExporterNetworkHandler = mock()
    private val persistenceCallFactory: TrackedEntityInstancePersistenceCallFactory = mock()
    private val eventHandler: EventHandler = mock()
    private val relatives = RelationshipItemRelatives()

    private val oldDate = Date(1_000)
    private val newDate = Date(2_000)

    private lateinit var downloader: SelectiveEventDownloader

    @Before
    fun setUp() = runTest {
        whenever(parentCallHelper.useNewTrackerExporter()).doReturn(true)
        downloader = SelectiveEventDownloader(
            parentCallHelper,
            enrollmentStore,
            eventStore,
            databaseAdapter,
            networkHandler,
            CoroutineAPICallExecutorMock(),
            persistenceCallFactory,
            eventHandler,
        )
    }

    @Test
    fun download_events_with_the_tracked_entities_without_program_or_with_the_legacy_exporter() = runTest {
        assertThat(downloader.eventsMode(null)).isEqualTo(TrackerEventsMode.FULL)

        whenever(parentCallHelper.useNewTrackerExporter()).doReturn(false)
        assertThat(downloader.eventsMode(PROGRAM)).isEqualTo(TrackerEventsMode.FULL)
    }

    @Test
    fun download_events_apart_without_local_enrollments_of_the_program_and_index_otherwise() = runTest {
        whenever(enrollmentStore.countWhere(any())).doReturn(0)
        assertThat(downloader.eventsMode(PROGRAM)).isEqualTo(TrackerEventsMode.NONE)

        whenever(enrollmentStore.countWhere(any())).doReturn(3)
        assertThat(downloader.eventsMode(PROGRAM)).isEqualTo(TrackerEventsMode.INDEX)
    }

    @Test
    fun persist_the_tracked_entities_without_the_index_events() = runTest {
        givenLocalEvents()
        whenever(networkHandler.getEventsByUids(any(), any())).doReturn(emptyList())
        val tei = tei(enrollment("e1", event("a")))
        downloader.persist(listOf(tei), params(), relatives, query(TrackerEventsMode.INDEX))

        val captor = argumentCaptor<List<TrackedEntityInstance>>()
        verify(persistenceCallFactory).persistTEIs(captor.capture(), any(), any())
        assertThat(captor.firstValue.single().enrollments()!!.single().events()).isNull()
    }

    @Test
    fun download_new_and_modified_events_and_delete_synced_events_missing_from_the_index() = runTest {
        givenLocalEvents(
            event("unchanged", State.SYNCED, oldDate),
            event("modified", State.SYNCED, oldDate),
            event("deletedInServer", State.SYNCED, oldDate),
            event("localChanges", State.TO_UPDATE, oldDate),
            event("relationship", State.RELATIONSHIP, oldDate),
            event("missing", State.SYNCED, oldDate),
            event("toPost", State.TO_POST, oldDate),
        )
        val enrollment = enrollment(
            "e1",
            event("new", lastUpdated = newDate),
            event("unchanged", lastUpdated = oldDate),
            event("modified", lastUpdated = newDate),
            event("deletedInServer", lastUpdated = newDate, deleted = true),
            event("localChanges", lastUpdated = newDate),
            event("relationship", lastUpdated = oldDate),
        )
        whenever(networkHandler.getEventsByUids(any(), any())).doReturn(emptyList())

        downloader.persist(listOf(tei(enrollment)), params(), relatives, query(TrackerEventsMode.INDEX))

        verify(networkHandler).getEventsByUids(PROGRAM, listOf("new", "modified", "relationship"))
        val whereClause = argumentCaptor<String>()
        verify(databaseAdapter).delete(eq("Event"), whereClause.capture())
        assertThat(whereClause.firstValue).contains("'deletedInServer'")
        assertThat(whereClause.firstValue).contains("'missing'")
        assertThat(whereClause.firstValue).doesNotContain("'toPost'")
        assertThat(whereClause.firstValue).doesNotContain("'unchanged'")
    }

    @Test
    fun download_events_with_local_changes_when_overwriting() = runTest {
        givenLocalEvents(event("localChanges", State.TO_UPDATE, oldDate))
        whenever(networkHandler.getEventsByUids(any(), any())).doReturn(emptyList())

        val enrollment = enrollment("e1", event("localChanges", lastUpdated = oldDate))
        downloader.persist(listOf(tei(enrollment)), params(overwrite = true), relatives, query(TrackerEventsMode.INDEX))

        verify(networkHandler).getEventsByUids(PROGRAM, listOf("localChanges"))
    }

    @Test
    fun ignore_events_that_were_not_requested() = runTest {
        givenLocalEvents()
        whenever(networkHandler.getEventsByUids(any(), any())).doReturn(listOf(event("new"), event("other")))

        val enrollment = enrollment("e1", event("new", lastUpdated = newDate))
        downloader.persist(listOf(tei(enrollment)), params(), relatives, query(TrackerEventsMode.INDEX))

        assertThat(handledEvents().single().map { it.uid() }).containsExactly("new")
    }

    @Test
    fun ignore_deleted_enrollments_and_enrollments_of_other_programs() = runTest {
        val enrollments = listOf(
            enrollment("e1"),
            enrollment("deleted").toBuilder().deleted(true).build(),
            enrollment("other").toBuilder().program("otherProgram").build(),
        )
        whenever(networkHandler.getEventsOfEnrollments(any(), any(), any(), any())).doReturn(EventsPage(emptyList(), 1))

        downloader.persist(listOf(tei(*enrollments.toTypedArray())), params(), relatives, query(TrackerEventsMode.NONE))

        verify(networkHandler).getEventsOfEnrollments(PROGRAM, listOf("e1"), 1, PAGE_SIZE)
    }

    @Test
    fun download_the_events_of_a_batch_of_enrollments_in_one_page_when_they_fit() = runTest {
        val events = listOf(event("a", enrollment = "e1"), event("b", enrollment = "e2"), event("c", enrollment = "x"))
        whenever(networkHandler.getEventsOfEnrollments(any(), any(), any(), any())).doReturn(EventsPage(events, 1))

        downloader.persist(
            listOf(tei(enrollment("e1")), tei(enrollment("e2"))),
            params(),
            relatives,
            query(TrackerEventsMode.NONE),
        )

        verify(networkHandler).getEventsOfEnrollments(PROGRAM, listOf("e1", "e2"), 1, PAGE_SIZE)
        verify(networkHandler, never()).getEventsOfEnrollments(any(), any(), eq(2), any())
        assertThat(handledEvents().single().map { it.uid() }).containsExactly("a", "b")
    }

    @Test
    fun stop_at_the_last_page_even_if_it_is_full() = runTest {
        whenever(networkHandler.getEventsOfEnrollments(any(), any(), any(), any()))
            .doReturn(EventsPage(fullPage("e1"), 1))

        downloader.persist(listOf(tei(enrollment("e1"))), params(), relatives, query(TrackerEventsMode.NONE))

        verify(networkHandler, times(1)).getEventsOfEnrollments(any(), any(), any(), any())
    }

    @Test
    fun page_through_the_batch_when_it_has_few_pages() = runTest {
        val batch = listOf("e1", "e2")
        whenever(networkHandler.getEventsOfEnrollments(PROGRAM, batch, 1, PAGE_SIZE))
            .doReturn(EventsPage(fullPage("e1"), 2))
        whenever(networkHandler.getEventsOfEnrollments(PROGRAM, batch, 2, PAGE_SIZE))
            .doReturn(EventsPage(listOf(event("last", enrollment = "e2")), null))

        downloader.persist(batch.map { tei(enrollment(it)) }, params(), relatives, query(TrackerEventsMode.NONE))

        verify(networkHandler).getEventsOfEnrollments(PROGRAM, batch, 2, PAGE_SIZE)
        verify(networkHandler, never()).getEventsOfEnrollments(PROGRAM, listOf("e1"), 1, PAGE_SIZE)
        assertThat(handledEvents(times = 2).flatten()).hasSize(PAGE_SIZE + 1)
    }

    @Test
    fun download_each_enrollment_apart_when_the_batch_has_many_pages() = runTest {
        val batch = listOf("e1", "e2")
        whenever(networkHandler.getEventsOfEnrollments(PROGRAM, batch, 1, PAGE_SIZE))
            .doReturn(EventsPage(fullPage("e1"), 10))
        whenever(networkHandler.getEventsOfEnrollments(PROGRAM, listOf("e1"), 1, PAGE_SIZE))
            .doReturn(EventsPage(fullPage("e1"), 2))
        whenever(networkHandler.getEventsOfEnrollments(PROGRAM, listOf("e1"), 2, PAGE_SIZE))
            .doReturn(EventsPage(emptyList(), null))
        whenever(networkHandler.getEventsOfEnrollments(PROGRAM, listOf("e2"), 1, PAGE_SIZE))
            .doReturn(EventsPage(listOf(event("last", enrollment = "e2")), 1))

        downloader.persist(batch.map { tei(enrollment(it)) }, params(), relatives, query(TrackerEventsMode.NONE))

        verify(networkHandler, never()).getEventsOfEnrollments(PROGRAM, batch, 2, PAGE_SIZE)
        verify(networkHandler).getEventsOfEnrollments(PROGRAM, listOf("e1"), 2, PAGE_SIZE)
        verify(networkHandler).getEventsOfEnrollments(PROGRAM, listOf("e2"), 1, PAGE_SIZE)
    }

    private suspend fun givenLocalEvents(vararg events: Event) {
        whenever(eventStore.selectWhere(any<String>())).doReturn(events.toList())
    }

    private suspend fun handledEvents(times: Int = 1): List<Collection<Event>> {
        val captor = argumentCaptor<Collection<Event>>()
        verify(eventHandler, times(times)).handleMany(captor.capture(), any(), any())
        return captor.allValues
    }

    private fun fullPage(enrollment: String): List<Event> {
        return List(PAGE_SIZE) { event("$enrollment-$it", enrollment = enrollment) }
    }

    private fun event(
        uid: String,
        syncState: State? = null,
        lastUpdated: Date? = null,
        deleted: Boolean? = null,
        enrollment: String = "e1",
    ): Event {
        return Event.builder()
            .uid(uid)
            .enrollment(enrollment)
            .syncState(syncState)
            .lastUpdated(lastUpdated)
            .deleted(deleted)
            .build()
    }

    private fun enrollment(uid: String, vararg events: Event): Enrollment {
        return Enrollment.builder()
            .uid(uid)
            .program(PROGRAM)
            .attributeOptionCombo("aoc")
            .events(events.map { it.toBuilder().enrollment(uid).build() })
            .build()
    }

    private fun tei(vararg enrollments: Enrollment): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .uid("tei-${enrollments.first().uid()}")
            .enrollments(enrollments.toList())
            .build()
    }

    private fun params(overwrite: Boolean = false): IdentifiableDataHandlerParams {
        return IdentifiableDataHandlerParams(hasAllAttributes = true, overwrite = overwrite, asRelationship = false)
    }

    private fun query(eventsMode: TrackerEventsMode): TrackerAPIQuery {
        return TrackerAPIQuery(
            commonParams = TrackerQueryCommonParams(
                uids = emptyList(),
                programs = listOf(PROGRAM),
                program = PROGRAM,
                startDate = null,
                hasLimitByOrgUnit = false,
                ouMode = OrganisationUnitMode.DESCENDANTS,
                orgUnitsBeforeDivision = emptyList(),
                limit = 50,
            ),
            eventsMode = eventsMode,
        )
    }

    companion object {
        private const val PROGRAM = "program"
        private const val PAGE_SIZE = 1000
    }
}
