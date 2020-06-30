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

package org.hisp.dhis.android.core.domain.aggregated.data.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.period.internal.PeriodForDataSetManager;
import org.hisp.dhis.android.core.settings.DataSetSettings;
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AggregatedDataCallBundleFactoryShould {

    @Mock
    private DataSetCollectionRepository dataSetRepository;

    @Mock
    private OrganisationUnitCollectionRepository organisationUnitRepository;

    @Mock
    private DataSetSettingsObjectRepository dataSetSettingsObjectRepository;

    @Mock
    private PeriodForDataSetManager periodManager;

    @Mock
    private ObjectWithoutUidStore<AggregatedDataSync> aggregatedDataSyncStore;

    @Mock
    private AggregatedDataSyncLastUpdatedCalculator lastUpdatedCalculator;

    @Mock
    private DataSetSettings dataSetSettings;

    @Mock
    private DataSet dataSet1;

    @Mock
    private DataSet dataSet2;

    private String ds1 = "ds1", ds2 = "ds2";

    private String ou1 = "ou1", ou2 = "ou2";

    private List<String> rootOrgUnits = Arrays.asList(ou1, ou2);

    private Set<String> allOrgUnits = new HashSet<>(rootOrgUnits);

    private List<Period> periods = Collections.singletonList(Period.builder().periodId("202002").build());

    // Object to test
    private AggregatedDataCallBundleFactory bundleFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(dataSet1.uid()).thenReturn(ds1);
        when(dataSet2.uid()).thenReturn(ds2);
        when(dataSetSettings.specificSettings()).thenReturn(Collections.emptyMap());
        when(periodManager.getPeriodsInRange(PeriodType.Monthly, 0, 0)).thenReturn(Collections.emptyList());
        when(periodManager.getPeriodsInRange(any(), anyInt(), anyInt())).thenReturn(periods);

        bundleFactory = new AggregatedDataCallBundleFactory(dataSetRepository, organisationUnitRepository,
                dataSetSettingsObjectRepository, periodManager, aggregatedDataSyncStore, lastUpdatedCalculator);
    }

    @Test
    public void create_single_bundle_if_same_periods() {
        when(dataSet1.openFuturePeriods()).thenReturn(1);
        when(dataSet1.periodType()).thenReturn(PeriodType.Monthly);
        when(dataSet2.openFuturePeriods()).thenReturn(1);
        when(dataSet2.periodType()).thenReturn(PeriodType.Monthly);

        List<AggregatedDataCallBundle> bundles = bundleFactory.getBundlesInternal(Arrays.asList(dataSet1, dataSet2),
                dataSetSettings, rootOrgUnits, allOrgUnits, new HashMap<>());

        assertThat(bundles.size()).isEqualTo(1);
        assertThat(bundles.get(0).dataSets()).contains(dataSet1, dataSet2);
    }

    @Test
    public void create_different_bundles_if_different_periods() {
        when(dataSet1.openFuturePeriods()).thenReturn(1);
        when(dataSet1.periodType()).thenReturn(PeriodType.Monthly);
        when(dataSet2.openFuturePeriods()).thenReturn(2);
        when(dataSet2.periodType()).thenReturn(PeriodType.Monthly);

        List<AggregatedDataCallBundle> bundles = bundleFactory.getBundlesInternal(Arrays.asList(dataSet1, dataSet2),
                dataSetSettings, rootOrgUnits, allOrgUnits, new HashMap<>());

        assertThat(bundles.size()).isEqualTo(2);
    }

}
