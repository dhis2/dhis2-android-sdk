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
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeReservedValueModelBuilderShould extends ModelBuilderAbstractShould<
    TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueModel> {

    @Mock
    private OrganisationUnit organisationUnit;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        when(organisationUnit.uid()).thenReturn("orgUnitUid");
        MockitoAnnotations.initMocks(this);
    }

    protected TrackedEntityAttributeReservedValue buildPojo() {
        return TrackedEntityAttributeReservedValue.create(
                "ownerObject",
                "ownerUid",
                "key",
                "value",
                CREATED,
                CREATED
        );
    }

    @Override
    protected ModelBuilder<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueModel> modelBuilder() {
        return new TrackedEntityAttributeReservedValueModelBuilder(organisationUnit,  "pattern");
    }

    @Test
    public void copy_pojo_reserved_value_properties() {
        assertThat(model.ownerObject()).isEqualTo(pojo.ownerObject());
        assertThat(model.ownerUid()).isEqualTo(pojo.ownerUid());
        assertThat(model.key()).isEqualTo(pojo.key());
        assertThat(model.value()).isEqualTo(pojo.value());
        assertThat(model.created()).isEqualTo(pojo.created());
        assertThat(model.expiryDate()).isEqualTo(pojo.expiryDate());
        assertThat(model.organisationUnit()).isEqualTo(organisationUnit.uid());
        assertThat(model.temporalValidityDate()).isEqualTo(null);
    }
}
