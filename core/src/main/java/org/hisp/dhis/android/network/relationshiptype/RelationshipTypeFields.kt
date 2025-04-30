/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.network.relationshiptype

import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.relationship.RelationshipConstraint
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.relationship.RelationshipTypeTableInfo.Columns
import org.hisp.dhis.android.network.common.fields.AccessFields
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.hisp.dhis.android.network.common.fields.Fields

internal object RelationshipTypeFields : BaseFields<RelationshipType>() {
    private const val B_IS_TO_A = "bIsToA"
    private const val A_IS_TO_B = "aIsToB"
    private const val FROM_CONSTRAINT = "fromConstraint"
    private const val TO_CONSTRAINT = "toConstraint"
    private const val ACCESS = "access"

    // Used only for children appending, can't be used in query
    const val CONSTRAINTS = "constraints"

    val lastUpdated = fh.lastUpdated()

    val allFields = Fields.from(
        fh.getIdentifiableFields(),
        fh.field(B_IS_TO_A),
        fh.field(A_IS_TO_B),
        fh.field(Columns.FROM_TO_NAME),
        fh.field(Columns.TO_FROM_NAME),
        fh.field(Columns.BIDIRECTIONAL),
        fh.nestedField<RelationshipConstraint>(FROM_CONSTRAINT).with(RelationshipConstraintFields.allFields),
        fh.nestedField<RelationshipConstraint>(TO_CONSTRAINT).with(RelationshipConstraintFields.allFields),
        fh.nestedField<Access>(ACCESS).with(AccessFields.data.with(DataAccessFields.allFields)),
    )
}
