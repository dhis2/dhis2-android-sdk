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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.ObjectShould
import org.junit.Test

class TrackedEntityInstanceShould : BaseObjectShould("trackedentity/tracked_entity_instance.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val trackedEntityInstance = objectMapper.readValue(jsonStream, TrackedEntityInstance::class.java)

        assertThat(trackedEntityInstance.lastUpdated()).isEqualTo(
            DateUtils.DATE_FORMAT.parse("2015-10-15T11:32:27.242")
        )
        assertThat(trackedEntityInstance.created()).isEqualTo(
            DateUtils.DATE_FORMAT.parse("2014-06-06T20:44:21.375")
        )

        assertThat(trackedEntityInstance.uid()).isEqualTo("PgmUFEQYZdt")
        assertThat(trackedEntityInstance.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(trackedEntityInstance.trackedEntityType()).isEqualTo("nEenWmSyUEp")
        assertThat(trackedEntityInstance.geometry()!!.type()).isEqualTo(FeatureType.POINT)
        assertThat(trackedEntityInstance.geometry()!!.coordinates()).isEqualTo("[9.0, 9.0]")
        assertThat(trackedEntityInstance.deleted()).isFalse()

        assertThat(trackedEntityInstance.trackedEntityAttributeValues()!![0].trackedEntityAttribute())
            .isEqualTo("cejWyOfXge6")

        assertThat(trackedEntityInstance.programOwners()?.size).isEqualTo(1)
        assertThat(trackedEntityInstance.programOwners()?.first()?.trackedEntityInstance()).isEqualTo("PgmUFEQYZdt")
        assertThat(trackedEntityInstance.programOwners()?.first()?.program()).isEqualTo("lxAQ7Zs9VYR")
        assertThat(trackedEntityInstance.programOwners()?.first()?.ownerOrgUnit()).isEqualTo("DiszpKrYNg8")
    }
}
