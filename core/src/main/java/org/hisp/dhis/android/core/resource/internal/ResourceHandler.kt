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
package org.hisp.dhis.android.core.resource.internal

import androidx.annotation.VisibleForTesting
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
internal class ResourceHandler(private val resourceStore: ResourceStore) {
    internal var serverDate: Date? = null

    fun setServerDate(serverDate: Date) {
        this.serverDate = Date(serverDate.time)
    }

    fun getServerDate(): Date {
        return Date(serverDate!!.time)
    }

    fun handleResource(resourceType: Resource.Type?) {
        if (resourceType == null || serverDate == null) {
            return
        }
        val resource = Resource.builder()
            .resourceType(resourceType)
            .lastSynced(serverDate)
            .build()

        resourceStore.updateOrInsertWhere(resource)
    }

    /**
     * A wrapper to expose resourceStore.getLastUpdated(str).
     *
     * @param type Type of the resource.
     * @return a string representing the last synced date
     */
    fun getLastUpdated(type: Resource.Type?): String? {
        return resourceStore.getLastUpdated(type!!)
    }

    companion object {
        @VisibleForTesting
        fun create(databaseAdapter: DatabaseAdapter): ResourceHandler {
            return ResourceHandler(ResourceStoreImpl(databaseAdapter))
        }
    }
}
