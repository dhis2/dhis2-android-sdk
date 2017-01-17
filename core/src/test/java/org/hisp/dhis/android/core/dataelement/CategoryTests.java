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

 package org.hisp.dhis.android.core.dataelement;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CategoryTests {

    private static Category.Builder VALID_BUILDER;

    @Before
    public void setUp() {
        VALID_BUILDER = createValidBuilder();
    }

    //**************************************************************************************
    //
    // BASE IDENTIFIABLE OBJECT TESTS
    //
    //**************************************************************************************

    @Test(expected = IllegalStateException.class)
    public void build_shouldThrowOnNullUidField() {
        VALID_BUILDER.uid(null).build();
    }

    //**************************************************************************************
    //
    // EQUALS VERIFIER
    //
    //**************************************************************************************

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(VALID_BUILDER.build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    //**************************************************************************************
    //
    // COLLECTION MUTATION TESTS
    //
    //**************************************************************************************

    @Test(expected = UnsupportedOperationException.class)
    public void categoryOptions_shouldThrowOnCollectionMutations() {
        Category category = VALID_BUILDER
                .categoryOptions(Arrays.asList(
                        CategoryOption.builder().uid("catOptUid01").build(),
                        CategoryOption.builder().uid("catOptUid02").build()))
                .build();

        category.categoryOptions().add(CategoryOption.builder().uid("catOptUid03").build());
    }

    //**************************************************************************************
    //
    // CREATE VALID BUILDER OBJECT
    //
    //**************************************************************************************

    private Category.Builder createValidBuilder() {
        return Category.builder()
                .uid("a1b2c3d4e5f")
                .created(new java.util.Date())
                .lastUpdated(new java.util.Date());
    }
}