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

package org.hisp.dhis.android.core.relationship;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreRelationshipItemAdapter;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.Date;

public abstract class BaseRelationship extends BaseDeletableDataObject implements ObjectWithUidInterface {

    @Nullable
    @JsonProperty(RelationshipFields.RELATIONSHIP)
    public abstract String uid();

    @Nullable
    @JsonProperty(RelationshipFields.RELATIONSHIP_NAME)
    public abstract String name();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty()
    public abstract String relationshipType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreRelationshipItemAdapter.class)
    public abstract RelationshipItem from();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreRelationshipItemAdapter.class)
    public abstract RelationshipItem to();

    public abstract static class Builder<T extends Builder> extends BaseDeletableDataObject.Builder<T> {

        @JsonProperty(RelationshipFields.RELATIONSHIP)
        public abstract T uid(String uid);

        @JsonProperty(RelationshipFields.RELATIONSHIP_NAME)
        public abstract T name(String name);

        public abstract T created(Date created);

        public abstract T lastUpdated(Date lastUpdated);

        public abstract T relationshipType(String relationshipType);

        public abstract T from(RelationshipItem from);

        public abstract T to(RelationshipItem to);
    }
}
