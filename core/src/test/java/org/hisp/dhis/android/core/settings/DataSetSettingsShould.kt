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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class DataSetSettingsShould extends BaseObjectShould implements ObjectShould {

    public DataSetSettingsShould() {
        super("settings/dataset_settings.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        DataSetSettings dataSetSettings = objectMapper.readValue(jsonStream, DataSetSettings.class);

        DataSetSetting global = dataSetSettings.globalSettings();
        assertThat(global.uid()).isNull();
        assertThat(global.name()).isNull();
        assertThat(global.lastUpdated()).isEqualTo(BaseIdentifiableObject.parseDate("2020-01-31T22:42:57.763Z"));
        assertThat(global.periodDSDownload()).isEqualTo(30);
        assertThat(global.periodDSDBTrimming()).isEqualTo(40);

        DataSetSetting childHealth = dataSetSettings.specificSettings().get("BfMAe6Itzgt");
        assertThat(childHealth).isNotNull();
        assertThat(childHealth.uid()).isEqualTo("BfMAe6Itzgt");
        assertThat(childHealth.name()).isEqualTo("Child Health");
        assertThat(childHealth.lastUpdated()).isEqualTo(BaseIdentifiableObject.parseDate("2020-01-31T22:38:20.210Z"));
        assertThat(childHealth.periodDSDownload()).isEqualTo(10);
        assertThat(childHealth.periodDSDBTrimming()).isEqualTo(15);
    }
}
