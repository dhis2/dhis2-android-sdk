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

package org.hisp.dhis.android.core.period.internal;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.ObjectWithoutUidStoreAbstractIntegrationShould;
import org.hisp.dhis.android.core.data.period.PeriodSamples;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodTableInfo;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class PeriodStoreIntegrationShould extends ObjectWithoutUidStoreAbstractIntegrationShould<Period> {

    private PeriodStore periodStore;

    public PeriodStoreIntegrationShould() {
        super(PeriodStoreImpl.create(TestDatabaseAdapterFactory.get()), PeriodTableInfo.TABLE_INFO,
                TestDatabaseAdapterFactory.get());
        this.periodStore = PeriodStoreImpl.create(TestDatabaseAdapterFactory.get());
    }

    @Override
    protected Period buildObject() {
        return PeriodSamples.getPeriod();
    }

    @Override
    protected Period buildObjectToUpdate() {
        return PeriodSamples.getPeriod().toBuilder()
                .startDate(new Date())
                .build();
    }

    @Test
    public void select_correct_period_passing_period_type_and_a_date() throws ParseException {
        new PeriodHandler(periodStore, ParentPeriodGeneratorImpl.create(CalendarProviderFactory.createFixed())).generateAndPersist();

        Period period = periodStore.selectPeriodByTypeAndDate(PeriodType.SixMonthly,
                BaseIdentifiableObject.DATE_FORMAT.parse("2019-03-02T12:24:25.319"));

        assertThat(period.periodId()).isEqualTo("2019S1");
    }
}