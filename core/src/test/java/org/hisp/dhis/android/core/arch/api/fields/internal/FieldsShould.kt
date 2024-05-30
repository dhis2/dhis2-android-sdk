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
package org.hisp.dhis.android.core.arch.api.fields.internal

import com.google.common.truth.Truth
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class FieldsShould {
    @Test
    fun throw_illegal_argument_exception_on_null_arguments() {
        try {
            Fields.builder<Any>().fields().build()

            Assert.fail("IllegalArgumentException was expected but was not thrown")
        } catch (illegalArgumentException: IllegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    fun have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(Fields.builder<Any>().build().javaClass)
            .suppress(Warning.NULL_FIELDS)
            .verify()
    }

    @Test
    @Throws(IOException::class)
    fun respect_the_field_order() {
        val queryStringOne: String = Fields.builder<Any>().fields(
            Field.create<Any, Any?>(""),
        ).build().generateString(null)

        val queryStringTwo: String = Fields.builder<Any>().fields(
            Field.create<Any, Any?>("*"),
        ).build().generateString(null)

        val queryStringThree: String = Fields.builder<Any>().fields(
            Field.create<Any, Any?>("name"),
            Field.create<Any, Any?>("displayName"),
            Field.create<Any, Any?>("created"),
            Field.create<Any, Any?>("lastUpdated"),
        ).build().generateString(null)

        Truth.assertThat(queryStringOne).isEqualTo("")
        Truth.assertThat(queryStringTwo).isEqualTo("*")
        Truth.assertThat(queryStringThree).isEqualTo("name,displayName,created,lastUpdated")
    }

    @Test
    @Throws(IOException::class)
    fun respect_fields_order_with_nested_fields() {
        val id: Field<Any, *> = Field.create<Any, Any>("id")
        val displayName: Field<Any, *> = Field.create<Any, Any>("displayName")
        val programs: NestedField<Any, Any> = NestedField.create("programs")
        val programsWithChildren = programs.with(id, displayName)
        val programsWithChildrenWithChildren = programs.with(id, displayName, programsWithChildren)

        val queryStringOne: String = Fields.builder<Any>().fields(
            id,
            displayName,
            programs,
        ).build().generateString(null)
        val queryStringTwo: String = Fields.builder<Any>().fields(
            id,
            displayName,
            programsWithChildren,
        ).build().generateString(null)
        val queryStringThree: String = Fields.builder<Any>().fields(
            id,
            programsWithChildren,
            displayName,
        ).build().generateString(null)
        val queryStringFour: String = Fields.builder<Any>().fields(
            id,
            programsWithChildrenWithChildren,
        ).build().generateString(null)
        val queryStringFive: String = Fields.builder<Any>().fields(
            id,
            programsWithChildrenWithChildren,
            displayName,
        ).build().generateString(null)
        val queryStringSix: String = Fields.builder<Any>().fields(
            id,
            programs,
            displayName,
        ).build().generateString(null)

        Truth.assertThat(queryStringOne).isEqualTo("id,displayName,programs")
        Truth.assertThat(queryStringTwo).isEqualTo("id,displayName,programs[id,displayName]")
        Truth.assertThat(queryStringThree).isEqualTo("id,programs[id,displayName],displayName")
        Truth.assertThat(queryStringFour).isEqualTo("id,programs[id,displayName,programs[id,displayName]]")
        Truth.assertThat(queryStringFive).isEqualTo("id,programs[id,displayName,programs[id,displayName]],displayName")
        Truth.assertThat(queryStringSix).isEqualTo("id,programs,displayName")
    }
}
