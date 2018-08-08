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
import org.assertj.core.util.Sets;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModel;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModelBuilder;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private final String DATA_SET_1_UID = "test_data_set_uid1";
    private final String DATA_SET_2_UID = "test_data_set_uid2";
    private final String DATA_SET_3_UID = "test_data_set_uid3";

    private DataSetElement dataSetElement1 = DataSetElement.create(ObjectWithUid.create("dataSetElement1"),
            ObjectWithUid.create("categoryCombo1"));
    private DataSetElement dataSetElement2 = DataSetElement.create(ObjectWithUid.create("dataSetElement2"),
            ObjectWithUid.create("categoryCombo2"));
    private DataSetElement dataSetElement3 = DataSetElement.create(ObjectWithUid.create("dataSetElement3"),
            ObjectWithUid.create("categoryCombo3"));

    private ObjectWithUid indicator1 = ObjectWithUid.create("indicator1");
    private ObjectWithUid indicator2 = ObjectWithUid.create("indicator2");
    private ObjectWithUid indicator3 = ObjectWithUid.create("indicator3");

    @Mock
    private OrganisationUnit organisationUnit1;

    @Mock
    private OrganisationUnit organisationUnit2;

    private DataSetParentLinkManager dataSetParentLinkManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataSetParentLinkManager = new DataSetParentLinkManager(dataSetDataElementStore,
                dataSetOrganisationUnitStore, dataSetIndicatorStore);

        when(dataSet1.uid()).thenReturn(DATA_SET_1_UID);
        when(dataSet2.uid()).thenReturn(DATA_SET_2_UID);
        when(dataSet3.uid()).thenReturn(DATA_SET_3_UID);
        when(dataSet1.dataSetElements()).thenReturn(Lists.newArrayList(dataSetElement1, dataSetElement2));
        when(dataSet2.dataSetElements()).thenReturn(Lists.newArrayList(dataSetElement2, dataSetElement3));

        when(dataSet1.indicators()).thenReturn(Lists.newArrayList(indicator1, indicator2));
        when(dataSet2.indicators()).thenReturn(Lists.newArrayList(indicator2, indicator3));

        when(organisationUnit1.uid()).thenReturn("test_ou_uid_uid1");
        when(organisationUnit1.dataSets()).thenReturn(Lists.newArrayList(dataSet1, dataSet2));
        when(organisationUnit2.uid()).thenReturn("test_ou_uid_uid2");
        when(organisationUnit2.dataSets()).thenReturn(Lists.newArrayList(dataSet2, dataSet3));
    }

    @Test
    public void store_data_set_data_element_links() throws Exception {
        dataSetParentLinkManager.saveDataSetDataElementAndIndicatorLinks(Lists.newArrayList(dataSet1, dataSet2));
        dataSetParentLinkManager.saveDataSetOrganisationUnitLinks(
                Lists.newArrayList(organisationUnit1, organisationUnit2), Sets.newHashSet(
                        Lists.newArrayList(DATA_SET_1_UID, DATA_SET_2_UID, DATA_SET_3_UID)));

        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(dataSetElement1, dataSet1));
        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(dataSetElement2, dataSet1));
        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(dataSetElement2, dataSet2));
        verify(dataSetDataElementStore).updateOrInsertWhere(dataElementExpectedLink(dataSetElement3, dataSet2));

        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(indicator1, dataSet1));
        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(indicator2, dataSet1));
        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(indicator2, dataSet2));
        verify(dataSetIndicatorStore).updateOrInsertWhere(indicatorExpectedLink(indicator3, dataSet2));

        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(organisationUnit1, dataSet1));
        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(organisationUnit1, dataSet2));
        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(organisationUnit2, dataSet2));
        verify(dataSetOrganisationUnitStore).updateOrInsertWhere(orgUnitExpectedLink(organisationUnit2, dataSet3));

        verifyNoMoreInteractions(dataSetOrganisationUnitStore);
    }

    private DataSetDataElementLinkModel dataElementExpectedLink(DataSetElement decc, DataSet dataSet) {
        return new DataSetDataElementLinkModelBuilder(dataSet).buildModel(decc);
    }

    private DataSetIndicatorLinkModel indicatorExpectedLink(ObjectWithUid i1, DataSet dataSet) {
        return new DataSetIndicatorLinkModelBuilder(dataSet).buildModel(i1);
    }

    private DataSetOrganisationUnitLinkModel orgUnitExpectedLink(OrganisationUnit ou, DataSet dataSet) {
        return new DataSetOrganisationUnitLinkModelBuilder(ou).buildModel(dataSet);
    }
}
