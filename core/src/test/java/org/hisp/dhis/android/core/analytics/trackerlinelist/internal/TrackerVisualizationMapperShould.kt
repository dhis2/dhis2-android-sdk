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
package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.hisp.dhis.android.core.analytics.trackerlinelist.DataFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.DateFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.EnumFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimension
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackerVisualizationMapperShould {

    private val organisationUniLevelStore: OrganisationUnitLevelStore = mock()

    private val mapper = TrackerVisualizationMapper(organisationUniLevelStore)

    @Test
    fun should_map_data_filters() {
        val item = TrackerVisualizationDimension.builder()
            .filter("GT:6:ILIKE:ar:NE:4:NE:NV")
            .build()

        val dataFilters = mapper.mapDataFilters(item)

        assertThat(dataFilters).containsExactly(
            DataFilter.GreaterThan("6"),
            DataFilter.Like("ar", ignoreCase = true),
            DataFilter.NotEqualTo("4", ignoreCase = false),
            DataFilter.IsNull(false),
        )
    }

    @Test
    fun should_map_date_filters() {
        val item = TrackerVisualizationDimension.builder()
            .items(
                listOf(
                    ObjectWithUid.create("202403"),
                    ObjectWithUid.create("2024-03-05_2024-04-01"),
                ),
            )
            .build()

        val dateFilters = mapper.mapDateFilters(item)

        assertThat(dateFilters).containsExactly(
            DateFilter.Absolute("202403"),
            DateFilter.Range("2024-03-05", "2024-04-01"),
        )
    }

    @Test
    fun should_map_program_status() {
        val item = TrackerVisualizationDimension.builder()
            .dimensionType("dataX")
            .dimension("programStatus")
            .items(
                listOf(
                    ObjectWithUid.create(EnrollmentStatus.ACTIVE.name),
                    ObjectWithUid.create(EnrollmentStatus.CANCELLED.name),
                ),
            )
            .build()

        val programStatus = mapper.mapDataX(item)

        assertThat(programStatus).isEqualTo(
            TrackerLineListItem.ProgramStatusItem(
                null,
                listOf(
                    EnumFilter.In(
                        listOf(
                            EnrollmentStatus.ACTIVE,
                            EnrollmentStatus.CANCELLED,
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun should_map_categories() {
        val categoryType = "CATEGORY"
        val categoryId = "categoryId"
        val categoryOption1 = "categoryOption1"
        val categoryOption2 = "categoryOption2"

        val item = TrackerVisualizationDimension.builder()
            .dimensionType(categoryType)
            .dimension(categoryId)
            .items(
                listOf(
                    ObjectWithUid.create(categoryOption1),
                    ObjectWithUid.create(categoryOption2),
                ),
            )
            .build()
        val category = mapper.mapCategory(item)

        assertThat(category).isEqualTo(
            TrackerLineListItem.Category(
                categoryId,
                listOf(
                    DataFilter.In(
                        listOf(
                            categoryOption1,
                            categoryOption2,
                        ),
                    ),
                ),
            ),
        )
    }
}
