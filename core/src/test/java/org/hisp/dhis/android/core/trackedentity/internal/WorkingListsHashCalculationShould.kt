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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WorkingListsHashCalculationShould {

    private fun objectWithUid(uid: String): ObjectWithUidInterface = ObjectWithUidInterface { uid }

    @Test
    fun return_null_hash_when_no_objects() {
        val hash = WorkingListsHashHelper.calculateHashFromObjects(emptyList())
        assertThat(hash).isNull()
    }

    @Test
    fun return_null_hash_when_null_list() {
        val hash = WorkingListsHashHelper.calculateHashFromObjects(null)
        assertThat(hash).isNull()
    }

    @Test
    fun return_null_hash_when_multiple_empty_lists() {
        val hash = WorkingListsHashHelper.calculateHashFromObjects(emptyList(), emptyList())
        assertThat(hash).isNull()
    }

    @Test
    fun return_same_hash_for_same_objects() {
        val objects = listOf(objectWithUid("filter1"), objectWithUid("filter2"))
        val hash1 = WorkingListsHashHelper.calculateHashFromObjects(objects)
        val hash2 = WorkingListsHashHelper.calculateHashFromObjects(objects)
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    fun return_same_hash_regardless_of_order() {
        val hash1 = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1"), objectWithUid("filter2")),
        )
        val hash2 = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter2"), objectWithUid("filter1")),
        )
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    fun return_different_hash_when_uids_change() {
        val hash1 = WorkingListsHashHelper.calculateHashFromObjects(listOf(objectWithUid("filter1")))
        val hash2 = WorkingListsHashHelper.calculateHashFromObjects(listOf(objectWithUid("filter2")))
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun return_different_hash_when_object_added() {
        val hash1 = WorkingListsHashHelper.calculateHashFromObjects(listOf(objectWithUid("filter1")))
        val hash2 = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1"), objectWithUid("filter2")),
        )
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun return_different_hash_when_object_removed() {
        val hash1 = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1"), objectWithUid("filter2")),
        )
        val hash2 = WorkingListsHashHelper.calculateHashFromObjects(listOf(objectWithUid("filter1")))
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun combine_multiple_object_lists_for_hash() {
        val hashFiltersOnly = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1")),
            emptyList(),
        )
        val hashWorkingListsOnly = WorkingListsHashHelper.calculateHashFromObjects(
            emptyList(),
            listOf(objectWithUid("wl1")),
        )
        val hashBoth = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1")),
            listOf(objectWithUid("wl1")),
        )

        assertThat(hashFiltersOnly).isNotEqualTo(hashWorkingListsOnly)
        assertThat(hashFiltersOnly).isNotEqualTo(hashBoth)
        assertThat(hashWorkingListsOnly).isNotEqualTo(hashBoth)
    }

    @Test
    fun return_same_hash_for_same_combination_regardless_of_order() {
        val hash1 = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1"), objectWithUid("filter2")),
            listOf(objectWithUid("wl1"), objectWithUid("wl2")),
        )
        val hash2 = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter2"), objectWithUid("filter1")),
            listOf(objectWithUid("wl2"), objectWithUid("wl1")),
        )
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    fun return_same_hash_for_equivalent_combinations() {
        val allObjects = listOf(
            objectWithUid("filter1"),
            objectWithUid("filter2"),
            objectWithUid("wl1"),
            objectWithUid("wl2"),
        )
        val hashDirect = WorkingListsHashHelper.calculateHashFromObjects(allObjects)
        val hashCombined = WorkingListsHashHelper.calculateHashFromObjects(
            listOf(objectWithUid("filter1"), objectWithUid("filter2")),
            listOf(objectWithUid("wl1"), objectWithUid("wl2")),
        )
        assertThat(hashDirect).isEqualTo(hashCombined)
    }
}
