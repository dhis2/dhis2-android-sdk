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

import kotlinx.serialization.SerializationException
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfigurationDAO
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfigurationDAO.Companion.toDao
import org.hisp.dhis.android.core.configuration.internal.migration.DatabasesConfigurationOld
import org.hisp.dhis.android.core.configuration.internal.migration.DatabasesConfigurationOldDAO
import org.hisp.dhis.android.core.configuration.internal.migration.DatabasesConfigurationOldDAO.Companion.toDao
import java.io.IOException

@Suppress("TooGenericExceptionThrown")
internal open class JsonKeyValueStoreImpl<O>(
    private val secureStore: KeyValueStore,
    private val key: String,
    private val clazz: Class<O>,
) : ObjectKeyValueStore<O> {
    private var value: O? = null

    override fun set(o: O) {
        try {
            val strObject = when (o) {
                is DatabasesConfigurationOld ->
                    KotlinxJsonParser.instance.encodeToString(
                        DatabasesConfigurationOldDAO.serializer(),
                        o.toDao(),
                    )

                is DatabasesConfiguration ->
                    KotlinxJsonParser.instance.encodeToString(
                        DatabasesConfigurationDAO.serializer(),
                        o.toDao(),
                    )

                else -> ""
            }
            secureStore.setData(key, strObject)
            this.value = o
        } catch (e: SerializationException) {
            throw RuntimeException("Couldn't persist object in key value store")
        }
    }

    override fun get(): O? {
        val strObject = secureStore.getData(key)
        return when {
            strObject == null -> null
            this.value != null -> this.value
            else -> {
                val parsedValue: Any = try {
                    when (clazz) {
                        DatabasesConfigurationOld::class.java ->
                            KotlinxJsonParser.instance.decodeFromString(
                                DatabasesConfigurationOldDAO.serializer(),
                                strObject,
                            ).toDomain()

                        DatabasesConfiguration::class.java ->
                            KotlinxJsonParser.instance.decodeFromString(
                                DatabasesConfigurationDAO.serializer(),
                                strObject,
                            ).toDomain()

                        else -> throw RuntimeException("Unsupported class type")
                    }
                } catch (e: IOException) {
                    throw RuntimeException("Couldn't read object from key value store")
                }
                @Suppress("UNCHECKED_CAST")
                this.value = parsedValue as O
                return this.value
            }
        }
    }

    override fun remove() {
        this.value = null
        secureStore.removeData(key)
    }
}
