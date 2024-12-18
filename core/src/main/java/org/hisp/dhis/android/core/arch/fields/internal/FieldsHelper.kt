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
package org.hisp.dhis.android.core.arch.fields.internal

import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseNameableObject
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.network.common.fields.Field
import org.hisp.dhis.android.network.common.fields.NestedField
import org.hisp.dhis.android.network.common.fields.Property

@Suppress("TooManyFunctions")
internal class FieldsHelper<O> {
    fun field(fieldName: String): Field<O> {
        return Field.create(fieldName)
    }

    fun <T> nestedField(fieldName: String): NestedField<O, T> {
        return NestedField.create(fieldName)
    }

    fun uid(): Field<O> {
        return Field.create(BaseIdentifiableObject.UID)
    }

    fun code(): Field<O> {
        return Field.create(BaseIdentifiableObject.CODE)
    }

    fun name(): Field<O> {
        return Field.create(BaseIdentifiableObject.NAME)
    }

    fun displayName(): Field<O> {
        return Field.create(BaseIdentifiableObject.DISPLAY_NAME)
    }

    fun created(): Field<O> {
        return Field.create(BaseIdentifiableObject.CREATED)
    }

    fun lastUpdated(): Field<O> {
        return Field.create(BaseIdentifiableObject.LAST_UPDATED)
    }

    fun deleted(): Field<O> {
        return Field.create(BaseIdentifiableObject.DELETED)
    }

    fun nestedFieldWithUid(fieldName: String): NestedField<O, *> {
        val nested = nestedField<ObjectWithUid>(fieldName)
        return nested.with(ObjectWithUid.uid)
    }

    fun getIdentifiableFields(): List<Property<O>> {
        return listOf(
            uid(),
            code(),
            name(),
            displayName(),
            created(),
            lastUpdated(),
            deleted(),
        )
    }

    fun getNameableFields(): List<Property<O>> {
        return getIdentifiableFields() + listOf(
            this.field(BaseNameableObject.SHORT_NAME),
            this.field(BaseNameableObject.DISPLAY_SHORT_NAME),
            this.field(BaseNameableObject.DESCRIPTION),
            this.field(BaseNameableObject.DISPLAY_DESCRIPTION),
        )
    }
}
