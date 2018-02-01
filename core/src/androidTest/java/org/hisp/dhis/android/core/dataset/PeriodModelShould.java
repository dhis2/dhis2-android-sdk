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

package org.hisp.dhis.android.core.dataset;

import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class PeriodModelShould {

    @Test
    public void create_model_when_created_from_database_cursor() throws ParseException {
        String periodId = "2018W1";
        String periodType = "Weekly";

        String startDateStr = "2018-01-01T00:00:00.000";
        Date startDate = BaseIdentifiableObject.DATE_FORMAT.parse(startDateStr);
        String endDateStr = "2018-01-07T23:59:59.999";
        Date endDate = BaseIdentifiableObject.DATE_FORMAT.parse(endDateStr);

        MatrixCursor cursor = new MatrixCursor(PeriodModel.Columns.all());
        cursor.addRow(new Object[]{
                periodId,
                periodType,
                startDateStr,
                endDateStr
        });
        cursor.moveToFirst();

        PeriodModel model = PeriodModel.create(cursor);
        cursor.close();

        assertThat(model.periodId()).isEqualTo(periodId);
        assertThat(model.periodType()).isEqualTo(PeriodType.Weekly);
        assertThat(model.startDate()).isEqualTo(startDate);
        assertThat(model.endDate()).isEqualTo(endDate);
    }
}