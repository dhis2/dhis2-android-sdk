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

package org.hisp.dhis.android.core.common;

import android.database.MatrixCursor;

import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public abstract class ModelAbstractShould<M extends BaseModel, P> {

    protected final M model;
    protected final P pojo;
    protected final String[] columns;
    protected final int columnsLength;
    protected final ModelFactory<M, P> modelFactory;

    public ModelAbstractShould(String[] columns, int columnsLength, ModelFactory<M, P> modelFactory) {
        this.model = buildModel();
        this.pojo = buildPojo();
        this.columns = columns;
        this.columnsLength = columnsLength;
        this.modelFactory = modelFactory;
    }

    protected abstract M buildModel();

    protected abstract P buildPojo();

    protected abstract Object[] getModelAsObjectArray();

    @Test
    public void create_model_from_cursor() {
        MatrixCursor cursor = new MatrixCursor(ColumnsArrayUtils.getColumnsWithId(columns));
        cursor.addRow(getModelAsObjectArray());
        cursor.moveToFirst();

        M modelFromDB = modelFactory.fromCursor(cursor);
        cursor.close();

        assertThat(modelFromDB).isEqualTo(model);
    }

    @Test
    public void create_model_from_pojo() {
        assertThat(modelFactory.fromPojo(pojo)).isEqualTo(model);
    }

    @Test
    public void have_correct_number_of_columns() {
        assertThat(columns.length).isEqualTo(columnsLength);
    }
}
