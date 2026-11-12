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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.network.common.fields.AccessFields
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.common.fields.Property
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Ignore("Test for fields string generation. Only to be executed on demand")
@RunWith(JUnit4::class)
class FieldsStringGenerationShould {

    @Test
    fun generate_not_empty_string() {
        val sortedFieldsClasses = fieldsClasses.sortedBy { it::class.simpleName }

        sortedFieldsClasses.forEach { fieldsClass ->
            fieldsClass::class.members
                .filter { it.returnType.toString().contains("Fields") }
                .forEach { member ->
                    val fieldInstance = try {
                        member.call(fieldsClass) as? Fields<*>
                    } catch (e: Exception) {
                        null
                    }

                    fieldInstance?.let { field ->
                        val sortedFields = field.fields.sortedBy { it.name }

                        @Suppress("UNCHECKED_CAST")
                        val sortedField = Fields(sortedFields as List<Property<Any>>)

                        val generatedString = sortedField.generateString()
                        println("${fieldsClass::class.simpleName}.${member.name}: $generatedString")

                        assertThat(generatedString).isNotEmpty()
                    }
                }
        }
    }

    private val fieldsClasses = listOf(
        AccessFields,
        DataAccessFields,
    )
}
