/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.core.commons;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

public final class Query {

    @Nullable
    private final String[] projection;

    @Nullable
    private final String selection;

    @Nullable
    private final String[] selectionArgs;

    @Nullable
    private final String sortOrder;

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    private Query(@Nullable String[] projection, @Nullable String selection,
                  @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    @Nullable
    public String[] projection() {
        if (projection != null) {
            return Arrays.copyOf(projection, projection.length);
        }

        return null;
    }

    @Nullable
    public String[] selectionArgs() {
        if (selectionArgs != null) {
            return Arrays.copyOf(selectionArgs, selectionArgs.length);
        }

        return null;
    }

    @Nullable
    public String selection() {
        return selection;
    }

    @Nullable
    public String sortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Query query = (Query) other;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(projection, query.projection)) {
            return false;
        }

        if (selection != null ? !selection.equals(query.selection) : query.selection != null) {
            return false;
        }

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(selectionArgs, query.selectionArgs)) {
            return false;
        }

        return sortOrder != null ? sortOrder.equals(query.sortOrder) : query.sortOrder == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(projection);
        result = 31 * result + (selection != null ? selection.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(selectionArgs);
        result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private String[] projection;
        private String[] selectionArgs;
        private String selection;
        private String sortOrder;

        Builder() {
            // explicit constructor
        }

        public Builder projection(String[] projection) {
            this.projection = projection;
            return this;
        }

        public Builder selectionArgs(String[] selectionArgs) {
            this.selectionArgs = selectionArgs;
            return this;
        }

        public Builder selection(String selection) {
            this.selection = selection;
            return this;
        }

        public Builder sortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Query build() {
            return new Query(projection, selection, selectionArgs, sortOrder);
        }
    }
}
