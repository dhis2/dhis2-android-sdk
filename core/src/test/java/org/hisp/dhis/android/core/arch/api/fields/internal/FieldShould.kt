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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FieldShould {
//    @Test(expected = NullPointerException::class)
//    fun throw_null_pointer_exception_on_null_field_name() {
//        Field.create<Any, Any>(null)
//    }
//
//    @Test(expected = NullPointerException::class)
//    fun throw_null_pointer_exception_on_null_nested_field_name() {
//        NestedField.create<Any, Any>(null)
//    }

    @Test
    fun return_field_name_given_in_constructor() {
        val field: Field<*, *> = Field.create<Any, Any>("test_field_name")
        Truth.assertThat(field.name).isEqualTo("test_field_name")
    }

    @Test
    fun have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(Field.create<Any, Any>("").javaClass)
            .suppress(Warning.NULL_FIELDS)
            .verify()
    }

    @Test
    fun conform_nested_field_to_contract() {
        EqualsVerifier.forClass(NestedField.create<Any, Any>("").javaClass)
            .suppress(Warning.NULL_FIELDS)
            .verify()
    }

    @Test
    fun return_nested_field_children_when_they_are_added_in_a_new_variable() {
        val fieldOne = Field.create<String, String?>("fieldOne")
        val fieldTwo = Field.create<String, String?>("fieldTwo")

        val nestedField = NestedField.create<String, String>("test_nested_field")
        val nestedFieldWithChildren: NestedField<String, *> = nestedField.with(fieldOne, fieldTwo)

        Truth.assertThat(nestedField.children).isEmpty()
        Truth.assertThat(nestedField).isNotEqualTo(nestedFieldWithChildren)
        Truth.assertThat(
            nestedFieldWithChildren.children.contains(fieldOne) &&
                nestedFieldWithChildren.children.contains(fieldTwo),
        ).isTrue()
    }

//    @Test(expected = UnsupportedOperationException::class)
//    fun throw_unsupported_operation_exception_when_try_to_modify_a_immutable_nested_field() {
//        val fieldOne = Field.create<String, String?>("test_field_one")
//
//        val nestedField = NestedField.create<String, String>("test_nested_field")
//        val nestedFieldWithChildren: NestedField<String, *> = nestedField.with(fieldOne)
//
//        nestedFieldWithChildren.children.remove(0)
//    }

    @Test
    fun no_throw_exceptions_when_nested_fields_have_null_arguments() {
        val nestedField: NestedField<String, *> =
            NestedField.create<String, Any>("test_nested_field")
        Truth.assertThat(nestedField.with().children).isEmpty()
    }
}
