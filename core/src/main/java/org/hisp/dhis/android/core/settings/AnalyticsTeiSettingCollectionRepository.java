
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
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.settings.internal.AnalyticsTeiDataChildrenAppender.KEY;

@Reusable
public class AnalyticsTeiSettingCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<AnalyticsTeiSetting, AnalyticsTeiSettingCollectionRepository> {

    @Inject
    AnalyticsTeiSettingCollectionRepository(ObjectWithoutUidStore<AnalyticsTeiSetting> store,
                                            RepositoryScope scope,
                                            Map<String, ChildrenAppender<AnalyticsTeiSetting>> childrenAppenders) {
        super(store,
                childrenAppenders,
                scope.toBuilder().children(scope.children().withChild(KEY)).build(),
                new FilterConnectorFactory<>(scope,
                        s -> new AnalyticsTeiSettingCollectionRepository(store, s, childrenAppenders)));
    }

    public StringFilterConnector<AnalyticsTeiSettingCollectionRepository> byUid() {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.UID);
    }

    public StringFilterConnector<AnalyticsTeiSettingCollectionRepository> byName() {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.NAME);
    }

    public StringFilterConnector<AnalyticsTeiSettingCollectionRepository> byShortName() {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.SHORT_NAME);
    }

    public StringFilterConnector<AnalyticsTeiSettingCollectionRepository> byProgram() {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.PROGRAM);
    }

    public StringFilterConnector<AnalyticsTeiSettingCollectionRepository> byProgramStage() {
        return cf.string(AnalyticsTeiSettingTableInfo.Columns.PROGRAM_STAGE);
    }

    public EnumFilterConnector<AnalyticsTeiSettingCollectionRepository, PeriodType> byPeriod() {
        return cf.enumC(AnalyticsTeiSettingTableInfo.Columns.PERIOD);
    }

    public EnumFilterConnector<AnalyticsTeiSettingCollectionRepository, ChartType> byType() {
        return cf.enumC(AnalyticsTeiSettingTableInfo.Columns.TYPE);
    }
}