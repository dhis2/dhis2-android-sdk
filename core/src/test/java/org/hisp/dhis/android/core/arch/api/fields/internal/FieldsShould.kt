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

package org.hisp.dhis.android.core.arch.api.fields.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class FieldsShould {

    @Test
    public void throw_illegal_argument_exception_on_null_arguments() {
        try {
            Fields.builder().fields().build();

            fail("IllegalArgumentException was expected but was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void throw_unsupported_operation_exception_when_try_to_modify_a_immutable_field() {
        Fields fields = Fields.builder()
                .fields(
                        Field.create("one"),
                        Field.create("two"),
                        Field.create("three"))
                .build();

        try {
            fields.fields().add(Field.create("four"));

            fail("UnsupportedOperationException was expected but nothing was thrown");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // swallow exception
        }
    }

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(Fields.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
