/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.models.dataelement;

import org.junit.Test;

import java.util.Date;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryOptionComboTests {

    @Test
    public void equals_shouldConformToContract() {
        CategoryOptionCombo validCoc = CategoryOptionCombo.builder()
                .uid("a1b2c3d4e5f")
                .created(new Date())
                .lastUpdated(new Date())
                .build();

        EqualsVerifier.forClass(validCoc.getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test(expected = IllegalStateException.class)
    public void build_shouldThrowOnNullUidField() {
        CategoryOptionCombo.builder().build();
    }

    @Test
    public void isValid_shouldReturnFalseOnMalformedUid() {
        // corner case: empty string
        CategoryOptionCombo cocWithEmptyUid = CategoryOptionCombo
                .builder().uid("").build();

        // uid of 10 chars long
        CategoryOptionCombo cocWithShortUid = CategoryOptionCombo
                .builder().uid("a1b2c3d4e5").build();

        // uid of 12 chars long
        CategoryOptionCombo cocWithLongUid = CategoryOptionCombo
                .builder().uid("a1b2c3d4e5ff").build();

        assertThat(!cocWithEmptyUid.isValid());
        assertThat(!cocWithShortUid.isValid());
        assertThat(!cocWithLongUid.isValid());
    }

    @Test
    public void isValid_shouldReturnFalseOnNullCreatedField() {
        CategoryOptionCombo categoryOptionCombo = CategoryOptionCombo.builder()
                .uid("a1b2c3d4e5f")
                .lastUpdated(new Date())
                .build();

        assertThat(!categoryOptionCombo.isValid());
    }

    @Test
    public void isValid_shouldReturnFalseOnNullLastUpdatedField() {
        CategoryOptionCombo categoryOptionCombo = CategoryOptionCombo.builder()
                .uid("a1b2c3d4e5f")
                .created(new Date())
                .build();

        assertThat(!categoryOptionCombo.isValid());
    }

    @Test
    public void isValid_shouldReturnFalseOnNullCreatedAndLastUpdatedField() {
        CategoryOptionCombo categoryOptionCombo = CategoryOptionCombo.builder()
                .uid("a1b2c3d4e5f")
                .build();

        assertThat(!categoryOptionCombo.isValid());
    }

    @Test
    public void isValid_shouldReturnTrueOnValidObject() {
        CategoryOptionCombo categoryOptionCombo = CategoryOptionCombo.builder()
                .uid("a1b2c3d4e5f")
                .created(new Date())
                .lastUpdated(new Date())
                .build();

        assertThat(categoryOptionCombo.isValid());
    }
}
