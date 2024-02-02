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
package org.hisp.dhis.android.core.arch.repositories.collection

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingData
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository

interface ReadOnlyCollectionRepository<M : Any> : BaseRepository {
    /**
     * Get the objects in scope in an asynchronous way, returning a `Single<List>`.
     *
     * @return A `Single` object with the list of objects.
     */
    fun get(): Single<List<M>>

    /**
     * Get the list of objects in a synchronous way. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.get].
     *
     * @return List of objects
     */
    fun blockingGet(): List<M>

    /**
     * Handy method to use in conjunction with PagedListAdapter to build paged lists.
     *
     * @param pageSize Length of the page
     * @return A LiveData object of PagedList of elements
     */
    @Deprecated(message = "Use {@link #getPagingData()} instead}", replaceWith = ReplaceWith("getPagingData()"))
    fun getPaged(pageSize: Int): LiveData<PagedList<M>>

    /**
     * Uses Paging3 library and return a Flow
     * @param pageSize Length of the page
     * @return a Flow of PagingData elements
     */
    fun getPagingData(pageSize: Int): Flow<PagingData<M>>

    /**
     * Get the count of elements in an asynchronous way, returning a `Single`.
     * @return A `Single` object with the element count
     */
    fun count(): Single<Int>

    /**
     * Get the count of elements. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.count].
     *
     * @return Element count
     */
    fun blockingCount(): Int

    /**
     * Check if selection of objects in current scope with applied filters is empty in an asynchronous way,
     * returning a `Single`.
     * @return If selection is empty
     */
    fun isEmpty(): Single<Boolean>

    /**
     * Check if selection of objects with applied filters is empty in a synchronous way.
     * Important: this is a blocking method and it should not be executed in the main thread.
     * Consider the asynchronous version [.isEmpty].
     *
     * @return If selection is empty
     */
    fun blockingIsEmpty(): Boolean

    /**
     * Get a [ReadOnlyObjectRepository] pointing to the first element in the list.
     *
     * @return Object repository
     */
    fun one(): ReadOnlyObjectRepository<M>
}
