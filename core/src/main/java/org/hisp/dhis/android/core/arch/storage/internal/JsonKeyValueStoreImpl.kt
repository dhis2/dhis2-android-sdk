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
package org.hisp.dhis.android.core.arch.storage.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import java.io.IOException

@Suppress("TooGenericExceptionThrown")
internal open class JsonKeyValueStoreImpl<T>(
    private val secureStore: KeyValueStore,
    private val key: String,
    private val serializer: KSerializer<T>,
) : ObjectKeyValueStore<T> {
    private var value: T? = null

    override fun set(t: T) {
        try {
            val strObject = KotlinxJsonParser.instance.encodeToString(serializer, t)
            secureStore.setData(key, strObject)
            this.value = t
        } catch (e: SerializationException) {
            throw RuntimeException("Couldn't persist object in key value store")
        }
    }

    override fun get(): T? {
        val strObject = secureStore.getData(key)
        return when {
            strObject == null -> null
            this.value != null -> this.value
            else -> {
                val parsedValue = try {
                    KotlinxJsonParser.instance.decodeFromString(serializer, strObject)
                } catch (e: IOException) {
                    throw RuntimeException("Couldn't read object from key value store")
                }
                @Suppress("UNCHECKED_CAST")
                this.value = parsedValue
                return this.value
            }
        }
    }

    override fun remove() {
        this.value = null
        secureStore.removeData(key)
    }
}
