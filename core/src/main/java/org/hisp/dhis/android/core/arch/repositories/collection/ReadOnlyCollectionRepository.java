/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.repositories.collection;

import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.common.Model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import io.reactivex.Single;

public interface ReadOnlyCollectionRepository<M extends Model> {

    /**
     * Query returning a {@link io.reactivex.Single} object. As querying might be a
     * time-consuming task, this method facilitates dealing with asynchronous behavior.
     *
     * @return A {@link io.reactivex.Single} object emitting a list of elements.
     */
    Single<List<M>> getAsync();

    /**
     * Get the list of elements. Important: this is blocking method and it should be executed in a
     * separated thread.
     *
     * @return List of elements
     */
    List<M> get();

    /**
     * Handy method to use in conjunction with {@link androidx.paging.PagedListAdapter} to build
     * paged lists.
     *
     * @param pageSize Length of the page
     * @return A {@link androidx.lifecycle.LiveData} object of
     * {@link androidx.paging.PagedList} of elements
     */
    LiveData<PagedList<M>> getPaged(int pageSize);

    /**
     * Get a count of elements. Important: this is a blocking method and it should be executed in
     * a separated thread.
     *
     * @return Element count
     */
    int count();

    /**
     * Get a {@link ReadOnlyObjectRepository} pointing to the first element in the list.
     *
     * @return {@link ReadOnlyObjectRepository}
     */
    ReadOnlyObjectRepository<M> one();
}