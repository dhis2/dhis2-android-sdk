/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.common

import kotlinx.datetime.TimeZone
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext
import org.hisp.dhis.android.core.arch.helpers.DateTimezoneConverter
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.InputStream

abstract class CoreObjectShould(private val jsonPath: String) {

    private val jsonParser = KotlinxJsonParser.instance

    @Before
    fun setUpKoin() {
        // Initialize Koin if not already initialized to support DateTimezoneConverter
        try {
            DhisAndroidSdkKoinContext.koin
        } catch (_: Exception) {
            // Koin not initialized, initialize with neutral mock
            val neutralMock: ServerTimezoneManager = mock {
                on { getServerTimeZone() } doReturn TimeZone.currentSystemDefault()
            }
            val koinApp = startKoin {
                modules(
                    module {
                        single { neutralMock }
                    },
                )
            }
            DhisAndroidSdkKoinContext.koin = koinApp.koin
            DateTimezoneConverter.serverTimezoneManager = neutralMock
        }
    }

    abstract fun map_from_json_string()

    protected fun <T> deserialize(serializer: kotlinx.serialization.KSerializer<T>): T {
        return deserializePath(jsonPath, serializer)
    }

    protected fun <T> deserialize(jsonString: String, serializer: kotlinx.serialization.KSerializer<T>): T {
        return jsonParser.decodeFromString(serializer, jsonString)
    }

    protected fun <T> deserializePath(path: String, serializer: kotlinx.serialization.KSerializer<T>): T {
        val jsonString = getStringValueFromFile(path)
        return jsonParser.decodeFromString(serializer, jsonString)
    }

    protected fun <T> serialize(value: T, serializer: kotlinx.serialization.KSerializer<T>): String {
        return jsonParser.encodeToString(serializer, value)
    }

    protected fun getStringValueFromFile(): String {
        return getStringValueFromFile(jsonPath)
    }

    private fun getStringValueFromFile(path: String): String {
        val jsonStream: InputStream = this::class.java.classLoader?.getResourceAsStream(path)
            ?: throw IllegalArgumentException("File not found: $jsonPath")
        return jsonStream.bufferedReader().use { it.readText() }
    }
}
