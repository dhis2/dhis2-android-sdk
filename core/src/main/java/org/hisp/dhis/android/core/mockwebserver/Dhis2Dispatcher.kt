/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.mockwebserver

import android.util.Log
import io.ktor.http.HttpStatusCode
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hisp.dhis.android.core.arch.file.IFileReader
import java.io.IOException

class Dhis2Dispatcher internal constructor(
    private val fileReader: IFileReader,
    private val responseController: ResponseController,
) : Dispatcher() {
    fun configInternalResponseController() {
        responseController.populateInternalResponses()
    }

    @Throws(InterruptedException::class)
    public override fun dispatch(request: RecordedRequest): MockResponse {
        val method = request.method!!.uppercase()
        val path = request.path

        val fileName = responseController.getBody(method, path)
        val httpCode = responseController.getCode(fileName)
        val contentType = responseController.getContentType(fileName)

        try {
            val body = fileReader.getStringFromFile(fileName)
            Log.i(DISPATCHER, String.format(method, path, body))
            return MockResponse()
                .setBody(body!!)
                .setResponseCode(httpCode)
                .setHeader("Content-Type", contentType!!)
        } catch (e: IOException) {
            return MockResponse().setResponseCode(HttpStatusCode.InternalServerError.value)
                .setBody("Error reading JSON file for MockServer")
        }
    }

    fun addResponse(
        method: String,
        path: String,
        responseName: String,
        responseCode: Int,
        contentType: String,
    ) {
        responseController.addResponse(method, path, responseName, responseCode, contentType)
    }

    companion object {
        private const val DISPATCHER = "Dispatcher"
    }
}
