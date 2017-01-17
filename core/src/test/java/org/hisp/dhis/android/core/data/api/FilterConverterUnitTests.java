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

 package org.hisp.dhis.android.core.data.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.annotation.Annotation;

import retrofit2.Converter;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class FilterConverterUnitTests {
    private Converter<Filter, String> fieldsConverter;

    @Before
    public void setUp() {
        fieldsConverter = new FilterConverter();
    }

    @Test
    @SuppressWarnings("BadAnnotationImplementation")
    public void converterFactory_shouldReturnConverterOnSpecificAnnotation() {
        Converter.Factory converterFactory = FilterConverterFactory.create();

        Converter<?, String> converter = converterFactory
                .stringConverter(null, new Annotation[]{new Fields() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Fields.class;
                    }
                }}, null);

        assertThat(converter).isInstanceOf(FilterConverter.class);
    }

    @Test
    public void converter_shouldRespectFields() throws IOException {
        String queryStringOne = fieldsConverter.convert(
                Filter.builder().fields(Field.create("")).build());
        String queryStringTwo = fieldsConverter.convert(
                Filter.builder().fields(Field.create("*")).build());
        String queryStringThree = fieldsConverter.convert(
                Filter.builder().fields(
                        Field.create("name"),
                        Field.create("displayName"),
                        Field.create("created"),
                        Field.create("lastUpdated")
                ).build());

        assertThat(queryStringOne).isEqualTo("");
        assertThat(queryStringTwo).isEqualTo("*");
        assertThat(queryStringThree).isEqualTo("name,displayName,created,lastUpdated");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void converter_shouldRespectNestedFields() throws IOException {
        Field id = Field.create("id");
        Field displayName = Field.create("displayName");
        NestedField programs = NestedField.create("programs");
        NestedField programsWithChildren = programs.with(id, displayName);

        String queryStringOne = fieldsConverter.convert(
                Filter.builder().fields(
                        id, displayName, programs
                ).build());
        String queryStringTwo = fieldsConverter.convert(
                Filter.builder().fields(
                        id, displayName, programsWithChildren
                ).build());

        assertThat(queryStringOne).isEqualTo("id,displayName,programs");
        assertThat(queryStringTwo).isEqualTo("id,displayName,programs[id,displayName]");
    }
}
