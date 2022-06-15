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

package org.hisp.dhis.android.core.common;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringSetColumnAdapter;
import org.hisp.dhis.android.core.common.tableinfo.ItemFilterTableInfo;

import java.util.Set;

public abstract class FilterOperators {

    /**
     * Less than or equal to
     */
    @Nullable
    @JsonProperty()
    public abstract String le();

    /**
     * Greater than or equal to
     */
    @Nullable
    @JsonProperty()
    public abstract String ge();

    /**
     * Greater than
     */
    @Nullable
    @JsonProperty()
    public abstract String gt();

    /**
     * Lesser than
     */
    @Nullable
    @JsonProperty()
    public abstract String lt();

    /**
     * Equal to
     */
    @Nullable
    @JsonProperty()
    public abstract String eq();

    /**
     * In a list
     */
    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringSetColumnAdapter.class)
    @ColumnName(ItemFilterTableInfo.Columns.IN)
    public abstract Set<String> in();

    /**
     * Like
     */
    @Nullable
    @JsonProperty()
    public abstract String like();

    /**
     * If the dataItem is of type date, then date filtering parameters are specified using this.
     */
    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod dateFilter();

    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder<T extends Builder> {

        public abstract T le(String le);

        public abstract T ge(String ge);

        public abstract T gt(String gt);

        public abstract T lt(String lt);

        public abstract T eq(String eq);

        public abstract T in(Set<String> in);

        public abstract T like(String like);

        public abstract T dateFilter(DateFilterPeriod dateFilter);
    }
}