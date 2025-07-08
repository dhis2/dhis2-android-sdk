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
package org.hisp.dhis.android.testapp.fileresource

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class FileResourceCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val fileResources = d2.fileResourceModule().fileResources()
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_uid() {
        val fileResources = d2.fileResourceModule().fileResources()
            .byUid().eq("befryEfXge5")
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_name() {
        val fileResources = d2.fileResourceModule().fileResources()
            .byName().eq("profile.png")
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated() {
        val fileResources = d2.fileResourceModule().fileResources()
            .byLastUpdated().after(DateUtils.DATE_FORMAT.parse("2007-12-24T12:24:25.203"))
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_content_type() {
        val fileResources = d2.fileResourceModule().fileResources()
            .byContentType().eq("image/png")
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_path() {
        val fileResources = d2.fileResourceModule().fileResources()
            .byPath().like("files/sdk_resources")
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_state() {
        val fileResources = d2.fileResourceModule().fileResources()
            .bySyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }

    @Test
    fun filter_by_content_length() {
        val fileResources = d2.fileResourceModule().fileResources()
            .byContentLength().eq(9270L)
            .blockingGet()

        assertThat(fileResources.size).isEqualTo(1)
    }
}
