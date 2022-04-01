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

import dagger.Reusable
import javax.inject.Inject

@Reusable
internal class MultiDimensionalPartitioner @Inject constructor() {

    fun partitionForSize(size: Int, vararg partitions: Collection<String>): List<List<List<String>>> {
        return partitionInternal(
            UrlLengthHelper.getHowManyUidsFitInURL(size),
            listOf(partitions.map { it.toList() })
        )
    }

    fun partition(maxValues: Int, vararg partitions: Collection<String>): List<List<List<String>>> {
        return partitionInternal(
            maxValues,
            listOf(partitions.map { it.toList() })
        )
    }

    private fun partitionInternal(maxValues: Int, partitions: List<List<List<String>>>): List<List<List<String>>> {
        return partitions.flatMap { part ->
            val count = part.map { it.size }.sum()
            if (count <= maxValues) {
                listOf(part)
            } else {
                val largerDimension = part.maxByOrNull { it.size }!!
                val lds = largerDimension.size
                val largerDimensionPart1 = largerDimension.subList(0, lds / 2)
                val largerDimensionPart2 = largerDimension.subList(lds / 2, lds)
                val divided1 = replace(part, largerDimension, largerDimensionPart1)
                val divided2 = replace(part, largerDimension, largerDimensionPart2)
                partitionInternal(maxValues, listOf(divided1, divided2))
            }
        }
    }

    private fun replace(parent: List<List<String>>, check: List<String>, repl: List<String>): List<List<String>> {
        return parent.map { valueList -> if (valueList == check) repl else valueList }
    }
}
