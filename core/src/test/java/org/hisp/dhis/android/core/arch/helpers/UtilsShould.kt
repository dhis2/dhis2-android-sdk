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
package org.hisp.dhis.android.core.arch.helpers

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.setPartition
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UtilsShould {
    @Test
    fun set_partition_returns_empty_array() {
        val list: Set<String> = HashSet()

        val partition: List<Set<String>?> = setPartition(list, 10)
        Truth.assertThat(partition).isEmpty()
    }

    @Test
    fun set_partition_return_single_list() {
        val list: MutableSet<String> = HashSet()
        list.add("first")
        list.add("second")

        val partition = setPartition(list, 10)
        Truth.assertThat(partition.size).isEqualTo(1)
        Truth.assertThat(partition[0].size).isEqualTo(2)
        Truth.assertThat(containsElementsInList(partition, list)).isTrue()
    }

    @Test
    fun set_partition_splits_list() {
        val list: MutableSet<String> = HashSet()
        for (i in 0..19) {
            list.add("element$i")
        }

        val partition = setPartition(list, 10)
        Truth.assertThat(partition.size).isEqualTo(2)
        Truth.assertThat(partition[0].size).isEqualTo(10)
        Truth.assertThat(partition[1].size).isEqualTo(10)
        Truth.assertThat(containsElementsInList(partition, list)).isTrue()
    }

    // Auxiliary methods
    private fun <T> containsElementsInList(partition: List<Set<T>>, list: Set<T>): Boolean {
        val flattenPartitions: MutableSet<T> = HashSet()
        for (item in partition) {
            flattenPartitions.addAll(item)
        }

        return flattenPartitions.containsAll(list)
    }
}
