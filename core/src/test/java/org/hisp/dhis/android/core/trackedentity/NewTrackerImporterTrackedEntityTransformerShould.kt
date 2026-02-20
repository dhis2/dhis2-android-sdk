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
package org.hisp.dhis.android.core.trackedentity

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext
import org.hisp.dhis.android.core.arch.helpers.DateTimezoneConverter
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.category.internal.DefaultCategoryComboManager
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
import org.hisp.dhis.android.network.trackedentityinstance.TrackedEntityInstanceDTO
import org.hisp.dhis.android.network.tracker.NewTrackedEntityDTO
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class NewTrackerImporterTrackedEntityTransformerShould {

    private val jsonParser: Json = KotlinxJsonParser.instance

    @Before
    fun setUpKoin() {
        try {
            DhisAndroidSdkKoinContext.koin
        } catch (_: Exception) {
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

        if (DhisAndroidSdkKoinContext.koin.getOrNull<DefaultCategoryComboManager>() == null) {
            val defaultCategoryComboManager: DefaultCategoryComboManager = mock {
                on { defaultCategoryOptionComboUid } doReturn "HllvX50cXC0"
            }
            DhisAndroidSdkKoinContext.koin.loadModules(
                listOf(module { single { defaultCategoryComboManager } }),
            )
        }
    }

    @Test
    fun detransform_tracked_entity() {
        val oldJson = this.javaClass.classLoader
            ?.getResourceAsStream("trackedentity/new_tracker_transformer_old_tracked_entity.json")

        val newJson = this.javaClass.classLoader
            ?.getResourceAsStream("trackedentity/new_tracker_transformer_new_tracked_entity.json")

        val oldTrackedEntity = jsonParser.decodeFromStream(TrackedEntityInstanceDTO.serializer(), oldJson!!).toDomain()
        val newTrackedEntity = jsonParser.decodeFromStream(NewTrackedEntityDTO.serializer(), newJson!!)

        val transformedTrackedEntity = newTrackedEntity.toDomain()

        assertThat(transformedTrackedEntity).isEqualTo(oldTrackedEntity)
    }
}
