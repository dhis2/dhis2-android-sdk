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
package org.hisp.dhis.android.core.trackedentity

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class TrackedEntityTypeCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_all_tracked_entity_types_without_children() {
        val trackedEntityTypes = d2.trackedEntityModule().trackedEntityTypes().blockingGet()

        assertThat(trackedEntityTypes.size).isEqualTo(1)
    }

    @Test
    fun include_attributes_as_children() {
        val trackedEntityType = d2.trackedEntityModule().trackedEntityTypes()
            .withTrackedEntityTypeAttributes().one().blockingGet()

        assertThat(trackedEntityType!!.trackedEntityTypeAttributes()!!.size).isEqualTo(1)
    }

    @Test
    fun filter_by_feature_type() {
        val trackedEntityTypes = d2.trackedEntityModule().trackedEntityTypes()
            .byFeatureType().eq(FeatureType.NONE)
            .blockingGet()

        assertThat(trackedEntityTypes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_color() {
        val trackedEntityTypes = d2.trackedEntityModule().trackedEntityTypes()
            .byColor().eq("#000")
            .blockingGet()

        assertThat(trackedEntityTypes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_icon() {
        val trackedEntityTypes = d2.trackedEntityModule().trackedEntityTypes()
            .byIcon().eq("my-tracked-entity-attribute-icon-name")
            .blockingGet()

        assertThat(trackedEntityTypes.size).isEqualTo(1)
    }
}
