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

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class FieldsConverterShould {
    private Converter<Fields, String> fieldsConverter;

    interface TestService {
        @GET("api/")
        retrofit2.Call<ResponseBody> test(@Query("fields") @Which Fields<String> fields);
    }

    @Before
    public void setUp() {
        fieldsConverter = new FieldsConverter();
    }

    @Test
    public void have_correct_retrofit_request_format() throws IOException, InterruptedException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();

        mockWebServer.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        TestService testService = retrofit.create(TestService.class);
        testService.test(Fields.<String>builder()
                .fields(
                        Field.<String, String>create("property_one"),
                        Field.<String, String>create("property_two"),
                        NestedField.<String, String>create("nested_property").with(
                                Field.<String, String>create("nested_property_one")))
                .build())
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo(
                "/api/?fields=property_one,property_two,nested_property[nested_property_one]");

        mockWebServer.shutdown();
    }

    @Test
    @SuppressWarnings("BadAnnotationImplementation")
    public void return_instance_of_fields_converters_when_create_a_field_converter_factory() {
        Converter.Factory converterFactory = FieldsConverterFactory.create();

        Converter<?, String> converter = converterFactory
                .stringConverter(null, new Annotation[]{new Which() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Which.class;
                    }
                }}, null);

        assertThat(converter).isInstanceOf(FieldsConverter.class);
    }

    @Test
    public void respect_the_field_order() throws IOException {
        String queryStringOne = fieldsConverter.convert(
                Fields.builder().fields(Field.create("")).build());
        String queryStringTwo = fieldsConverter.convert(
                Fields.builder().fields(Field.create("*")).build());
        String queryStringThree = fieldsConverter.convert(
                Fields.builder().fields(
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
    public void respect_fields_order_with_nested_fields() throws IOException {
        Field id = Field.create("id");
        Field displayName = Field.create("displayName");
        NestedField programs = NestedField.create("programs");
        NestedField programsWithChildren = programs.with(id, displayName);

        String queryStringOne = fieldsConverter.convert(
                Fields.builder().fields(
                        id, displayName, programs
                ).build());
        String queryStringTwo = fieldsConverter.convert(
                Fields.builder().fields(
                        id, displayName, programsWithChildren
                ).build());

        assertThat(queryStringOne).isEqualTo("id,displayName,programs");
        assertThat(queryStringTwo).isEqualTo("id,displayName,programs[id,displayName]");
    }
}
