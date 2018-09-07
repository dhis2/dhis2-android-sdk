/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectStore;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ReadOnlyCollectionRepositoryImpl<M extends Model> implements ReadOnlyCollectionRepository<M> {

    private final ObjectStore<M> store;
    protected final CursorModelFactory<M> modelFactory;
    protected final Collection<ChildrenAppender<M>> childrenAppenders;

    public ReadOnlyCollectionRepositoryImpl(ObjectStore<M> store,
                                            CursorModelFactory<M> modelFactory,
                                            Collection<ChildrenAppender<M>> childrenAppenders) {
        this.store = store;
        this.modelFactory = modelFactory;
        this.childrenAppenders = childrenAppenders;
    }

    public ReadOnlyCollectionRepositoryImpl(ObjectStore<M> store,
                                            CursorModelFactory<M> modelFactory) {
        this(store, modelFactory, Collections.<ChildrenAppender<M>>emptyList());
    }

    @Override
    public Set<M> getSet() {
        return store.selectAll(modelFactory);
    }

    @Override
    public Set<M> getSetWithAllChildren() {
        return ChildrenAppenderExecutor.appendInObjectSet(getSet(), childrenAppenders);
    }
}
