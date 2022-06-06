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
import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.*
import org.junit.Test

class TrackedEntityTypeShould : BaseObjectShould("trackedentity/tracked_entity_type.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val entityType = objectMapper.readValue(jsonStream, TrackedEntityType::class.java)

        assertThat(entityType.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2014-08-20T12:28:56.409"))
        assertThat(entityType.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-10-14T13:36:53.063"))
        assertThat(entityType.uid()).isEqualTo("nEenWmSyUEp")
        assertThat(entityType.name()).isEqualTo("Person")
        assertThat(entityType.displayName()).isEqualTo("Person")
        assertThat(entityType.description()).isEqualTo("Person")
        assertThat(entityType.displayDescription()).isEqualTo("Person")
        assertThat(entityType.featureType()).isEqualTo(FeatureType.NONE)
        assertThat(entityType.access()).isEqualTo(AccessHelper.createForDataWrite(true))
    }
}
