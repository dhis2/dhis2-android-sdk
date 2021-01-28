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

package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.relationship.internal.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

public final class RelationshipFields {
    // 2.29 only //
    private static final String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
    private static final String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
    private static final String RELATIVE = "relative";

    static final String RELATIONSHIP = "relationship";
    static final String RELATIONSHIP_NAME = "relationshipName";
    private static final String RELATIONSHIP_TYPE = "relationshipType";
    private static final String FROM = "from";
    private static final String TO = "to";

    // Used only for children appending, can't be used in query
    public static final String ITEMS = "items";

    private static FieldsHelper<Relationship229Compatible> fh = new FieldsHelper<>();

    public static final Fields<Relationship229Compatible> allFields
            = Fields.<Relationship229Compatible>builder().fields(
            fh.<String>field(TRACKED_ENTITY_INSTANCE_A),
            fh.<String>field(TRACKED_ENTITY_INSTANCE_B),
            fh.<String>field(RELATIONSHIP),
            fh.<String>field(RELATIONSHIP_NAME),
            fh.<String>field(RELATIONSHIP_TYPE),
            fh.<String>field(BaseIdentifiableObject.CREATED),
            fh.<String>field(BaseIdentifiableObject.LAST_UPDATED),
            fh.<RelationshipItem>nestedField(FROM).with(RelationshipItemFields.allFields),
            fh.<RelationshipItem>nestedField(TO).with(RelationshipItemFields.allFields),
            fh.<TrackedEntityInstance>nestedField(RELATIVE)
    ).build();

    private static FieldsHelper<Relationship> rfh = new FieldsHelper<>();

    public static final Fields<Relationship> allNewModelFields
            = Fields.<Relationship>builder().fields(
            rfh.<String>field(RELATIONSHIP),
            rfh.<String>field(RELATIONSHIP_NAME),
            rfh.<String>field(RELATIONSHIP_TYPE),
            rfh.<String>field(BaseIdentifiableObject.CREATED),
            rfh.<String>field(BaseIdentifiableObject.LAST_UPDATED),
            rfh.<RelationshipItem>nestedField(FROM).with(RelationshipItemFields.allFields),
            rfh.<RelationshipItem>nestedField(TO).with(RelationshipItemFields.allFields),
            rfh.<TrackedEntityInstance>nestedField(RELATIVE)
    ).build();

    private RelationshipFields() {
    }
}
