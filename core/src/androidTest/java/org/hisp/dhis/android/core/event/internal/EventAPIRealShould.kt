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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.junit.Assert
import org.junit.Before
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
abstract class EventAPIRealShould internal constructor(
    // API version dependant parameters
    private val serverUrl: String,
    private val strategy: String,
) : BaseRealIntegrationTest() {
    private val ouMode = OrganisationUnitMode.ACCESSIBLE.name
    private lateinit var apiCallExecutor: CoroutineAPICallExecutor
    private lateinit var eventService: EventService

    @Before
    override fun setUp() {
        super.setUp()
        apiCallExecutor = d2.coroutineAPICallExecutor()
        eventService = EventService(d2.httpServiceClient())
    }

    // @Test
    @Throws(Exception::class)
    fun valid_events() = runTest {
        login()
        val validEvent1 = EventUtils.createValidEvent()
        val validEvent2 = EventUtils.createValidEvent()
        val payload = EventPayload()
        payload.events = listOf(validEvent1, validEvent2)

        val response = postPayload(payload).getOrThrow()

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validEvent1.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            } else if (validEvent2.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            }
        }

        // Check server status
        val serverValidEvent1 = apiCallExecutor.wrap {
            eventService.getEvent(validEvent1.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        val serverValidEvent2 = apiCallExecutor.wrap {
            eventService.getEvent(validEvent2.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        assertThat(serverValidEvent1).isNotNull()
        assertThat(serverValidEvent2).isNotNull()
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_invalid_orgunit() = runTest {
        login()
        val validEvent = EventUtils.createValidEvent()
        val invalidEvent = EventUtils.createEventWithInvalidOrgunit()
        val payload = EventPayload()
        payload.events = listOf(validEvent, invalidEvent)

        val response = postPayload(payload).getOrThrow()

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.ERROR)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            } else if (invalidEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.ERROR)
            }
        }

        // Check server status
        val serverValidEvent = apiCallExecutor.wrap {
            eventService.getEvent(validEvent.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        assertThat(serverValidEvent).isNotNull()

        try {
            apiCallExecutor.wrap {
                eventService.getEvent(
                    invalidEvent.uid(),
                    EventFields.allFields,
                    ouMode,
                )
            }.getOrThrow()
            Assert.fail("Should not reach that line")
        } catch (e: D2Error) {
            assertThat(e.httpErrorCode()).isEqualTo(404)
        }
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_invalid_attribute_option_combo() = runTest {
        login()
        val validEvent = EventUtils.createValidEvent()
        val invalidEvent = EventUtils.createEventWithInvalidAttributeOptionCombo()
        val payload = EventPayload()
        payload.events = listOf(validEvent, invalidEvent)

        val response = postPayload(payload).getOrThrow()

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.ERROR)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            } else if (invalidEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.ERROR)
            }
        }

        // Check server status
        val serverValidEvent = apiCallExecutor.wrap {
            eventService.getEvent(validEvent.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        assertThat(serverValidEvent).isNotNull()

        try {
            apiCallExecutor.wrap {
                eventService.getEvent(
                    invalidEvent.uid(),
                    EventFields.allFields,
                    ouMode,
                )
            }.getOrThrow()
            Assert.fail("Should not reach that line")
        } catch (e: D2Error) {
            assertThat(e.httpErrorCode()).isEqualTo(404)
        }
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_future_date_does_not_fail() = runTest {
        login()
        val validEvent1 = EventUtils.createValidEvent()
        val validEvent2 = EventUtils.createEventWithFutureDate()
        val payload = EventPayload()
        payload.events = listOf(validEvent1, validEvent2)

        val response = postPayload(payload).getOrThrow()

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validEvent1.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            } else if (validEvent2.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            }
        }

        // Check server status
        val serverValidEvent1 = apiCallExecutor.wrap {
            eventService.getEvent(validEvent1.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        val serverValidEvent2 = apiCallExecutor.wrap {
            eventService.getEvent(validEvent2.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        assertThat(serverValidEvent1).isNotNull()
        assertThat(serverValidEvent2).isNotNull()
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_invalid_program() = runTest {
        login()
        val validEvent = EventUtils.createValidEvent()
        val invalidEvent = EventUtils.createEventWithInvalidProgram()
        val payload = EventPayload()
        payload.events = listOf(validEvent, invalidEvent)

        val response = postPayload(payload).getOrThrow()

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.ERROR)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            } else if (invalidEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.ERROR)
            }
        }

        // Check server status
        val serverValidEvent = apiCallExecutor.wrap {
            eventService.getEvent(validEvent.uid(), EventFields.allFields, ouMode)
        }.getOrThrow()

        assertThat(serverValidEvent).isNotNull()

        try {
            apiCallExecutor.wrap {
                eventService.getEvent(
                    invalidEvent.uid(),
                    EventFields.allFields,
                    ouMode,
                )
            }.getOrThrow()
            Assert.fail("Should not reach that line")
        } catch (e: D2Error) {
            assertThat(e.httpErrorCode()).isEqualTo(404)
        }
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_invalid_data_values() = runTest {
        login()
        val validEvent = EventUtils.createValidEvent()
        val invalidEvent = EventUtils.createEventWithInvalidDataValues()
        val payload = EventPayload()
        payload.events = listOf(validEvent, invalidEvent)

        val response = postPayload(payload).getOrThrow()

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.WARNING)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.SUCCESS)
            } else if (invalidEvent.uid() == importSummary.reference()) {
                EventUtils.assertEvent(importSummary, ImportStatus.WARNING)
            }
        }

        // Check server status
        val serverValidEvent = apiCallExecutor.wrap {
            eventService.getEvent(
                validEvent.uid(),
                EventFields.allFields,
                ouMode,
            )
        }.getOrThrow()

        val serverInvalidEvent = apiCallExecutor.wrap {
            eventService.getEvent(
                invalidEvent.uid(),
                EventFields.allFields,
                ouMode,
            )
        }.getOrThrow()

        assertThat(serverValidEvent).isNotNull()
        assertThat(serverValidEvent.trackedEntityDataValues()!!.size).isEqualTo(2)
        assertThat(serverInvalidEvent).isNotNull()
        assertThat(serverInvalidEvent.trackedEntityDataValues()).isNull()
    }

    private suspend fun postPayload(payload: EventPayload): Result<EventWebResponse, D2Error> {
        return apiCallExecutor.wrap(
            storeError = false,
            acceptedErrorCodes = listOf(409),
            errorClass = EventWebResponse::class.java,
        ) {
            eventService.postEvents(payload, strategy)
        }
    }

    private fun login() {
        d2.userModule().logIn(username, password, url, null).blockingGet()
    }
}
