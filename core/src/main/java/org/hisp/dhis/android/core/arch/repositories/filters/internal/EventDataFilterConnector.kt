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
package org.hisp.dhis.android.core.arch.repositories.filters.internal

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedRepositoryFilterFactory
import org.hisp.dhis.android.core.event.EventDataFilter

class EventDataFilterConnector<R : BaseRepository> internal constructor(
    private val key: String,
    private val repositoryFactory: ScopedRepositoryFilterFactory<R, EventDataFilter>
) {
    fun eq(value: String): R {
        val filter = EventDataFilter.builder().dataItem(key).eq(value).build()
        return repositoryFactory.updated(filter)
    }

    fun le(value: String): R {
        val filter = EventDataFilter.builder().dataItem(key).le(value).build()
        return repositoryFactory.updated(filter)
    }

    fun lt(value: String): R {
        val filter = EventDataFilter.builder().dataItem(key).lt(value).build()
        return repositoryFactory.updated(filter)
    }

    fun ge(value: String): R {
        val filter = EventDataFilter.builder().dataItem(key).ge(value).build()
        return repositoryFactory.updated(filter)
    }

    fun gt(value: String): R {
        val filter = EventDataFilter.builder().dataItem(key).gt(value).build()
        return repositoryFactory.updated(filter)
    }

    fun `in`(value: Set<String>): R {
        val filter = EventDataFilter.builder().dataItem(key).`in`(value).build()
        return repositoryFactory.updated(filter)
    }

    fun like(value: String): R {
        val filter = EventDataFilter.builder().dataItem(key).like(value).build()
        return repositoryFactory.updated(filter)
    }
}