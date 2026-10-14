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
package org.hisp.dhis.android.core.organisationunit

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit.Companion.builder
import org.junit.Test

class OrganisationUnitTreeShould {
    // Assigned uid's:
    private val ASSIGNED_L11 = "Level11"

    // Not assigned uid's:
    private val UNASSIGNED_L12 = "Level12"
    private val UNASSIGNED_L23 = "Level23"
    private val UNASSIGNED_ROOT = "RootOrtUnit"

    private val ouSamples = listOf(
        "Level11" to "/RootOrgUnit/Level11/",
        "Level21" to "/RootOrgUnit/Level11/Level21",
        "Level22" to "/RootOrgUnit/Level11/Level22",
        "Level24" to "/RootOrgUnit/Level12/Level24",
        "Level13" to "/RootOrgUnit/Level13/Level26",
        "Level13" to "/RootOrgUnit/Level13",
    )
    private val expectedResult = listOf("Level11", "Level13", "Level24")

    @Test
    fun return_all_root_uids_when_find_roots() {
        // create a bunch of dummy Organisation units from the strings:
        val orgUnits = ouSamples.map { (uid, path) -> builder().uid(uid).path(path).build() }
        val rootUids = getUids(OrganisationUnitTree.findRoots(orgUnits))

        // assert that: returned uid list does not contain unassigned & root.
        assertThat(rootUids.contains(UNASSIGNED_L12)).isFalse()
        assertThat(rootUids.contains(UNASSIGNED_L23)).isFalse()
        assertThat(rootUids.contains(UNASSIGNED_ROOT)).isFalse()

        // assert that: returned uid list contains what it should: expectedResult.
        assertThat(rootUids.size).isEqualTo(expectedResult.size)

        assertThat(rootUids.containsAll(expectedResult)).isTrue()
    }

    @Test
    fun return_all_root_uids_missing_slashes_when_find_roots() {
        val orgUnit = builder().uid(ouSamples[0].first).path("RootOrgUnit//Level11//").build()
        val orgUnits = listOf(orgUnit)

        val rootUids = getUids(OrganisationUnitTree.findRoots(orgUnits))
        assertThat(rootUids.contains(UNASSIGNED_L12)).isFalse()
        assertThat(rootUids.contains(UNASSIGNED_L23)).isFalse()
        assertThat(rootUids.contains(UNASSIGNED_ROOT)).isFalse()
        assertThat(rootUids.size).isEqualTo(1)

        assertThat(rootUids.contains(ASSIGNED_L11)).isTrue()
    }

    @Test
    fun return_all_root_uids_double_slashes_when_find_roots() {
        val orgUnit = builder().uid(ouSamples[0].first).path("//RootOrgUnit//Level11//").build()
        val orgUnits = listOf(orgUnit)

        val rootUids = getUids(OrganisationUnitTree.findRoots(orgUnits))
        assertThat(rootUids.contains(UNASSIGNED_L12)).isFalse()
        assertThat(rootUids.contains(UNASSIGNED_L23)).isFalse()
        assertThat(rootUids.contains(UNASSIGNED_ROOT)).isFalse()
        assertThat(rootUids.size).isEqualTo(1)

        assertThat(rootUids.contains(ASSIGNED_L11)).isTrue()
    }

    @Test(expected = IllegalArgumentException::class)
    fun return_all_root_uids_null_paths_when_find_roots() {
        val orgUnit = builder().uid(ouSamples[0].first).path(null).build()
        val orgUnits = listOf(orgUnit)

        OrganisationUnitTree.findRoots(orgUnits)
    }

    @Test
    fun return_all_root_empty_uids_list_when_find_roots_with_empty_list() {
        val rootUids = getUids(OrganisationUnitTree.findRoots(emptyList()))
        assertThat(rootUids.isEmpty()).isTrue()
    }

    @Test(expected = IllegalArgumentException::class)
    fun thrown_illegal_argument_exception_when_find_roots_uids_with_empty_paths() {
        val orgUnit = builder().uid(ouSamples[0].first).path("").build()
        val orgUnits = listOf(orgUnit)

        OrganisationUnitTree.findRoots(orgUnits)
    }

    @Test
    fun return_root_uids_when_have_not_assigned() {
        val orgUnit = builder().uid(ouSamples[0].first).path("/RootOrgUnit//Level11/").build()
        val orgUnits = listOf(orgUnit)

        val rootUids = getUids(OrganisationUnitTree.findRoots(orgUnits))
        assertThat(rootUids.contains(ASSIGNED_L11)).isTrue()
    }

    @Test
    fun findRoots_shouldReturnRootUids_NullList() {
        val rootUids = getUids(OrganisationUnitTree.findRoots(null))
        assertThat(rootUids.isEmpty()).isTrue()
    }
}
