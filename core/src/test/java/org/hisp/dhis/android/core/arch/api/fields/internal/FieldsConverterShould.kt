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
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hisp.dhis.android.core.arch.api.filters.internal.Which
import org.hisp.dhis.android.core.arch.api.testutils.RetrofitFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import retrofit2.Converter
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

@RunWith(JUnit4::class)
class FieldsConverterShould {
    private var fieldsConverter: Converter<Fields<*>, String>? = null

    internal interface TestService {
        @GET("api/")
        fun test(@Query("fields") @Which fields: Fields<String>?): Call<ResponseBody?>
    }

    @Before
    fun setUp() {
        fieldsConverter = FieldsConverter()
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun have_correct_retrofit_request_format() {
        val mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse())

        val testService = testService(mockWebServer)

        testService.test(
            Fields.from(
                Field.create("property_one"),
                Field.create("property_two"),
                NestedField.create<String, String>("nested_property").with(
                    Field.create("nested_property_one"),
                ),
            ),
        ).execute()

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.path).isEqualTo(
            "/api/?fields=property_one,property_two,nested_property[nested_property_one]",
        )

        mockWebServer.shutdown()
    }

    private fun testService(mockWebServer: MockWebServer): TestService {
        return RetrofitFactory.fromMockWebServer(mockWebServer).create(
            TestService::class.java,
        )
    }

    @Test
    fun returns_converter_factory_on_correct_annotation() {
        val retrofit = RetrofitFactory.fromMockWebServer(MockWebServer())
        val annotations = arrayOf<Annotation>(Which())
        val converter = FieldsConverterFactory().stringConverter(String::class.java, annotations, retrofit)

        assertThat(converter).isInstanceOf(FieldsConverter::class.java)
    }

    @Test
    fun returns_null_on_missing_annotation() {
        val retrofit = RetrofitFactory.fromMockWebServer(MockWebServer())
        val annotations = emptyArray<Annotation>()
        val converter = FieldsConverterFactory().stringConverter(String::class.java, annotations, retrofit)

        assertThat(converter).isNull()
    }
}
