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

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl;
import org.hisp.dhis.android.core.settings.internal.DataSetSettingCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class DataSetSettingsObjectRepository
        extends ReadOnlyAnyObjectWithDownloadRepositoryImpl<DataSetSettings>
        implements ReadOnlyWithDownloadObjectRepository<DataSetSettings> {

    private final ObjectWithoutUidStore<DataSetSetting> store;

    @Inject
    DataSetSettingsObjectRepository(ObjectWithoutUidStore<DataSetSetting> store,
                                    DataSetSettingCall dataSetSettingCall) {
        super(dataSetSettingCall);
        this.store = store;
    }

    @Override
    public DataSetSettings blockingGet() {
        List<DataSetSetting> settings = store.selectAll();

        if (settings.isEmpty()) {
            return null;
        }

        DataSetSettings.Builder builder = DataSetSettings.builder();
        Map<String, DataSetSetting> specifics = new HashMap<>();

        for (DataSetSetting dataSetSetting : settings) {
            if (dataSetSetting.uid() == null) {
                builder.globalSettings(dataSetSetting);
            } else {
                specifics.put(dataSetSetting.uid(), dataSetSetting);
            }
        }
        builder.specificSettings(specifics);

        return builder.build();
    }
}