/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import android.database.Cursor;

import org.hisp.dhis.android.core.common.ModelAbstractShould;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED_STR;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeReservedValueModelShould extends
        ModelAbstractShould<TrackedEntityAttributeReservedValueModel> {
    private final static String OWNER_OBJECT = "ownerObject";
    private final static String OWNER_UID = "ownerUid";
    private final static String KEY = "key";
    private final static String VALUE = "value";
    private final static String ORGANISATION_UNIT = "orgUnitUid";

    public TrackedEntityAttributeReservedValueModelShould() {
        super(new TrackedEntityAttributeReservedValueModel.Columns().all(), 8);
    }

    @Override
    protected TrackedEntityAttributeReservedValueModel cursorToModel(Cursor cursor) {
        return TrackedEntityAttributeReservedValueModel.create(cursor);
    }

    @Override
    protected TrackedEntityAttributeReservedValueModel buildModel() {
        TrackedEntityAttributeReservedValueModel.Builder modelBuilder =
                TrackedEntityAttributeReservedValueModel.builder();
        modelBuilder
                .ownerObject(OWNER_OBJECT)
                .ownerUid(OWNER_UID)
                .key(KEY)
                .value(VALUE)
                .created(CREATED)
                .expiryDate(CREATED)
                .organisationUnit(ORGANISATION_UNIT)
                .temporalValidityDate(CREATED);
        return modelBuilder.build();
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getModelAsObjectArray(model),
                model.ownerObject(), model.ownerUid(), model.key(), model.value(), CREATED_STR, CREATED_STR,
                ORGANISATION_UNIT, CREATED_STR);
    }

    @Test
    public void have_tea_reserved_value_columns() {
        List<String> columnsList = Arrays.asList(new TrackedEntityAttributeReservedValueModel.Columns().all());

        assertThat(columnsList.contains(Columns.OWNER_OBJECT)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.OWNER_UID)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.KEY)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.VALUE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.CREATED)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.EXPIRY_DATE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.ORGANISATION_UNIT)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.TEMPORAL_VALIDITY_DATE)).isEqualTo(true);
    }
}
