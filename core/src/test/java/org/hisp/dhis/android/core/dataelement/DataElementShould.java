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

package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.core.common.ValueTypeRenderingType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DataElementShould extends BaseObjectShould implements ObjectShould {

    private static ValueTypeDeviceRendering desktopRendering = ValueTypeDeviceRendering.create(
            ValueTypeRenderingType.VERTICAL_RADIOBUTTONS, 0, 10, 1, 0);

    private static ValueTypeDeviceRendering mobileRendering = ValueTypeDeviceRendering.create(
            ValueTypeRenderingType.SHARED_HEADER_RADIOBUTTONS, 3, 15, 2, 1);

    public DataElementShould() {
        super("dataelement/data_element.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        DataElement dataElement = objectMapper.readValue(jsonStream, DataElement.class);

        // basic properties
        assertThat(dataElement.uid()).isEqualTo("g9eOBujte1U");
        assertThat(dataElement.code()).isEqualTo("DE_2005735");
        assertThat(dataElement.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-09-20T08:36:46.552"));
        assertThat(dataElement.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-11-11T21:56:05.560"));

        // names
        assertThat(dataElement.name()).isEqualTo("MCH ANC Visit");
        assertThat(dataElement.shortName()).isEqualTo("ANC Visit");
        assertThat(dataElement.displayName()).isEqualTo("MCH ANC Visit");
        assertThat(dataElement.displayShortName()).isEqualTo("ANC Visit");
        assertThat(dataElement.formName()).isEqualTo("ANC Visit");
        assertThat(dataElement.displayFormName()).isEqualTo("ANC Visit");

        assertThat(dataElement.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(dataElement.zeroIsSignificant()).isEqualTo(false);
        assertThat(dataElement.optionSetUid()).isEqualTo("fUS7fy2HbaI");
        assertThat(dataElement.categoryComboUid()).isEqualTo("p0KPaWEg3cf");
        assertThat(dataElement.domainType()).isEqualTo("TRACKER");
        assertThat(dataElement.renderType().desktop()).isEqualTo(desktopRendering);
        assertThat(dataElement.renderType().mobile()).isEqualTo(mobileRendering);
    }
}
