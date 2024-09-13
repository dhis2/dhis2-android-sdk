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

package org.hisp.dhis.android.core.arch.api.internal

import android.content.Context
import okhttp3.TlsVersion
import org.hisp.dhis.android.R
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

internal object TrustFactory {
    fun getTrustFactoryManager(context: Context): Pair<SSLSocketFactory, X509TrustManager> {
        val cf = CertificateFactory.getInstance("X.509")

        val isrgRoot1Input = context.resources.openRawResource(R.raw.isrgrootx1)
        val isrgRoot1Certificate: Certificate = isrgRoot1Input.use {
            cf.generateCertificate(it)
        }

        val isrgRoot2Input = context.resources.openRawResource(R.raw.isrgrootx2)
        val isrgRoot2Certificate: Certificate = isrgRoot2Input.use {
            cf.generateCertificate(it)
        }

        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("isrgrootx1", isrgRoot1Certificate)
            setCertificateEntry("isrgrootx2", isrgRoot2Certificate)
        }

        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }

        val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName).apply {
            init(null, tmf.trustManagers, null)
        }

        return Pair(sslContext.socketFactory, tmf.trustManagers[0] as X509TrustManager)
    }
}
