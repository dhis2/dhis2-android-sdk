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

import android.os.Build
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import io.ktor.util.*
import org.hisp.dhis.android.BuildConfig
import org.hisp.dhis.android.core.D2Configuration

public class UserAgentPlugin private constructor(public val agent: String) {

    @KtorDsl
    public class Config {
        public lateinit var d2Configuration: D2Configuration
        var userAgent: String? = null
            private set

        internal fun generateUserAgent() {
            userAgent = String.format(
                "%s/%s/%s/Android_%s",
                d2Configuration.appName(),
                BuildConfig.VERSION_NAME, // SDK version
                d2Configuration.appVersion(),
                Build.VERSION.SDK_INT, // Android Version
            )
        }
    }

    public companion object Plugin : HttpClientPlugin<Config, UserAgentPlugin> {
        override val key: AttributeKey<UserAgentPlugin> = AttributeKey("CustomUserAgent")

        override fun prepare(block: Config.() -> Unit): UserAgentPlugin {
            val config = Config().apply(block)
            config.generateUserAgent()
            return UserAgentPlugin(config.userAgent ?: "Ktor http-client")
        }

        override fun install(plugin: UserAgentPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                context.headers.remove(HttpHeaders.UserAgent) // Remove any existing User-Agent header
                context.header(HttpHeaders.UserAgent, plugin.agent)
            }
        }
    }
}
