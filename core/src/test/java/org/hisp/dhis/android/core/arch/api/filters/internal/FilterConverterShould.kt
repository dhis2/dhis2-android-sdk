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
package org.hisp.dhis.android.core.arch.api.filters.internal

import com.google.common.truth.Truth.assertThat
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hisp.dhis.android.core.arch.api.fields.internal.Field.Companion.create
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields.Companion.builder
import org.hisp.dhis.android.core.arch.api.filters.internal.InFilter.Companion.create
import org.hisp.dhis.android.core.arch.api.filters.internal.SingleValueFilter.Companion.gt
import org.hisp.dhis.android.core.arch.api.testutils.RetrofitFactory
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

class FilterConverterShould {

    internal interface TestService {
        @GET("api")
        fun test(
            @Query("filter") @Where idFilter: Filter<String>?,
            @Query("filter") @Where lastUpdatedFilter: Filter<String>?,
        ): Call<ResponseBody?>
    }

    internal interface MixedTestService {
        @GET("api")
        fun test(
            @Query("field") @Which fields: Fields<String>?,
            @Query("filter") @Where idFilter: Filter<String>?,
            @Query("filter") @Where lastUpdatedFilter: Filter<String>?,
        ): Call<ResponseBody?>
    }

    @Before
    @Throws(IOException::class)
    fun setUp() {
        server!!.enqueue(MockResponse())
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun returns_correct_path_when_create_a_retrofit_request_using_filters() {
        testService(server!!).test(
            create(create("id"), listOf("uid1", "uid2")),
            gt(create("lastUpdated"), "updatedDate"),
        ).execute()

        val request = server!!.takeRequest()

        assertThat(request.path).isEqualTo("/api?filter=id:in:[uid1,uid2]&filter=lastUpdated:gt:updatedDate")
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun returns_correct_path_when_create_a_retrofit_request_using_filters_and_single_value() {
        testService(server!!).test(
            create(create("id"), listOf("uid1")),
            gt(create("lastUpdated"), "updatedDate"),
        ).execute()

        val request = server!!.takeRequest()

        assertThat(request.path).isEqualTo("/api?filter=id:in:[uid1]&filter=lastUpdated:gt:updatedDate")
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun returns_correct_path_when_create_a_retrofit_request_using_filters_and_values() {
        mixedTestService(server!!).test(
            builder<String>().fields(
                create("id"),
                create("code"),
                create("name"),
                create("displayName"),
            ).build(),
            create(create("id"), listOf("uid1", "uid2")),
            gt(create("lastUpdated"), "updatedDate"),
        ).execute()

        val request = server!!.takeRequest()

        assertThat(request.path).isEqualTo(
            "/api?field=id,code,name,displayName&filter=id:in:[uid1,uid2]&filter=lastUpdated:gt:updatedDate",
        )
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun returns_correct_path_when_create_a_retrofit_request_ignoring_null_filter() {
        testService(server!!).test(
            create(create("id"), listOf("uid1", "uid2")),
            null,
        ).execute()

        val request = server!!.takeRequest()

        assertThat(request.path).isEqualTo("/api?filter=id:in:[uid1,uid2]")
    }

    private fun testService(mockWebServer: MockWebServer): TestService {
        return RetrofitFactory.fromMockWebServer(mockWebServer).create(
            TestService::class.java,
        )
    }

    private fun mixedTestService(mockWebServer: MockWebServer): MixedTestService {
        return RetrofitFactory.fromMockWebServer(mockWebServer).create(
            MixedTestService::class.java,
        )
    }

    @Test
    fun returns_converter_factory_on_correct_annotation() {
        val retrofit = RetrofitFactory.fromMockWebServer(MockWebServer())
        val annotations = arrayOf<Annotation>(Where())
        val converter = FilterConverterFactory().stringConverter(String::class.java, annotations, retrofit)

        assertThat(converter).isInstanceOf(FilterConverter::class.java)
    }

    @Test
    fun returns_null_on_missing_annotation() {
        val retrofit = RetrofitFactory.fromMockWebServer(MockWebServer())
        val annotations = emptyArray<Annotation>()
        val converter = FilterConverterFactory().stringConverter(String::class.java, annotations, retrofit)

        assertThat(converter).isNull()
    }

    companion object {
        private var server: MockWebServer? = null

        @JvmStatic
        @BeforeClass
        @Throws(IOException::class)
        fun setUpClass() {
            server = MockWebServer()
            server!!.start()
        }

        @JvmStatic
        @AfterClass
        @Throws(IOException::class)
        fun tearDownClass() {
            server!!.shutdown()
        }
    }
}
