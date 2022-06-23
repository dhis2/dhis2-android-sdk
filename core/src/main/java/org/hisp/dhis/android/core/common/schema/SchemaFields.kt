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
package org.hisp.dhis.android.core.common.schema

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper

internal object SchemaFields {
    private const val PROPERTIES = "properties"
    private const val NAME = "name"
    private const val PROPERTY_TYPE = "propertyType"
    private const val CONSTANTS = "constants"
    private val SchemaFh = FieldsHelper<Schema>()
    private val SchemaPropertyFh = FieldsHelper<Schema.Companion.SchemaProperty>()

    private val propertyFields: Fields<Schema.Companion.SchemaProperty> =
        Fields.builder<Schema.Companion.SchemaProperty>()
        .fields(
            SchemaPropertyFh.field<String>(NAME),
            SchemaPropertyFh.field<String>(PROPERTY_TYPE),
            SchemaPropertyFh.field<String>(CONSTANTS)
        ).build()

    val allFields: Fields<Schema> = Fields.builder<Schema>()
        .fields(
            SchemaFh.nestedField<Schema.Companion.SchemaProperty>(PROPERTIES)
                .with(propertyFields)
        ).build()
}
