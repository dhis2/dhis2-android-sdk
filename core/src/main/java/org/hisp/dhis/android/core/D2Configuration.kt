/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core

import android.content.Context
import okhttp3.Interceptor
import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.api.NetworkPlugin

@ModelBuilder
data class D2Configuration(
    val appName: String?,
    val appVersion: String?,
    val readTimeoutInSeconds: Int,
    val connectTimeoutInSeconds: Int,
    val writeTimeoutInSeconds: Int,
    val interceptors: List<Interceptor>,
    val networkInterceptors: List<Interceptor>,
    val networkPlugins: List<NetworkPlugin<Any, Any>>,
    val context: Context,
) {
    fun appName(): String? = appName
    fun appVersion(): String? = appVersion
    fun readTimeoutInSeconds(): Int = readTimeoutInSeconds
    fun connectTimeoutInSeconds(): Int = connectTimeoutInSeconds
    fun writeTimeoutInSeconds(): Int = writeTimeoutInSeconds
    fun interceptors(): List<Interceptor> = interceptors
    fun networkInterceptors(): List<Interceptor> = networkInterceptors
    fun networkPlugins(): List<NetworkPlugin<Any, Any>> = networkPlugins
    fun context(): Context = context

    fun toBuilder(): Builder = D2ConfigurationBuilder.from(this)

    class Builder : D2ConfigurationBuilder()

    companion object {
        const val READ_TIMEOUT_IN_SECONDS_DEFAULT = 30
        const val CONNECT_TIMEOUT_IN_SECONDS_DEFAULT = 30
        const val WRITE_TIMEOUT_IN_SECONDS_DEFAULT = 30

        @JvmStatic
        fun builder(): Builder = Builder()
            .readTimeoutInSeconds(READ_TIMEOUT_IN_SECONDS_DEFAULT)
            .connectTimeoutInSeconds(CONNECT_TIMEOUT_IN_SECONDS_DEFAULT)
            .writeTimeoutInSeconds(WRITE_TIMEOUT_IN_SECONDS_DEFAULT)
            .interceptors(emptyList())
            .networkInterceptors(emptyList())
            .networkPlugins(emptyList())
    }
}
