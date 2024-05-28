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

package org.hisp.dhis.android.core.arch.api.fields.internal;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.api.filters.internal.Filter;
import org.hisp.dhis.android.core.arch.api.filters.internal.InFilter;
import org.hisp.dhis.android.core.arch.api.filters.internal.SingleValueFilter;

import java.util.Collection;

import androidx.annotation.NonNull;

@AutoValue
public abstract class Field<Parent, Child> implements Property<Parent, Child> {

    public <V> Filter<Parent, Child> eq(V value) {
        return SingleValueFilter.Companion.eq(this, value.toString());
    }

    public Filter<Parent, Child> gt(String value) {
        return SingleValueFilter.Companion.gt(this, value);
    }

    public Filter<Parent, Child> like(String value) {
        return SingleValueFilter.Companion.like(this, value);
    }

    public Filter<Parent, Child> in(Collection<String> values) {
        return InFilter.Companion.create(this, values);
    }

    public static <T, K> Field<T, K> create(@NonNull String name) {
        return new AutoValue_Field<>(name);
    }
}
