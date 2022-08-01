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

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class SynchronizationSettingsShould extends BaseObjectShould implements ObjectShould {

    public SynchronizationSettingsShould() {
        super("settings/synchronization_settings.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        SynchronizationSettings syncSettings = objectMapper.readValue(jsonStream, SynchronizationSettings.class);

        assertThat(syncSettings.dataSync()).isEqualTo(DataSyncPeriod.EVERY_24_HOURS);
        assertThat(syncSettings.metadataSync()).isEqualTo(MetadataSyncPeriod.EVERY_12_HOURS);
        assertThat(syncSettings.trackerImporterVersion()).isEqualTo(TrackerImporterVersion.V1);

        assertThat(syncSettings.dataSetSettings()).isNotNull();
        assertThat(syncSettings.dataSetSettings().globalSettings()).isNotNull();
        assertThat(syncSettings.dataSetSettings().globalSettings().periodDSDownload()).isEqualTo(30);
        assertThat(syncSettings.dataSetSettings().specificSettings()).isNotNull();
        assertThat(syncSettings.dataSetSettings().specificSettings().get("EKWVBc5C0ms").periodDSDownload()).isEqualTo(10);

        assertThat(syncSettings.programSettings()).isNotNull();
        assertThat(syncSettings.programSettings().globalSettings()).isNotNull();
        assertThat(syncSettings.programSettings().globalSettings().teiDownload()).isEqualTo(500);
        assertThat(syncSettings.programSettings().globalSettings().settingDownload()).isEqualTo(LimitScope.PER_ORG_UNIT);
        assertThat(syncSettings.programSettings().specificSettings()).isNotNull();
        assertThat(syncSettings.programSettings().specificSettings().get("lxAQ7Zs9VYR").eventsDownload()).isEqualTo(10);
    }
}
