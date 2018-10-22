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

package org.hisp.dhis.android.core.indicator;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.indicator.IndicatorSamples;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class IndicatorStoreIntegrationShould extends AbsStoreTestCase {

    private final Indicator indicator = IndicatorSamples.getIndicator();

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        database().setForeignKeyConstraintsEnabled(false);
    }

    @Test
    public void get_inserted_object() {
        IdentifiableObjectStore<Indicator> store = IndicatorStore.create(databaseAdapter());
        store.insert(indicator);
        Indicator indicatorFromDb = store.selectFirst();
        assertThat(indicatorFromDb).isEqualTo(indicator);
    }

    @Test
    public void delete_inserted_object() {
        IdentifiableObjectStore<Indicator> store = IndicatorStore.create(databaseAdapter());
        store.insert(indicator);
        store.delete(indicator.uid());
        assertThat(store.selectFirst()).isEqualTo(null);
    }

    @Test
    public void update_inserted_object() {
        IdentifiableObjectStore<Indicator> store = IndicatorStore.create(databaseAdapter());
        store.insert(indicator);
        Indicator indicatorToUpdate = indicator.toBuilder()
                .indicatorType(ObjectWithUid.create("new_indicator_type_uid"))
                .build();
        store.update(indicatorToUpdate);
        Indicator indicatorFromDb = store.selectFirst();
        assertThat(indicatorFromDb).isEqualTo(indicatorToUpdate);
    }

    @Test
    public void select_inserted_object_uid() {
        IdentifiableObjectStore<Indicator> store = IndicatorStore.create(databaseAdapter());
        store.insert(indicator);
        String indicatorUidFromDb = store.selectUids().iterator().next();
        assertThat(indicatorUidFromDb).isEqualTo(indicator.uid());
    }
}