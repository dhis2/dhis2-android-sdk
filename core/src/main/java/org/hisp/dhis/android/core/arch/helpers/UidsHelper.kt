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

import org.hisp.dhis.android.core.arch.handlers.internal.Transformer
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

object UidsHelper {
    /**
     * Returns a [Set] of uids of the given objects.
     *
     * @param objects A collection of objects with uid.
     * @return A [Set] with the uids of the given objects.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> getUids(objects: Collection<O>): Set<String> {
        return getUidsList(objects).toSet()
    }

    /**
     * Returns a [Set] of uids of the children selected by a lambda function.
     *
     * @param objects A collection of objects with children with uid.
     * @param childExtractor The lambda to extract the child list from the parent
     * @return A [Set] with the uids of the children
     */
    @JvmStatic
    fun <P, C : ObjectWithUidInterface> getChildrenUids(
        parentList: Collection<P>,
        childExtractor: (P) -> List<C>
    ): Set<String> {
        return getUids(parentList.flatMap { childExtractor(it) })
    }

    /**
     * Return the uid of the object if the object is not null. If it is null, return null.
     *
     * @param o A object with uid.
     * @return The uid of the object.
     */
    @JvmStatic
    fun getUidOrNull(o: ObjectWithUidInterface?): String? {
        return o?.uid()
    }

    /**
     * Returns a [List] of uids of the given objects.
     *
     * @param objects A collection of objects with uid.
     * @return A [List] with the uids of the given objects.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> getUidsList(objects: Collection<O>): List<String> {
        return objects.map { o -> o.uid() }
    }

    /**
     * Remove from the given [Collection] the objects which uid is contained in the given [List] of uids.
     *
     * @param objects A collection of objects with uid.
     * @param uids A list of uids.
     * @return A [List] of the objects which uid is not contained in the given [List] of uids.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> excludeUids(objects: Collection<O>, uids: List<String>): List<O> {
        return objects.filterNot { o -> uids.contains(o.uid()) }
    }

    private fun <O : ObjectWithUidInterface> getUidsArray(objects: Collection<O>): Array<String> {
        return objects.map { o -> "'${o.uid()}'" }.toTypedArray()
    }

    /**
     * Map a [Collection] by uid.
     *
     * @param objects A collection of objects with uid.
     * @return A [Map] with the uid as key of the objects from the collection.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> mapByUid(objects: Collection<O>): Map<String, O> {
        return objects.map { it.uid() to it }.toMap()
    }

    /**
     * Map a [Collection] with the a custom key extracted from the parentExtractor.
     *
     * @param objects           A collection of objects with uid.
     * @param parentExtractor   A `Transformer` that extracts the parent property of the object.
     * @return A [Map] with a custom parent as key of the objects from the collection.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> mapByParentUid(
        objects: Collection<O>,
        parentExtractor: Transformer<O, String>
    ): Map<String, List<O>> {
        return objects.groupBy { o -> parentExtractor.transform(o) }
    }

    /**
     * Find an object with uid from a collection and returns it. Returns null if the object can`t be find.
     *
     * @param objects   A collection of objects with uid.
     * @param uid       The uid of the object to extract.
     * @return The objects from the collection with the given uid.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> findByUid(objects: Collection<O>, uid: String): O? {
        return objects.find { o -> o.uid() == uid }
    }

    /**
     * Build a [String] with the single quoted uids separated by commas and spaces.
     *
     * @param objects Array with the objects with uid to concatenate.
     * @return A [String] with the concatenated uids.
     */
    @JvmStatic
    fun <O : ObjectWithUidInterface> commaSeparatedUidsWithSingleQuotationMarks(
        objects: Collection<O>
    ): String {
        return CollectionsHelper.commaAndSpaceSeparatedArrayValues(getUidsArray(objects))
    }
}
