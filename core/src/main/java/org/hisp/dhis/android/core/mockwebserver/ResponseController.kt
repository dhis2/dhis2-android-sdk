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

class ResponseController internal constructor() {
    private var methodsMap: MutableMap<String, LinkedHashMap<String?, String>>? = null
    private var codeResponses: MutableMap<String, Int>? = null
    private var contentTypeMap: MutableMap<String, String>? = null

    init {
        initMaps()
    }

    private fun initMaps() {
        codeResponses = mutableMapOf()
        contentTypeMap = mutableMapOf()
        methodsMap = mutableMapOf()
        methodsMap?.put(GET, LinkedHashMap())
        methodsMap?.put(POST, LinkedHashMap())
        methodsMap?.put(PUT, LinkedHashMap())
        methodsMap?.put(DELETE, LinkedHashMap())
    }

    fun populateInternalResponses() {
        // move sdk dispatcher here
    }

    fun addResponse(
        method: String,
        path: String,
        responseName: String,
        responseCode: Int,
        contentType: String,
    ) {
        var resourcesMap = methodsMap!!.get(method)!!
        resourcesMap.put(path, responseName)
        codeResponses!!.put(responseName, responseCode)
        contentTypeMap!!.put(responseName, contentType)
    }

    fun getBody(method: String, currentPath: String?): String? {
        var resourcesMap: Map<String?, String> = methodsMap?.get(method) ?: return null
        var filename: String? = ""

        val paths: List<String?> = resourcesMap.keys.toList().asReversed()
        for (path in paths) {
            filename = findResponse(resourcesMap, path, currentPath)
            if (!filename!!.isEmpty()) {
                break
            }
        }
        return filename
    }

    private fun findResponse(
        resourcesMap: Map<String?, String>,
        path: String?,
        currentPath: String?,
    ): String? {
        var filename: String? = ""
        val pattern = path?.toRegex() ?: return filename

        if (currentPath != null && pattern.matches(currentPath)) {
            filename = resourcesMap[path]
        }
        return filename
    }

    @Suppress("TooGenericExceptionThrown")
    fun getCode(resource: String?): Int {
        if (resource == null || resource.isEmpty()) {
            throw RuntimeException("Resource not not found")
        }
        return codeResponses!!.get(resource)!!
    }

    @Suppress("TooGenericExceptionThrown")
    fun getContentType(resource: String?): String? {
        if (resource == null || resource.isEmpty()) {
            throw RuntimeException("Resource not not found")
        }
        return contentTypeMap!!.get(resource)
    }

    companion object {
        const val GET: String = "GET"
        const val POST: String = "POST"
        const val PUT: String = "PUT"
        const val DELETE: String = "DELETE"

        const val API_ME_PATH: String = "/api/me?.*"
        const val API_SYSTEM_INFO_PATH: String = "/api/system/info?.*"
    }
}
