/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.tracker

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.hisp.dhis.android.core.systeminfo.DHISPatchVersion
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.junit.Before
import org.junit.Test

class TrackerPostParentCallHelperShould {

    private val systemInfoStore: ObjectWithoutUidStore<SystemInfo> = mock()
    private val synchronizationSettingStore: ObjectWithoutUidStore<SynchronizationSettings> = mock()

    private val systemInfo: SystemInfo = mock()
    private val syncSettings: SynchronizationSettings = mock()

    private lateinit var helper: TrackerPostParentCallHelper

    @Before
    fun setup() {
        val dhisVersionManager = DHISVersionManagerImpl(systemInfoStore)
        helper = TrackerPostParentCallHelper(dhisVersionManager, synchronizationSettingStore)

        whenever(systemInfoStore.selectFirst()).doReturn(systemInfo)
        whenever(synchronizationSettingStore.selectFirst()).doReturn(syncSettings)
    }

    @Test
    fun should_return_old_importer_if_less_than_40() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_39_0.strValue)
        assertThat(helper.useNewTrackerImporter()).isFalse()
    }

    @Test
    fun should_return_new_importer_if_greater_or_equal_than_40() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_40_0.strValue)
        assertThat(helper.useNewTrackerImporter()).isTrue()
    }

    @Test
    fun should_return_true_if_explicitly_set_in_importer() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_39_0.strValue)
        whenever(syncSettings.trackerImporterVersion()).doReturn(TrackerImporterVersion.V2)
        assertThat(helper.useNewTrackerImporter()).isTrue()
    }

    @Test
    fun should_return_false_if_explicitly_unset_in_importer() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_40_0.strValue)
        whenever(syncSettings.trackerImporterVersion()).doReturn(TrackerImporterVersion.V1)
        assertThat(helper.useNewTrackerImporter()).isFalse()
    }

    @Test
    fun should_always_return_false_if_less_than_38_in_importer() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_37_0.strValue)
        whenever(syncSettings.trackerImporterVersion()).doReturn(TrackerImporterVersion.V2)
        assertThat(helper.useNewTrackerImporter()).isFalse()
    }

    @Test
    fun should_return_true_if_explicitly_set_in_exporter() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_40_0.strValue)
        whenever(syncSettings.trackerExporterVersion()).doReturn(TrackerExporterVersion.V2)
        assertThat(helper.useNewTrackerExporter()).isTrue()
    }

    @Test
    fun should_return_false_if_explicitly_unset_in_exporter() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_40_0.strValue)
        whenever(syncSettings.trackerExporterVersion()).doReturn(TrackerExporterVersion.V1)
        assertThat(helper.useNewTrackerExporter()).isFalse()
    }

    @Test
    fun should_always_return_false_if_less_than_40_in_exporter() {
        whenever(systemInfo.version()).doReturn(DHISPatchVersion.V2_39_0.strValue)
        whenever(syncSettings.trackerExporterVersion()).doReturn(TrackerExporterVersion.V2)
        assertThat(helper.useNewTrackerExporter()).isFalse()
    }
}
