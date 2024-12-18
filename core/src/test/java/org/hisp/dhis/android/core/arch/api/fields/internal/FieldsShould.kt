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
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.network.common.fields.Fields
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class FieldsShould {

    private val fh: FieldsHelper<Any> = FieldsHelper()

    @Test
    fun throw_illegal_argument_exception_on_null_arguments() {
        try {
            Fields.from<Any>()

            Assert.fail("IllegalArgumentException was expected but was not thrown")
        } catch (illegalArgumentException: IllegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    fun have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(Fields.from(fh.field("field")).javaClass)
            .suppress(Warning.NULL_FIELDS)
            .verify()
    }

    @Test
    @Throws(IOException::class)
    fun respect_the_field_order() {
        val queryStringOne: String = Fields.from(fh.field("")).generateString()

        val queryStringTwo: String = Fields.from(
            fh.field("*"),
        ).generateString()

        val queryStringThree: String = Fields.from(
            fh.field("name"),
            fh.field("displayName"),
            fh.field("created"),
            fh.field("lastUpdated"),
        ).generateString()

        Truth.assertThat(queryStringOne).isEqualTo("")
        Truth.assertThat(queryStringTwo).isEqualTo("*")
        Truth.assertThat(queryStringThree).isEqualTo("name,displayName,created,lastUpdated")
    }

    @Test
    @Throws(IOException::class)
    fun respect_fields_order_with_nested_fields() {
        val id = fh.field("id")
        val displayName = fh.field("displayName")
        val programs = fh.nestedField<Any>("programs")
        val programsWithChildren = programs.with(id, displayName)
        val programsWithChildrenWithChildren = programs.with(id, displayName, programsWithChildren)

        val queryStringOne: String = Fields.from(
            id,
            displayName,
            programs,
        ).generateString()
        val queryStringTwo: String = Fields.from(
            id,
            displayName,
            programsWithChildren,
        ).generateString()
        val queryStringThree: String = Fields.from(
            id,
            programsWithChildren,
            displayName,
        ).generateString()
        val queryStringFour: String = Fields.from(
            id,
            programsWithChildrenWithChildren,
        ).generateString()
        val queryStringFive: String = Fields.from(
            id,
            programsWithChildrenWithChildren,
            displayName,
        ).generateString()
        val queryStringSix: String = Fields.from(
            id,
            programs,
            displayName,
        ).generateString()

        Truth.assertThat(queryStringOne).isEqualTo("id,displayName,programs")
        Truth.assertThat(queryStringTwo).isEqualTo("id,displayName,programs[id,displayName]")
        Truth.assertThat(queryStringThree).isEqualTo("id,programs[id,displayName],displayName")
        Truth.assertThat(queryStringFour).isEqualTo("id,programs[id,displayName,programs[id,displayName]]")
        Truth.assertThat(queryStringFive).isEqualTo("id,programs[id,displayName,programs[id,displayName]],displayName")
        Truth.assertThat(queryStringSix).isEqualTo("id,programs,displayName")
    }
}
