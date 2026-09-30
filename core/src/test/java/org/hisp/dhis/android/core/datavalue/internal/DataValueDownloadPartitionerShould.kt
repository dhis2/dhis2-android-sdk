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
package org.hisp.dhis.android.core.datavalue.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DataValueDownloadPartitionerShould {

    @Test
    fun issue_a_single_request_when_the_potential_values_are_under_the_limit() {
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(4), attributeOptionComboUids = aocs(2), maxPotentialValues = 100),
        )

        assertThat(partitions).hasSize(1)
        assertThat(partitions.single().periodIds).isEqualTo(periods(4))
        assertThat(partitions.single().orgUnitUids).containsExactly(ROOT)
        assertThat(partitions.single().includeDescendants).isTrue()
        assertThat(partitions.single().attributeOptionComboUids).isEqualTo(aocs(2))
    }

    @Test
    fun split_the_periods_before_any_other_dimension() {
        // 4 periods x 7 orgunits x 1 coc x 1 aoc = 28
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(4), maxPotentialValues = 14),
        )

        assertThat(partitions.map { it.periodIds }).containsExactly(
            listOf("p0", "p1"),
            listOf("p2", "p3"),
        )
        partitions.forEach {
            assertThat(it.orgUnitUids).containsExactly(ROOT)
            assertThat(it.includeDescendants).isTrue()
        }
    }

    @Test
    fun split_the_attribute_option_combos_once_a_single_period_is_not_enough() {
        // 1 period x 7 orgunits x 1 coc x 4 aocs = 28
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(1), attributeOptionComboUids = aocs(4), maxPotentialValues = 14),
        )

        assertThat(partitions.map { it.attributeOptionComboUids }).containsExactly(
            listOf("aoc0", "aoc1"),
            listOf("aoc2", "aoc3"),
        )
        partitions.forEach {
            assertThat(it.orgUnitUids).containsExactly(ROOT)
            assertThat(it.includeDescendants).isTrue()
        }
    }

    @Test
    fun touch_the_organisation_units_only_after_periods_and_option_combos_are_exhausted() {
        // 2 periods x 7 orgunits x 1 coc x 2 aocs = 28. Every dimension has to give way.
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(2), attributeOptionComboUids = aocs(2), maxPotentialValues = 3),
        )

        assertThat(partitions.map { it.periodIds }.distinct()).containsExactly(listOf("p0"), listOf("p1"))
        assertThat(partitions.map { it.attributeOptionComboUids }.distinct())
            .containsExactly(listOf("aoc0"), listOf("aoc1"))
        assertThat(partitions.any { !it.includeDescendants }).isTrue()
    }

    @Test
    fun descend_one_level_keeping_the_parents_in_their_own_not_expanded_request() {
        // 1 period x 7 orgunits x 1 coc x 1 aoc = 7
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(1), maxPotentialValues = 3),
        )

        assertThat(partitions).hasSize(3)
        assertThat(partitions[0].orgUnitUids).containsExactly(ROOT)
        assertThat(partitions[0].includeDescendants).isFalse()
        assertThat(partitions[1].orgUnitUids).containsExactly("a")
        assertThat(partitions[1].includeDescendants).isTrue()
        assertThat(partitions[2].orgUnitUids).containsExactly("b")
        assertThat(partitions[2].includeDescendants).isTrue()
    }

    @Test
    fun cover_the_whole_organisation_unit_space_exactly_once_when_descending() {
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(1), maxPotentialValues = 1),
        )

        val covered = partitions.flatMap { partition ->
            partition.orgUnitUids.flatMap { uid ->
                if (partition.includeDescendants) subtreeOf(uid) else listOf(uid)
            }
        }

        assertThat(covered).containsExactlyElementsIn(HIERARCHY.keys + HIERARCHY.values.flatten())
        assertThat(covered).hasSize(covered.distinct().size)
    }

    @Test
    fun descend_recursively_until_every_request_is_within_the_limit() {
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(1), maxPotentialValues = 1),
        )

        val hierarchy = DataValueOrgUnitHierarchy(HIERARCHY, ALL_ORG_UNITS)
        partitions.forEach { partition ->
            assertThat(hierarchy.assignedCount(partition.orgUnitUids, partition.includeDescendants))
                .isAtMost(1L)
        }
    }

    @Test
    fun issue_a_single_request_when_no_dimension_can_be_split_any_further() {
        // A leaf with more option combos in a cell than the limit allows cannot be split at all.
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(
                periodIds = periods(1),
                rootOrgUnitUids = listOf("a1"),
                categoryOptionCombosPerCell = 50,
                maxPotentialValues = 10,
            ),
        )

        assertThat(partitions).hasSize(1)
        assertThat(partitions.single().orgUnitUids).containsExactly("a1")
    }

    @Test
    fun discard_the_requests_whose_organisation_units_are_not_assigned_to_the_data_set() {
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(
                periodIds = periods(1),
                assignedOrgUnitUids = setOf("a1", "a2"),
                maxPotentialValues = 1,
            ),
        )

        val covered = partitions.flatMap { partition ->
            partition.orgUnitUids.flatMap { uid ->
                if (partition.includeDescendants) subtreeOf(uid) else listOf(uid)
            }
        }
        assertThat(covered).containsAtLeast("a1", "a2")
        assertThat(covered).doesNotContain("b1")
        assertThat(covered).doesNotContain("b2")
    }

    @Test
    fun omit_the_attribute_option_combo_filter_when_the_data_set_has_no_option_combos() {
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(1), attributeOptionComboUids = emptyList(), maxPotentialValues = 100),
        )

        assertThat(partitions.single().attributeOptionComboUids).isNull()
    }

    @Test
    fun keep_every_request_within_the_url_budget_even_when_the_volume_fits() {
        val partitions = DataValueDownloadPartitioner.partition(
            estimate(periodIds = periods(400), maxPotentialValues = Long.MAX_VALUE),
        )

        assertThat(partitions).isNotEmpty()
        partitions.forEach { partition ->
            val uids = 1 + partition.periodIds.size + partition.orgUnitUids.size +
                partition.attributeOptionComboUids.orEmpty().size
            assertThat(uids).isAtMost(MAX_UIDS_IN_URL)
        }
        assertThat(partitions.flatMap { it.periodIds }).containsExactlyElementsIn(periods(400))
    }

    @Test
    fun require_an_org_unit_split_only_when_a_single_cell_column_exceeds_the_limit() {
        assertThat(DataValueDownloadPartitioner.requiresOrgUnitSplit(10, 100, 1_000)).isFalse()
        assertThat(DataValueDownloadPartitioner.requiresOrgUnitSplit(10, 101, 1_000)).isTrue()
    }

    @Suppress("LongParameterList")
    private fun estimate(
        periodIds: List<String>,
        rootOrgUnitUids: List<String> = listOf(ROOT),
        attributeOptionComboUids: List<String> = aocs(1),
        categoryOptionCombosPerCell: Int = 1,
        assignedOrgUnitUids: Set<String> = ALL_ORG_UNITS,
        maxPotentialValues: Long = DataValueDownloadPartitioner.DEFAULT_MAX_POTENTIAL_VALUES,
    ) = DataValueDownloadEstimate(
        dataSetUid = "dataSet1",
        periodIds = periodIds,
        rootOrgUnitUids = rootOrgUnitUids,
        attributeOptionComboUids = attributeOptionComboUids,
        categoryOptionCombosPerCell = categoryOptionCombosPerCell,
        orgUnits = DataValueOrgUnitHierarchy(HIERARCHY, assignedOrgUnitUids),
        maxPotentialValues = maxPotentialValues,
    )

    private fun periods(count: Int) = (0 until count).map { "p$it" }

    private fun aocs(count: Int) = (0 until count).map { "aoc$it" }

    private fun subtreeOf(uid: String): List<String> =
        listOf(uid) + HIERARCHY[uid].orEmpty().flatMap { subtreeOf(it) }

    companion object {
        private const val ROOT = "root"

        /**
         * root
         *  |- a -> a1, a2
         *  |- b -> b1, b2
         */
        private val HIERARCHY = mapOf(
            ROOT to listOf("a", "b"),
            "a" to listOf("a1", "a2"),
            "b" to listOf("b1", "b2"),
        )

        private val ALL_ORG_UNITS = setOf(ROOT, "a", "b", "a1", "a2", "b1", "b2")

        private const val MAX_UIDS_IN_URL = 169
    }
}
