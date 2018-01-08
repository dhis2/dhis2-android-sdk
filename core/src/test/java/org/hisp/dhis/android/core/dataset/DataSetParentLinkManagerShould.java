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
import org.hisp.dhis.android.core.common.ObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataSetParentLinkManagerShould {

    @Mock
    private ObjectStore<DataSetDataElementLinkModel> dataSetDataElementStore;

    @Mock
    private DataSet dataSet1;

    @Mock
    private DataSet dataSet2;

    private DataElementCategoryCombo decc1 = DataElementCategoryCombo.create(
            ObjectWithUid.create("de1"), ObjectWithUid.create("cc1"));

    private DataElementCategoryCombo decc2 = DataElementCategoryCombo.create(
            ObjectWithUid.create("de2"), ObjectWithUid.create("cc2"));

    private DataElementCategoryCombo decc3 = DataElementCategoryCombo.create(
            ObjectWithUid.create("de3"), ObjectWithUid.create("cc3"));

    private DataSetParentLinkManager linkManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        linkManager = new DataSetParentLinkManager(
                dataSetDataElementStore);
        when(dataSet1.uid()).thenReturn("test_data_uid_uid1");
        when(dataSet2.uid()).thenReturn("test_data_uid_uid2");
        when(dataSet1.dataSetElements()).thenReturn(Lists.newArrayList(decc1, decc2));
        when(dataSet2.dataSetElements()).thenReturn(Lists.newArrayList(decc2, decc3));
    }

    @Test
    public void store_data_set_data_element_links() throws Exception {
        linkManager.saveDataSetDataElementLinks(Lists.newArrayList(dataSet1, dataSet2));
        verify(dataSetDataElementStore).insert(expectedLink(decc1, dataSet1));
        verify(dataSetDataElementStore).insert(expectedLink(decc2, dataSet1));
        verify(dataSetDataElementStore).insert(expectedLink(decc2, dataSet2));
        verify(dataSetDataElementStore).insert(expectedLink(decc3, dataSet2));
    }

    private DataSetDataElementLinkModel expectedLink(DataElementCategoryCombo decc, DataSet dataSet) {
        return DataSetDataElementLinkModel.builder()
                .dataSet(dataSet.uid()).dataElement(decc.dataElement().uid())
                .categoryCombo(decc.categoryComboUid()).build();
    }
}
