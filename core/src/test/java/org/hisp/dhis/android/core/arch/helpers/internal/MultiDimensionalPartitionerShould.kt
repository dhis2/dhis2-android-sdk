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
package org.hisp.dhis.android.core.arch.helpers.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MultiDimensionalPartitionerShould {

    private val partitioner = MultiDimensionalPartitioner()

    @Test
    fun `Should return same partition for one dimension under threshold`() {
        val values = listOf("A1", "A2")
        assertPartition(
            partitioner.partition(4, values),
            """
            [[[A1,A2]]]
        """
        )
    }

    @Test
    fun `Should return same partition for one dimension at threshold`() {
        val values = listOf("A1", "A2", "A3", "A4")
        assertPartition(
            partitioner.partition(4, values),
            """
            [[[A1,A2,A3,A4]]]
        """
        )
    }

    @Test
    fun `Should return two partitions for one dimension over threshold`() {
        val values = listOf("A1", "A2", "A3", "A4", "A5")
        assertPartition(
            partitioner.partition(4, values),
            """
            [[[A1,A2]],
            [[A3,A4,A5]]]
        """
        )
    }

    @Test
    fun `Should return same partition for two dimensions under threshold`() {
        val valuesA = listOf("A1", "A2")
        val valuesB = listOf("B1")
        assertPartition(
            partitioner.partition(4, valuesA, valuesB),
            """
            [[[A1,A2],[B1]]]
        """
        )
    }

    @Test
    fun `Should return same partition for two dimensions at threshold`() {
        val valuesA = listOf("A1", "A2")
        val valuesB = listOf("B1", "B2")
        assertPartition(
            partitioner.partition(4, valuesA, valuesB),
            """
            [[[A1,A2],[B1,B2]]]
        """
        )
    }

    @Test
    fun `Should partition by larger dimension for two dimensions over threshold`() {
        val valuesA = listOf("A1", "A2")
        val valuesB = listOf("B1", "B2", "B3")
        assertPartition(
            partitioner.partition(4, valuesA, valuesB),
            """
            [[[A1,A2],[B1]],
            [[A1,A2],[B2,B3]]]
        """
        )
    }

    @Test
    fun `Should partition by both dimensions for two dimensions over threshold if one not enough`() {
        val valuesA = listOf("A1", "A2", "A3")
        val valuesB = listOf("B1", "B2", "B3")
        assertPartition(
            partitioner.partition(4, valuesA, valuesB),
            """
            [[[A1],[B1,B2,B3]],
            [[A2,A3],[B1]],
            [[A2,A3],[B2,B3]]]"""
        )
    }

    @Test
    fun `Should partition twice by larger dimension for two dimensions over threshold if one not enough`() {
        val valuesA = listOf("A1")
        val valuesB = listOf("B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8")
        assertPartition(
            partitioner.partition(4, valuesA, valuesB),
            """
               [[[A1],[B1,B2]],
               [[A1],[B3,B4]],
               [[A1],[B5,B6]],
               [[A1],[B7,B8]]]"""
        )
    }

    @Test
    fun `Should partition for 3 dimensions over threshold`() {
        val valuesA = listOf("A1", "A2", "A3")
        val valuesB = listOf("B1", "B2", "B3")
        val valuesC = listOf("C1", "C2", "C3")
        assertPartition(
            partitioner.partition(4, valuesA, valuesB, valuesC),
            """
                    [[[A1], [B1], [C1]],
                    [[A1], [B1], [C2, C3]],
                    [[A1], [B2, B3], [C1]],
                    [[A1], [B2], [C2, C3]],
                    [[A1], [B3], [C2, C3]],
                    [[A2, A3], [B1], [C1]],
                    [[A2], [B1], [C2, C3]],
                    [[A3], [B1], [C2, C3]],
                    [[A2], [B2, B3], [C1]],
                    [[A3], [B2, B3], [C1]],
                    [[A2], [B2], [C2, C3]],
                    [[A2], [B3], [C2, C3]],
                    [[A3], [B2], [C2, C3]],
                    [[A3], [B3], [C2, C3]]]
                """
        )
    }

    private fun assertPartition(actual: List<List<List<String>>>, arrayStr: String) {
        val actualStr = actual.toString()
            .replace(" ", "")
        val expected = arrayStr
            .replace(" ", "")
            .replace("\n", "")
        assertThat(actualStr).isEqualTo(expected)
    }
}
