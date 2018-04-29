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

package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.NameableModelBuilderAbstractShould;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DESCRIPTION;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_DESCRIPTION;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.DISPLAY_SHORT_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.SHORT_NAME;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;

@RunWith(JUnit4.class)
public class CategoryOptionModelBuilderShould extends
        NameableModelBuilderAbstractShould<CategoryOption, CategoryOptionModel> {

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        super.setUp();
    }

    @Override
    protected CategoryOption buildPojo() {
        return CategoryOption.create(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,

                CREATED,
                CREATED
        );
    }

    @Override
    protected ModelBuilder<CategoryOption, CategoryOptionModel> modelBuilder() {
        return new CategoryOptionModelBuilder();
    }

    @Test
    public void copy_pojo_legend_properties() {
        assertThat(model.startDate()).isEqualTo(pojo.startDate());
        assertThat(model.endDate()).isEqualTo(pojo.endDate());
    }
}
