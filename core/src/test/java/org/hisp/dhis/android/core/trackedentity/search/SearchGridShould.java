/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class SearchGridShould extends BaseObjectShould implements ObjectShould {

    public SearchGridShould() {
        super("trackedentity/search_grid.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        SearchGrid searchGrid = objectMapper.readValue(jsonStream, SearchGrid.class);

        assertThat(searchGrid.headers().size()).isEqualTo(9);

        SearchGridHeader firstHeader = searchGrid.headers().get(0);
        assertThat(firstHeader.name()).isEqualTo("instance");
        assertThat(firstHeader.column()).isEqualTo("Instance");
        assertThat(firstHeader.type()).isEqualTo("java.lang.String");
        assertThat(firstHeader.hidden()).isFalse();
        assertThat(firstHeader.meta()).isFalse();

        assertThat(searchGrid.width()).isEqualTo(9);
        assertThat(searchGrid.height()).isEqualTo(2);

        SearchGridMetadata metaData = searchGrid.metaData();
        assertThat(metaData.names().size()).isEqualTo(1);
        assertThat(metaData.names().get("nEenWmSyUEp")).isEqualTo("Person");

        assertThat(searchGrid.rows().size()).isEqualTo(2);

        List<String> firstRow = searchGrid.rows().get(0);
        assertThat(firstRow.get(0)).isEqualTo("PmslDkqLqeG");
        assertThat(firstRow.get(1)).isEqualTo("2018-04-27 17:34:17.005");
        assertThat(firstRow.get(2)).isEqualTo("2018-04-27 17:34:28.442");
        assertThat(firstRow.get(3)).isEqualTo("DiszpKrYNg8");
        assertThat(firstRow.get(4)).isEqualTo("Ngelehun CHC");
        assertThat(firstRow.get(5)).isEqualTo("nEenWmSyUEp");
        assertThat(firstRow.get(6)).isEqualTo("");
        assertThat(firstRow.get(7)).isEqualTo("Firsty");
        assertThat(firstRow.get(8)).isEqualTo("Namey");

        List<String> secondRow = searchGrid.rows().get(1);
        assertThat(secondRow.get(0)).isEqualTo("Lf9FhXshRnd");
        assertThat(secondRow.get(1)).isEqualTo("2018-04-26 06:10:51.634");
        assertThat(secondRow.get(2)).isEqualTo("2018-04-26 06:10:52.944");
        assertThat(secondRow.get(3)).isEqualTo("DiszpKrYNg8");
        assertThat(secondRow.get(4)).isEqualTo("Ngelehun CHC");
        assertThat(secondRow.get(5)).isEqualTo("nEenWmSyUEp");
        assertThat(secondRow.get(6)).isEqualTo("false");
        assertThat(secondRow.get(7)).isEqualTo("Jorge");
        assertThat(secondRow.get(8)).isEqualTo("Fernandez");
    }
}