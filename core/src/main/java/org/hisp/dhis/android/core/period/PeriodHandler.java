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
package org.hisp.dhis.android.core.period;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class PeriodHandler {
    public static final String START_DATE_STR = "2017-01-01T00:00:00.000";

    private final ObjectWithoutUidStore<PeriodModel> store;
    private final PeriodGenerator generator;

    private Date startDate;

    PeriodHandler(ObjectWithoutUidStore<PeriodModel> store, PeriodGenerator generator) {
        this.store = store;
        this.generator = generator;

        try {
            this.startDate = BaseIdentifiableObject.DATE_FORMAT.parse(START_DATE_STR);
        } catch (ParseException e) {
            this.startDate = new Date();
        }
    }

    public void generateAndPersist() {
        List<PeriodModel> periods = generator.generatePeriods(startDate);
        for (PeriodModel p : periods) {
            store.updateOrInsertWhere(p);
        }
    }

    public static PeriodHandler create(DatabaseAdapter databaseAdapter) {
        return new PeriodHandler(
                PeriodStore.create(databaseAdapter),
                new MockPeriodGenerator());
    }
}
