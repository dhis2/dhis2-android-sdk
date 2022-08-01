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
package org.hisp.dhis.android.core.arch.storage.internal

import javax.inject.Inject
import javax.inject.Singleton

private const val SIZE_IN_CHARACTERS = 200

@Singleton
class ChunkedSecureStore @Inject constructor(private val internalStore: SecureStore) : SecureStore {

    override fun setData(key: String, data: String?) {
        removeData(key)
        if (data != null) {
            val chunked = data.chunked(SIZE_IN_CHARACTERS)
            setLen(key, chunked.size)
            chunked.forEachIndexed { i, chunk ->
                internalStore.setData(chunkKey(key, i), chunk)
            }
        }
    }

    override fun getData(key: String): String? {
        val len = getLen(key)
        return if (len == null) {
            internalStore.getData(key)
        } else {
            val dataArray = (0 until len).map {
                internalStore.getData(chunkKey(key, it))
            }
            dataArray.joinToString("")
        }
    }

    override fun removeData(key: String) {
        val len = getLen(key)
        if (len == null) {
            internalStore.removeData(key)
        } else {
            (0 until len).forEach {
                internalStore.removeData(chunkKey(key, it))
            }
            internalStore.removeData(lenKey(key))
        }
    }

    private fun getLen(key: String): Int? {
        val lenValStr = internalStore.getData(lenKey(key))
        return lenValStr?.toIntOrNull()
    }

    private fun setLen(key: String, len: Int) {
        internalStore.setData(lenKey(key), len.toString())
    }

    private fun lenKey(key: String): String {
        return "${key}_[LEN]_"
    }

    private fun chunkKey(key: String, i: Int): String {
        return "${key}_[$i]_"
    }
}
