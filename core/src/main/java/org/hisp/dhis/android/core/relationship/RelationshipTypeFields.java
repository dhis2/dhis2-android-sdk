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

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

final class RelationshipTypeFields {

    static final String B_IS_TO_A = "bIsToA";
    static final String A_IS_TO_B = "aIsToB";
    static final String FROM_CONSTRAINT = "fromConstraint";
    static final String TO_CONSTRAINT = "toConstraint";

    private static final FieldsHelper<RelationshipType> fh = new FieldsHelper<>();

    static final Field<RelationshipType, String> lastUpdated = fh.lastUpdated();

    static final Fields<RelationshipType> allFields = Fields.<RelationshipType>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<String>field(B_IS_TO_A),
                    fh.<String>field(A_IS_TO_B),
                    fh.<RelationshipConstraint>nestedField(FROM_CONSTRAINT)
                            .with(RelationshipConstraintFields.allFields),
                    fh.<RelationshipConstraint>nestedField(TO_CONSTRAINT)
                            .with(RelationshipConstraintFields.allFields)
            ).build();

    private RelationshipTypeFields() {
    }
}