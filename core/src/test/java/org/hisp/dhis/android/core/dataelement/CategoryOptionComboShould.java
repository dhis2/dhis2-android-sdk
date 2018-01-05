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
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CategoryOptionComboShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        CategoryOptionCombo categoryOptionCombo = objectMapper.readValue("{" +
                        "\"code\":\"COC_358963\"," +
                        "\"lastUpdated\":\"2011-12-24T12:24:25.319\"," +
                        "\"id\":\"S34ULMcHMca\"," +
                        "\"created\":\"2011-12-24T12:24:25.319\"," +
                        "\"name\":\"0-11m\"," +
                        "\"shortName\":\"0-11m\"," +
                        "\"displayName\":\"0-11m\"," +
                        "\"displayShortName\":\"0-11m\"," +
                        "\"externalAccess\":false," +
                        "\"ignoreApproval\":false," +
                        "\"dimensionItem\":\"S34ULMcHMca\"," +
                        "\"categoryCombo\":{\"id\":\"t3aNCvHsoSn\"}," +
                        "\"translations\":[]," +
                        "\"categoryOptions\":[{\"id\":\"FbLZS3ueWbQ\"}]," +
                        "\"userGroupAccesses\":[]," +
                        "\"attributeValues\":[]}",
                CategoryOptionCombo.class);

        assertThat(categoryOptionCombo.uid()).isEqualTo("S34ULMcHMca");
        assertThat(categoryOptionCombo.code()).isEqualTo("COC_358963");

        assertThat(categoryOptionCombo.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.319"));
        assertThat(categoryOptionCombo.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.319"));

        assertThat(categoryOptionCombo.name()).isEqualTo("0-11m");
        assertThat(categoryOptionCombo.shortName()).isEqualTo("0-11m");
        assertThat(categoryOptionCombo.displayName()).isEqualTo("0-11m");
        assertThat(categoryOptionCombo.displayShortName()).isEqualTo("0-11m");
    }
}
