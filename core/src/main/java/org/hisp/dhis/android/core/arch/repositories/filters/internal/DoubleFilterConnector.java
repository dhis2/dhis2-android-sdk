/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;

public final class DoubleFilterConnector<R extends BaseRepository>
        extends BaseAbstractFilterConnector<R, Double> {

    DoubleFilterConnector(BaseRepositoryFactory<R> repositoryFactory,
                          RepositoryScope scope,
                          String key) {
        super(repositoryFactory, scope, key);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The smallerThan filter checks if the given field has a value which is smaller than the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R smallerThan(double value) {
        return newWithWrappedScope(FilterItemOperator.LT, value);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The biggerThan filter checks if the given field has a value which is bigger than the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R biggerThan(double value) {
        return newWithWrappedScope(FilterItemOperator.GT, value);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The biggerOrEqualTo filter checks if the given field has a value which is bigger or equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R biggerOrEqualTo(double value) {
        return newWithWrappedScope(FilterItemOperator.GE, value);
    }

    String wrapValue(Double value) {
        return value.toString();
    }
}
