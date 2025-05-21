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
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListSortingItem
import org.hisp.dhis.android.core.common.SortingDirection
import org.hisp.dhis.android.core.visualization.TrackerVisualizationSorting
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackerLineListSortingMapperShould {

    private val mapper = TrackerLineListSortingMapper

    @Test
    fun should_map_program_data_elements() {
        val dataElementItem = TrackerLineListItem.ProgramDataElement("IpHINAT79UW", "cejWyOfXge6")

        assertThat(matchesItem(dataElementItem, "cejWyOfXge6.IpHINAT79UW")).isTrue()
        assertThat(matchesItem(dataElementItem, "ur1Edk5Oe2n.cejWyOfXge6.IpHINAT79UW")).isTrue()
        assertThat(matchesItem(dataElementItem, "aejWyOfXge6")).isFalse()
    }

    @Test
    fun should_map_program_status() {
        val programStatusItem = TrackerLineListItem.ProgramStatusItem("IpHINAT79UW")

        assertThat(matchesItem(programStatusItem, "programStatus")).isTrue()
        assertThat(matchesItem(programStatusItem, "IpHINAT79UW.programstatus")).isTrue()
        assertThat(matchesItem(programStatusItem, "aejWyOfXge6")).isFalse()
    }

    @Test
    fun should_map_orgunit() {
        val orgunitItem = TrackerLineListItem.OrganisationUnitItem("IpHINAT79UW")

        assertThat(matchesItem(orgunitItem, "ou")).isTrue()
        assertThat(matchesItem(orgunitItem, "IpHINAT79UW.ouname")).isTrue()
        assertThat(matchesItem(orgunitItem, "aejWyOfXge6")).isFalse()
    }

    @Test
    fun should_map_orgunit_without_program() {
        val orgunitItem = TrackerLineListItem.OrganisationUnitItem()

        assertThat(matchesItem(orgunitItem, "ou")).isTrue()
        assertThat(matchesItem(orgunitItem, "IpHINAT79UW.ouname")).isFalse()
        assertThat(matchesItem(orgunitItem, "aejWyOfXge6")).isFalse()
    }

    private fun matchesItem(item: TrackerLineListItem, sortDimension: String): Boolean {
        val sorting = listOf(
            TrackerVisualizationSorting.builder()
                .dimension(sortDimension)
                .direction(SortingDirection.ASC)
                .build(),
        )

        return mapper.mapSorting(sorting, listOf(item)) ==
            listOf(
                TrackerLineListSortingItem(
                    item,
                    SortingDirection.ASC,
                ),
            )
    }
}
