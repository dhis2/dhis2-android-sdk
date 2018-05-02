/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ModelBuilderAbstractShould;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.period.FeatureType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;


@RunWith(JUnit4.class)
public class TrackedEntityInstanceModelBuilderShould extends ModelBuilderAbstractShould<
        TrackedEntityInstance, TrackedEntityInstanceModel> {

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Override
    protected TrackedEntityInstance buildPojo() {
        return TrackedEntityInstance.create(
                UID,
                CREATED,
                LAST_UPDATED,
                "createdAtClient",
                "lastUpdatedAtClient",
                "orgUnit",
                "type",
                "10N,34E",
                FeatureType.MULTI_POLYGON,
                false,
                null,
                null,
                null);
    }

    @Override
    protected ModelBuilder<TrackedEntityInstance, TrackedEntityInstanceModel> modelBuilder() {
        return new TrackedEntityInstanceModelBuilder();
    }

    @Test
    public void copy_pojo_tracked_entity_instance_properties() {
        assertThat(model.uid()).isEqualTo(pojo.uid());
        assertThat(model.created()).isEqualTo(pojo.created());
        assertThat(model.lastUpdated()).isEqualTo(pojo.lastUpdated());
        assertThat(model.createdAtClient()).isEqualTo(pojo.createdAtClient());
        assertThat(model.lastUpdatedAtClient()).isEqualTo(pojo.lastUpdatedAtClient());
        assertThat(model.organisationUnit()).isEqualTo(pojo.organisationUnit());
        assertThat(model.trackedEntityType()).isEqualTo(pojo.trackedEntityType());
        assertThat(model.coordinates()).isEqualTo(pojo.coordinates());
        assertThat(model.featureType()).isEqualTo(pojo.featureType());
    }

    @Test
    public void set_state_automatically_to_synced() {
        assertThat(model.state()).isEqualTo(State.SYNCED);
    }
}
