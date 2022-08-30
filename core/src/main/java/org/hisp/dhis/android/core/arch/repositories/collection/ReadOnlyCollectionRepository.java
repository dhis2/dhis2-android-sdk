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
package org.hisp.dhis.android.core.arch.repositories.collection;

import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import io.reactivex.Single;

public interface ReadOnlyCollectionRepository<M extends CoreObject> extends BaseRepository {

    /**
     * Get the objects in scope in an asynchronous way, returning a {@code Single<List>}.
     *
     * @return A {@code Single} object with the list of objects.
     */
    Single<List<M>> get();

    /**
     * Get the list of objects in a synchronous way. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version {@link #get()}.
     *
     * @return List of objects
     */
    List<M> blockingGet();

    /**
     * Handy method to use in conjunction with PagedListAdapter to build paged lists.
     *
     * @param pageSize Length of the page
     * @return A LiveData object of PagedList of elements
     */
    LiveData<PagedList<M>> getPaged(int pageSize);

    /**
     * Get the count of elements in an asynchronous way, returning a {@code Single}.
     * @return A {@code Single} object with the element count
     */
    Single<Integer> count();

    /**
     * Get the count of elements. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version {@link #count()}.
     *
     * @return Element count
     */
    int blockingCount();

    /**
     * Check if selection of objects in current scope with applied filters is empty in an asynchronous way,
     * returning a {@code Single}.
     * @return If selection is empty
     */
    Single<Boolean> isEmpty();

    /**
     * Check if selection of objects with applied filters is empty in a synchronous way.
     * Important: this is a blocking method and it should not be executed in the main thread.
     * Consider the asynchronous version {@link #isEmpty()}.
     *
     * @return If selection is empty
     */
    boolean blockingIsEmpty();

    /**
     * Get a {@link ReadOnlyObjectRepository} pointing to the first element in the list.
     *
     * @return Object repository
     */
    ReadOnlyObjectRepository<M> one();
}