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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CategoryComboShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        CategoryCombo combo = objectMapper.readValue("{" +
                        "\"code\":\"BIRTHS\"," +
                        "\"created\":\"2011-12-24T12:24:25.203\"," +
                        "\"lastUpdated\":\"2016-04-18T16:04:34.745\"," +
                        "\"name\":\"Births\"," +
                        "\"id\":\"m2jTvAj5kkm\"," +
                        "\"dataDimensionType\":\"DISAGGREGATION\"," +
                        "\"displayName\":\"Births\"," +
                        "\"publicAccess\":\"rw------\"," +
                        "\"externalAccess\":false," +
                        "\"isDefault\":false," +
                        "\"skipTotal\":false," +
                        "\"user\":{\"id\":\"GOLswS44mh8\"}," +
                        "\"userGroupAccesses\":[]," +
                        "\"attributeValues\":[]," +
                        "\"categoryOptionCombos\":[" +
                        "{\"id\":\"b19Ye0TWs1D\"}," +
                        "{\"id\":\"YEmiuCcgNQI\"}," +
                        "{\"id\":\"vP9xV78M67W\"}]," +
                        "\"categories\":[" +
                        "{\"id\":\"KfdsGBcoiCa\"}," +
                        "{\"id\":\"cX5k9anHEHd\"}" +
                        "]" +
                        "}",
                CategoryCombo.class);

        assertThat(combo.uid()).isEqualTo("m2jTvAj5kkm");
        assertThat(combo.code()).isEqualTo("BIRTHS");
        assertThat(combo.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.203"));
        assertThat(combo.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-18T16:04:34.745"));
        assertThat(combo.name()).isEqualTo("Births");
        assertThat(combo.displayName()).isEqualTo("Births");
        assertThat(combo.isDefault()).isEqualTo(false);

        // categories
        assertThat(combo.categories().get(0).uid()).isEqualTo("KfdsGBcoiCa");
        assertThat(combo.categories().get(1).uid()).isEqualTo("cX5k9anHEHd");
    }
}
