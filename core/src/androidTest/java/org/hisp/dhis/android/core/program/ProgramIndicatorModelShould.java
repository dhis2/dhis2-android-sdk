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

package org.hisp.dhis.android.core.program;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.NameableModelAbstractShould;
import org.hisp.dhis.android.core.program.ProgramIndicatorModel.Columns;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableModelProperties;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorModelShould extends NameableModelAbstractShould<ProgramIndicatorModel> {
    private static final Boolean DISPLAY_IN_FORM = true;
    private static final String EXPRESSION = "test_expression";
    private static final String DIMENSION_ITEM = "test_dimension_item";
    private static final String FILTER = "test_filter";
    private static final Integer DECIMALS = 3;
    private static final String PROGRAM = "program_uid";

    public ProgramIndicatorModelShould() {
        super(new ProgramIndicatorModel.Columns().all(), 16);
    }

    @Override
    protected ProgramIndicatorModel buildModel() {
        ProgramIndicatorModel.Builder programIndicatorModelBuilder = ProgramIndicatorModel.builder();
        fillNameableModelProperties(programIndicatorModelBuilder);
        programIndicatorModelBuilder
                .displayInForm(DISPLAY_IN_FORM)
                .expression(EXPRESSION)
                .dimensionItem(DIMENSION_ITEM)
                .filter(FILTER)
                .decimals(DECIMALS)
                .program(PROGRAM);
        return programIndicatorModelBuilder.build();
    }

    @Override
    protected ProgramIndicatorModel cursorToModel(Cursor cursor) {
        return ProgramIndicatorModel.create(cursor);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getNameableModelAsObjectArray(model),
                toInteger(model.displayInForm()),
                model.expression(),
                model.dimensionItem(),
                model.filter(),
                model.decimals(),
                model.program());
    }

    @Test
    public void have_extra_data_set_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.DISPLAY_IN_FORM)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.EXPRESSION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DIMENSION_ITEM)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.FILTER)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DECIMALS)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.PROGRAM)).isEqualTo(true);
    }
}
