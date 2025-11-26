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
package org.hisp.dhis.android.core.arch.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.DateUtils.KTX_DATE_FORMAT
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toKtxInstant
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
import java.util.Date

/**
 * Utility object for converting dates between client and server timezones.
 *
 * These conversions are applied only in the network layer (DTOs) to maintain
 * backward compatibility with domain and persistence layers.
 */
internal object DateTimezoneConverter {
    lateinit var serverTimezoneManager: ServerTimezoneManager

    /**
     * Converts a date from client timezone to server timezone.
     * Uses the cached server timezone from ServerTimezoneManager.
     *
     * @param date The date in client timezone
     * @return Date converted to server timezone context
     */
    fun convertClientToServer(date: Date): String? {
        if (!::serverTimezoneManager.isInitialized) {
            serverTimezoneManager = koin.get()
        }

        val serverTimeZone = serverTimezoneManager.getServerTimeZone()

        val localDateTime = date.toKtxInstant().toLocalDateTime(serverTimeZone)
        val dateString = localDateTime.format(KTX_DATE_FORMAT)
        return dateString
    }

    /**
     * Converts a date from server timezone to client timezone.
     * Uses the cached server timezone from ServerTimezoneManager.
     *
     * @param dateString The date string in server timezone
     * @return Date converted to client timezone context
     */
    fun convertServerToClient(dateString: String?): Date? {
        if (!::serverTimezoneManager.isInitialized) {
            serverTimezoneManager = koin.get()
        }

        val serverTimeZone = serverTimezoneManager.getServerTimeZone()

        return dateString?.let {
            val localDateTime = LocalDateTime.parse(it, KTX_DATE_FORMAT)
            val fromInstant = localDateTime.toInstant(serverTimeZone)
            fromInstant.toJavaDate()
        }
    }

    /**
     * Converts a date from server timezone to client timezone.
     * Uses the cached server timezone from ServerTimezoneManager.
     *
     * @param dateString The date string in server timezone
     * @return Date as String converted to client timezone context
     */
    fun convertServerToClientAsString(dateString: String?): String? {
        val convertedDate = convertServerToClient(dateString)
        return convertedDate?.let { DateUtils.DATE_FORMAT.format(it) }
    }
}
