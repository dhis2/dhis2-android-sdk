/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.models.dataelement;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryOptionIntegrationTests {
    DateFormat dateFormat;
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(dateFormat);
    }

    @Test
    public void categoryOption_shouldMapFromJsonString() throws IOException, ParseException {
        CategoryOption categoryOption = objectMapper.readValue("{" +
                        "\"lastUpdated\":\"2016-08-08T11:17:59.448\"," +
                        "\"id\":\"cQYFfHX9oIT\"," +
                        "\"href\":\"https://play.dhis2.org/demo/api/categoryOptions/cQYFfHX9oIT\"," +
                        "\"created\":\"2016-08-08T11:17:59.448\"," +
                        "\"name\":\"Green\"," +
                        "\"shortName\":\"Green\"," +
                        "\"displayName\":\"Green\"," +
                        "\"displayShortName\":\"Green\"," +
                        "\"startDate\":\"2016-04-01T00:00:00.000\"," +
                        "\"endDate\":\"2016-05-01T00:00:00.000\"," +
                        "\"externalAccess\":false," +
                        "\"dimensionItem\":\"cQYFfHX9oIT\"," +
                        "\"dimensionItemType\":\"CATEGORY_OPTION\"," +
                        "\"categories\":[]," +
                        "\"organisationUnits\":[]," +
                        "\"categoryOptionCombos\":[" +
                        "{\"id\":\"S34ULMcHMca\"}," +
                        "{\"id\":\"sqGRzCziswD\"}," +
                        "{\"id\":\"WVzTbHctjok\"}," +
                        "{\"id\":\"QFcGyiRFFH5\"}" +
                        "]," +
                        "\"categoryOptionGroups\":[]" +
                        "}",
                CategoryOption.class);

        assertThat(categoryOption.uid()).isEqualTo("cQYFfHX9oIT");
        assertThat(categoryOption.lastUpdated())
                .isEqualTo(dateFormat.parse("2016-08-08T11:17:59.448"));
        assertThat(categoryOption.created())
                .isEqualTo(dateFormat.parse("2016-08-08T11:17:59.448"));

        assertThat(categoryOption.name()).isEqualTo("Green");
        assertThat(categoryOption.displayName()).isEqualTo("Green");
        assertThat(categoryOption.displayShortName()).isEqualTo("Green");
        assertThat(categoryOption.displayName()).isEqualTo("Green");

        assertThat(categoryOption.startDate())
                .isEqualTo(dateFormat.parse("2016-04-01T00:00:00.000"));
        assertThat(categoryOption.endDate())
                .isEqualTo(dateFormat.parse("2016-05-01T00:00:00.000"));

        // check if list maintains order of the items in payload
        assertThat(categoryOption.categoryOptionCombos().get(0).uid()).isEqualTo("S34ULMcHMca");
        assertThat(categoryOption.categoryOptionCombos().get(1).uid()).isEqualTo("sqGRzCziswD");
        assertThat(categoryOption.categoryOptionCombos().get(2).uid()).isEqualTo("WVzTbHctjok");
        assertThat(categoryOption.categoryOptionCombos().get(3).uid()).isEqualTo("QFcGyiRFFH5");
    }
}
