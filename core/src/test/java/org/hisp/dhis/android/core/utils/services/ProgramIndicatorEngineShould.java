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

package org.hisp.dhis.android.core.utils.services;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.constant.ConstantModel;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramIndicatorEngineShould {

    @Mock
    private Event event;

    @Mock
    private ProgramIndicator programIndicator;

    @Mock
    private TrackedEntityDataValue value1;

    @Mock
    private TrackedEntityDataValue value2;

    @Mock
    private TrackedEntityDataValue value3;

    @Mock
    private DataElementModel dataElementModel;

    private String dataElementUid1 = "HhyfnvrrKpN";
    private String dataElementUid2 = "nM4RZkpgMcP";
    private String dataElementUid3 = "vJeQc8NlWu6";

    private String programStageUid = "un3rUMhluNu";

    @Mock
    private ConstantModel constantModel;

    private String constantUid1 = "gzlRs2HEGAf";

    @Mock
    private IdentifiableObjectStore<DataElementModel> dataElementStore;
    @Mock
    private IdentifiableObjectStore<ConstantModel> constantStore;
    @Mock
    private TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    // Object to test
    private ProgramIndicatorEngine programIndicatorEngine;

    @Before
    public void setUp() throws Exception {

        programIndicatorEngine = new ProgramIndicatorEngine(dataElementStore, constantStore,
                trackedEntityAttributeValueStore);

        when(value1.dataElement()).thenReturn(dataElementUid1);
        when(value2.dataElement()).thenReturn(dataElementUid2);
        when(value3.dataElement()).thenReturn(dataElementUid3);
        when(event.trackedEntityDataValues()).thenReturn(Arrays.asList(value1, value2, value3));

        when(dataElementModel.valueType()).thenReturn(ValueType.NUMBER);
        when(dataElementStore.selectByUid(dataElementUid1, DataElementModel.factory)).thenReturn(dataElementModel);
        when(dataElementStore.selectByUid(dataElementUid2, DataElementModel.factory)).thenReturn(dataElementModel);
        when(dataElementStore.selectByUid(dataElementUid3, DataElementModel.factory)).thenReturn(dataElementModel);

        when(constantModel.uid()).thenReturn(constantUid1);
        when(constantStore.selectByUid(constantUid1, ConstantModel.factory)).thenReturn(constantModel);
    }

    @Test
    public void one_dataelement() throws Exception {
        when(programIndicator.expression()).thenReturn(de(programStageUid, dataElementUid1));

        when(value1.value()).thenReturn("3.5");

        String result = programIndicatorEngine.getProgramIndicatorValue(event, programIndicator);

        assertThat(result).isEqualTo("3.5");
    }

    @Test
    public void addition_two_dataelements() throws Exception {
        when(programIndicator.expression()).thenReturn(
                de(programStageUid, dataElementUid1) + " + " + de(programStageUid, dataElementUid2));

        when(value1.value()).thenReturn("3.5");
        when(value2.value()).thenReturn("2");

        String result = programIndicatorEngine.getProgramIndicatorValue(event, programIndicator);

        assertThat(result).isEqualTo("5.5");
    }

    @Test
    public void subtraction_two_dataelements() throws Exception {
        when(programIndicator.expression()).thenReturn(
                de(programStageUid, dataElementUid1) + " - " + de(programStageUid, dataElementUid2));

        when(value1.value()).thenReturn("3.5");
        when(value2.value()).thenReturn("2");

        String result = programIndicatorEngine.getProgramIndicatorValue(event, programIndicator);

        assertThat(result).isEqualTo("1.5");
    }

    @Test
    public void addition_two_dataelements_and_division() throws Exception {
        when(programIndicator.expression()).thenReturn(
                "(" + de(programStageUid, dataElementUid1) + " + " + de(programStageUid, dataElementUid2) +
                ") / " + de(programStageUid, dataElementUid3));

        when(value1.value()).thenReturn("2.5");
        when(value2.value()).thenReturn("2");
        when(value3.value()).thenReturn("1.5");

        String result = programIndicatorEngine.getProgramIndicatorValue(event, programIndicator);

        assertThat(result).isEqualTo("3");
    }

    @Test
    public void multiplication_two_dataelements() throws Exception {
        when(programIndicator.expression()).thenReturn(
                de(programStageUid, dataElementUid1) + " * " + de(programStageUid, dataElementUid2));

        when(value1.value()).thenReturn("3.5");
        when(value2.value()).thenReturn("2");

        String result = programIndicatorEngine.getProgramIndicatorValue(event, programIndicator);

        assertThat(result).isEqualTo("7");
    }

    @Test
    public void addition_dataelement_constant() throws Exception {
        when(programIndicator.expression()).thenReturn(
                de(programStageUid, dataElementUid1) + " + " + cons(constantUid1));

        when(value1.value()).thenReturn("3.5");
        when(constantModel.value()).thenReturn("2");

        String result = programIndicatorEngine.getProgramIndicatorValue(event, programIndicator);

        assertThat(result).isEqualTo("5.5");
    }

    private String de(String programStageUid, String dataElementUid) {
        return "#{" + programStageUid + "." + dataElementUid + "}";
    }

    private String cons(String constantUid) {
        return "C{" + constantUid + "}";
    }
}