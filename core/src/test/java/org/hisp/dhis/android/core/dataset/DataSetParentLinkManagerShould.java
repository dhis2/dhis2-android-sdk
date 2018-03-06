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

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataSetParentLinkManagerShould {

    @Mock
    private ObjectWithoutUidStore<DataSetDataElementLinkModel> dataSetDataElementStore;

    @Mock
    private ObjectWithoutUidStore<DataSetOrganisationUnitLinkModel> dataSetOrganisationUnitStore;

    @Mock
    private ObjectWithoutUidStore<DataSetIndicatorLinkModel> dataSetIndicatorStore;

    @Mock
    private DataSet dataSet1;

    @Mock
    private DataSet dataSet2;

    @Mock
    private DataSet dataSet3;

    private DataElementUids decc1 = DataElementUids.create(ObjectWithUid.create("de1"));

    private DataElementUids decc2 = DataElementUids.create(ObjectWithUid.create("de2"));

    private DataElementUids decc3 = DataElementUids.create(ObjectWithUid.create("de3"));

    private ObjectWithUid i1 = ObjectWithUid.create("i1");

    private ObjectWithUid i2 = ObjectWithUid.create("i2");

    private ObjectWithUid i3 = ObjectWithUid.create("i3");

    @Mock
    private OrganisationUnit ou1;

    @Mock
    private OrganisationUnit ou2;

    private DataSetParentLinkManager linkManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        linkManager = new DataSetParentLinkManager(dataSetDataElementStore,
                dataSetOrganisationUnitStore, dataSetIndicatorStore);

        when(dataSet1.uid()).thenReturn("test_data_uid_uid1");
        when(dataSet2.uid()).thenReturn("test_data_uid_uid2");
        when(dataSet3.uid()).thenReturn("test_data_uid_uid3");
        when(dataSet1.dataSetElements()).thenReturn(Lists.newArrayList(decc1, decc2));
        when(dataSet2.dataSetElements()).thenReturn(Lists.newArrayList(decc2, decc3));

        when(dataSet1.indicators()).thenReturn(Lists.newArrayList(i1, i2));
        when(dataSet2.indicators()).thenReturn(Lists.newArrayList(i2, i3));

        when(ou1.uid()).thenReturn("test_ou_uid_uid1");
        when(ou1.dataSets()).thenReturn(Lists.newArrayList(dataSet1, dataSet2));
        when(ou2.uid()).thenReturn("test_ou_uid_uid2");
        when(ou2.dataSets()).thenReturn(Lists.newArrayList(dataSet2, dataSet3));
    }

    @Test
    public void store_data_set_data_element_links() throws Exception {
        linkManager.saveDataSetDataElementAndIndicatorLinks(Lists.newArrayList(dataSet1, dataSet2));
        linkManager.saveDataSetOrganisationUnitLinks(Lists.newArrayList(ou1, ou2));

        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(decc1, dataSet1));
        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(decc2, dataSet1));
        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(decc2, dataSet2));
        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(decc3, dataSet2));

        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(i1, dataSet1));
        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(i2, dataSet1));
        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(i2, dataSet2));
        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(i3, dataSet2));

        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(ou1, dataSet1));
        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(ou1, dataSet2));
        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(ou2, dataSet2));
        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(ou2, dataSet3));

        verifyNoMoreInteractions(dataSetOrganisationUnitStore);
    }

    private DataSetDataElementLinkModel dataElementExpectedLink(DataElementUids decc, DataSet dataSet) {
        return DataSetDataElementLinkModel.builder()
                .dataSet(dataSet.uid()).dataElement(decc.dataElement().uid()).build();
    }

    private DataSetIndicatorLinkModel indicatorExpectedLink(ObjectWithUid i1, DataSet dataSet) {
        return DataSetIndicatorLinkModel.create(dataSet.uid(), i1.uid());
    }

    private DataSetOrganisationUnitLinkModel orgUnitExpectedLink(OrganisationUnit ou, DataSet dataSet) {
        return DataSetOrganisationUnitLinkModel.create(dataSet.uid(), ou.uid());
    }
}
