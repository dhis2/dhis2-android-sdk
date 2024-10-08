/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

/**
 * A collection of utility abstractions
 */
@Suppress("TooManyFunctions")
object CollectionsHelper {
    /**
     * A Null-safe safeUnmodifiableList.
     *
     * @param list
     * @return
     */
    @JvmStatic
    fun <T> safeUnmodifiableList(list: List<T>?): List<T>? {
        return list?.toList()
    }

    /**
     * A Null-safe deleted method.
     *
     * @param object object with deleted() method.
     * @return A boolean with the response of the deleted() method.
     */
    fun isDeleted(obj: ObjectWithDeleteInterface): Boolean {
        return obj.deleted() ?: false
    }

    /**
     * Throws an illegal argument exception if an object is null.
     *
     * @param object Object to validate.
     */
    fun <T> isNull(obj: T?) {
        requireNotNull(obj) { "Object must not be null" }
    }

    /**
     * Appends objects to an array in a new array.
     *
     * @param first Object array.
     * @param rest Objects to append.
     * @return An new array with the all the objects.
     */
    @JvmStatic
    @SafeVarargs
    fun <T> appendInNewArray(first: Array<T>, vararg rest: T): Array<T> {
        return first + rest
    }

    /**
     * Build a [String] with the values separated by commas and spaces.
     *
     * @param values Array with the values to concatenate.
     * @return A [String] with the concatenated values.
     */
    @JvmStatic
    fun commaAndSpaceSeparatedArrayValues(values: Array<String>): String {
        val withBrackets = values.contentToString()
        return withBrackets.substring(1, withBrackets.length - 1)
    }

    /**
     * Build a [String] with the values separated by commas and spaces.
     *
     * @param values Collection with the values to concatenate.
     * @return A [String] with the concatenated values.
     */
    @JvmStatic
    fun commaAndSpaceSeparatedCollectionValues(values: Collection<String>): String {
        return commaAndSpaceSeparatedArrayValues(values.toTypedArray<String>())
    }

    /**
     * Put in single quotes the string values inside a collection.
     *
     * @param objects Collection with the values to put in single quotes.
     * @return A [String[]] with the single quoted values.
     */
    fun withSingleQuotationMarksArray(objects: Collection<String>?): Array<String> {
        return objects?.map { "'$it'" }?.toTypedArray() ?: emptyArray()
    }

    /**
     * Put in single quotes a value.
     *
     * @param value Value to put in single quotes.
     * @return The single quote [String] value.
     */
    fun withSingleQuotationMarks(value: String?): String {
        return "'${value ?: ""}'"
    }

    /**
     * Build a [String] with the values separated by commas.
     *
     * @param values Array with the values to concatenate.
     * @return A [String] with the concatenated values.
     */
    @SafeVarargs
    private fun commaSeparatedArrayValues(vararg values: String): String {
        return commaAndSpaceSeparatedArrayValues(arrayOf(*values)).replace(", ", ",")
    }

    /**
     * Build a [String] with the values separated by commas.
     *
     * @param values Collection with the values to concatenate.
     * @return A [String] with the concatenated values.
     */
    @Suppress("SpreadOperator")
    fun commaSeparatedCollectionValues(values: Collection<String>): String {
        return commaSeparatedArrayValues(*values.toTypedArray<String>())
    }

    /**
     * Build a [String] with the uids of the objects separated by commas.
     *
     * @param objectsWithUid Collection with the objects to concatenate.
     * @return A [String] with the concatenated uids.
     */
    fun <O : ObjectWithUidInterface> commaSeparatedUids(objectsWithUid: Collection<O>): String {
        return commaSeparatedCollectionValues(getUids(objectsWithUid))
    }

    /**
     * Build a [String] with the values separated by semicolons.
     *
     * @param values Collection with the values to concatenate.
     * @return A [String] with the concatenated values.
     */
    fun semicolonSeparatedCollectionValues(values: Collection<String>): String {
        return joinCollectionWithSeparator(values, ";")
    }

    /**
     * Build a [String] with the values separated by custom separators.
     *
     * @param values    Collection with the values to concatenate.
     * @param separator Customizable separator.
     * @return A [String] with the concatenated values.
     */
    fun joinCollectionWithSeparator(values: Collection<String>, separator: String?): String {
        return commaSeparatedCollectionValues(values).replace(",", separator!!)
    }

    /**
     * Divide a [Collection] in a [List] of sets with a maximum size.
     *
     * @param originalSet The collection to divide.
     * @param size The maximum size of the partitions.
     * @return A [List] with the partitions of the given collection.
     */
    @JvmStatic
    fun <T> setPartition(originalSet: Collection<T>, size: Int): List<Set<T>> {
        return originalSet.chunked(size).map { it.toSet() }
    }
}
