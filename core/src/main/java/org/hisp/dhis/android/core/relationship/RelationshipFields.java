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

package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

public final class RelationshipFields {
    private static final String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
    private static final String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
    static final String RELATIONSHIP = "relationship";
    private static final String RELATIONSHIP_TYPE = "relationshipType";
    private static final String DISPLAY_NAME = "displayName";
    private static final String RELATIVE = "relative";
    private static final String FROM = "from";
    private static final String TO = "to";

    private static final Field<Relationship229Compatible, String> trackedEntityInstanceA
            = Field.create(TRACKED_ENTITY_INSTANCE_A);
    private static final Field<Relationship229Compatible, String> trackedEntityInstanceB
            = Field.create(TRACKED_ENTITY_INSTANCE_B);
    private static final Field<Relationship229Compatible, String> relationship = Field.create(RELATIONSHIP);
    private static final Field<Relationship229Compatible, String> relationshipType = Field.create(RELATIONSHIP_TYPE);
    private static final Field<Relationship229Compatible, String> displayName = Field.create(DISPLAY_NAME);
    private static final NestedField<Relationship229Compatible, TrackedEntityInstance> relative = NestedField.create(RELATIVE);
    private static final NestedField<Relationship229Compatible, RelationshipItem> from = NestedField.create(FROM);
    private static final NestedField<Relationship229Compatible, RelationshipItem> to = NestedField.create(TO);

    public static final Fields<Relationship229Compatible> allFields = Fields.<Relationship229Compatible>builder().fields(
            trackedEntityInstanceA, trackedEntityInstanceB, relationship, relationshipType, displayName,
            from.with(RelationshipItemFields.allFields), to.with(RelationshipItemFields.allFields), relative).build();
}
