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

import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class FilterConverterShould {

    interface TestService {
        @GET("api")
        retrofit2.Call<ResponseBody> test(@Query("filter") @Where Filter idFilter,
                                          @Query("filter") @Where Filter lastUpdatedFilter);
    }

    interface MixedTestService {
        @GET("api")
        retrofit2.Call<ResponseBody> test(
                @Query("field") @Which Fields fields,
                @Query("filter") @Where Filter idFilter,
                @Query("filter") @Where Filter lastUpdatedFilter);
    }

    @Test
    public void returns_correct_path_when_create_a_retrofit_request_using_filters() throws IOException, InterruptedException {
        ArrayList<String> values = new ArrayList(2);
        values.add("uid1");
        values.add("uid2");
        MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        TestService service = retrofit.create(TestService.class);
        service.test(
                InFilter.create(Field.create("id"), values),
                GtFilter.create(Field.create("lastUpdated"), "updatedDate")
        ).execute();

        RecordedRequest request = server.takeRequest();

        assertThat(request.getPath()).isEqualTo("/api?filter=id:in:[uid1,uid2]&filter=lastUpdated:gt:updatedDate");
        server.shutdown();
    }

    @Test
    public void returns_correct_path_when_create_a_retrofit_request_using_filters_and_single_value() throws IOException, InterruptedException {
        ArrayList<String> values = new ArrayList(2);
        values.add("uid1");

        MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        TestService service = retrofit.create(TestService.class);
        service.test(
                InFilter.create(Field.create("id"), values),
                GtFilter.create(Field.create("lastUpdated"), "updatedDate")
        ).execute();

        RecordedRequest request = server.takeRequest();

        assertThat(request.getPath()).isEqualTo("/api?filter=id:in:[uid1]&filter=lastUpdated:gt:updatedDate");
        server.shutdown();
    }

    @Test
    public void returns_correct_path_when_create_a_retrofit_request_using_filters_and_values() throws IOException, InterruptedException {
        ArrayList<String> values = new ArrayList(2);
        values.add("uid1");
        values.add("uid2");
        MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(FieldsConverterFactory.create())
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        MixedTestService service = retrofit.create(MixedTestService.class);
        service.test(
                Fields.builder().fields(
                        Field.create("id"), Field.create("code"),
                        Field.create("name"), Field.create("displayName")
                ).build(),
                InFilter.create(Field.create("id"), values),
                GtFilter.create(Field.create("lastUpdated"), "updatedDate")
        ).execute();

        RecordedRequest request = server.takeRequest();

        assertThat(request.getPath()).isEqualTo(
                "/api?field=id,code,name,displayName&filter=id:in:[uid1,uid2]&filter=lastUpdated:gt:updatedDate");
        server.shutdown();
    }

    @Test
    public void returns_correct_path_when_create_a_retrofit_request_ignoring_null_filter() throws IOException, InterruptedException {
        ArrayList<String> values = new ArrayList(2);
        values.add("uid1");
        values.add("uid2");
        MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        TestService service = retrofit.create(TestService.class);
        service.test(
                InFilter.create(Field.create("id"), values),
                GtFilter.create(Field.create("lastUpdated"), null)
        ).execute();

        RecordedRequest request = server.takeRequest();

        assertThat(request.getPath()).isEqualTo("/api?filter=id:in:[uid1,uid2]");
        server.shutdown();
    }

    @Test
    public void returns_correct_path_when_create_a_retrofit_request_ignoring_empty_string_filter() throws IOException, InterruptedException {
        ArrayList<String> values = new ArrayList(2);
        values.add("uid1");
        values.add("uid2");
        MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        TestService service = retrofit.create(TestService.class);
        service.test(
                InFilter.create(Field.create("id"), values),
                GtFilter.create(Field.create("lastUpdated"), "")
        ).execute();

        RecordedRequest request = server.takeRequest();

        assertThat(request.getPath()).isEqualTo("/api?filter=id:in:[uid1,uid2]");
        server.shutdown();
    }

    //TODO: test Filter for null input and empty string.

    @Test
    @SuppressWarnings("BadAnnotationImplementation")
    public void returns_converter_factory_on_correct_annotation() {
        Converter.Factory converterFactory = FilterConverterFactory.create();

        Converter<?, String> converter = converterFactory
                .stringConverter(null, new Annotation[]{new Where() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Where.class;
                    }
                }}, null);

        assertThat(converter).isInstanceOf(FilterConverter.class);
    }
}
