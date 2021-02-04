package org.hisp.dhis.android.core.event.search

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.event.EventFilterCollectionRepository
import org.junit.Before
import org.junit.Test

class EventQueryCollectionRepositoryShould {

    private val adapter: EventCollectionRepositoryAdapter = mock()
    private val filterRepository: EventFilterCollectionRepository = mock()

    private lateinit var queryRepository: EventQueryCollectionRepository

    @Before
    fun setUp() {
        val emptyScope = EventQueryRepositoryScope.empty()
        queryRepository = EventQueryCollectionRepository(adapter, filterRepository, emptyScope)
    }

    @Test
    fun `Should create scope with event date`() {
        val startDate = DateUtils.DATE_FORMAT.parse("2021-01-20T00:00:00.000")
        val endDate = DateUtils.DATE_FORMAT.parse("2021-01-29T00:00:00.000")

        val scope = queryRepository
            .byEventDate().after(startDate)
            .byEventDate().before(endDate)
            .scope

        assertThat(scope.eventDate()?.startDate()).isEqualTo(startDate)
        assertThat(scope.eventDate()?.endDate()).isEqualTo(endDate)
    }

    @Test
    fun `Should create scope with relative event date`() {
        val scope = queryRepository
            .byEventDate().inPeriod(RelativePeriod.LAST_3_DAYS)
            .scope

        assertThat(scope.eventDate()?.period()).isEqualTo(RelativePeriod.LAST_3_DAYS)
        assertThat(scope.eventDate()?.type()).isEqualTo(DatePeriodType.RELATIVE)
    }

    @Test
    fun `Should concat sort orders`() {
        val scope = queryRepository
            .orderByEventDate().eq(RepositoryScope.OrderByDirection.ASC)
            .orderByLastUpdated().eq(RepositoryScope.OrderByDirection.DESC)
            .scope

        assertThat(scope.order().size).isEqualTo(2)
        assertThat(scope.order().first().column()).isEqualTo(EventQueryScopeOrderColumn.EVENT_DATE)
        assertThat(scope.order().first().direction()).isEqualTo(RepositoryScope.OrderByDirection.ASC)
        assertThat(scope.order().last().column()).isEqualTo(EventQueryScopeOrderColumn.LAST_UPDATED)
        assertThat(scope.order().last().direction()).isEqualTo(RepositoryScope.OrderByDirection.DESC)
    }
}
