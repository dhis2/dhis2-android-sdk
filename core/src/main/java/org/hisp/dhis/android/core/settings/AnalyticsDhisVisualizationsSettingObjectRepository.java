/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.settings.internal.AnalyticsSettingCall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class AnalyticsDhisVisualizationsSettingObjectRepository
        extends ReadOnlyAnyObjectWithDownloadRepositoryImpl<AnalyticsDhisVisualizationsSetting>
        implements ReadOnlyWithDownloadObjectRepository<AnalyticsDhisVisualizationsSetting> {

    private final ObjectWithoutUidStore<AnalyticsDhisVisualization> analyticsDhisVisualizationStore;

    @Inject
    public AnalyticsDhisVisualizationsSettingObjectRepository(
            ObjectWithoutUidStore<AnalyticsDhisVisualization> analyticsDhisVisualizationStore,
            AnalyticsSettingCall analyticsSettingCall) {
        super(analyticsSettingCall);
        this.analyticsDhisVisualizationStore = analyticsDhisVisualizationStore;
    }

    @Override
    public AnalyticsDhisVisualizationsSetting blockingGet() {
        List<AnalyticsDhisVisualization> analyticsDhisVisualizations = analyticsDhisVisualizationStore.selectAll();

        if (analyticsDhisVisualizations.isEmpty()) {
            return null;
        }

        AnalyticsDhisVisualizationsSetting.Builder analyticsDhisVisualizationsSettingBuilder =
                AnalyticsDhisVisualizationsSetting.builder();
        List<AnalyticsDhisVisualizationsGroup> home = new ArrayList<>();
        Map<String, List<AnalyticsDhisVisualizationsGroup>> program = new HashMap<>();
        Map<String, List<AnalyticsDhisVisualizationsGroup>> dataSet = new HashMap<>();

        for (AnalyticsDhisVisualization analyticsDhisVisualization : analyticsDhisVisualizations) {
            AnalyticsDhisVisualizationsGroup group;
            switch (Objects.requireNonNull(analyticsDhisVisualization.scope())) {
                case HOME:
                    group = getGroup(analyticsDhisVisualization.groupUid(), home);

                    if (group == null) {
                        group = createGroup(analyticsDhisVisualization);
                        home.add(group);
                    } else {
                        AnalyticsDhisVisualizationsGroup updatedGroup = updateGroup(group, analyticsDhisVisualization);
                        home.remove(group);
                        home.add(updatedGroup);
                    }
                    break;
                case PROGRAM:
                    group = getGroup(
                            analyticsDhisVisualization.groupUid(),
                            program.get(analyticsDhisVisualization.scopeUid())
                    );

                    if (group == null) {
                        group = createGroup(analyticsDhisVisualization);
                        program.put(
                                analyticsDhisVisualization.scopeUid(),
                                createGroupList(group)
                        );
                    } else {
                        AnalyticsDhisVisualizationsGroup updatedGroup = updateGroup(group, analyticsDhisVisualization);
                        Objects.requireNonNull(program.get(analyticsDhisVisualization.scopeUid())).remove(group);
                        Objects.requireNonNull(program.get(analyticsDhisVisualization.scopeUid())).add(updatedGroup);
                    }

                    break;
                case DATA_SET:
                    group = getGroup(
                            analyticsDhisVisualization.groupUid(),
                            dataSet.get(analyticsDhisVisualization.scopeUid())
                    );

                    if (group == null) {
                        group = createGroup(analyticsDhisVisualization);
                        dataSet.put(
                                analyticsDhisVisualization.scopeUid(),
                                createGroupList(group)
                        );
                    } else {
                        AnalyticsDhisVisualizationsGroup updatedGroup = updateGroup(group, analyticsDhisVisualization);
                        Objects.requireNonNull(dataSet.get(analyticsDhisVisualization.scopeUid())).remove(group);
                        Objects.requireNonNull(dataSet.get(analyticsDhisVisualization.scopeUid())).add(updatedGroup);
                    }
                    break;
                default:
                    break;
            }
        }

        return analyticsDhisVisualizationsSettingBuilder
                .home(home)
                .program(program)
                .dataSet(dataSet)
                .build();
    }

    private AnalyticsDhisVisualizationsGroup getGroup(
            String groupUid,
            List<AnalyticsDhisVisualizationsGroup> groupList
    ) {
        if (groupList != null) {
            for (AnalyticsDhisVisualizationsGroup group : groupList) {
                if (group.id().equals(groupUid)) {
                    return group;
                }
            }
        }

        return null;
    }

    private AnalyticsDhisVisualizationsGroup createGroup(AnalyticsDhisVisualization analyticsDhisVisualization) {
        return AnalyticsDhisVisualizationsGroup
                .builder()
                .id(analyticsDhisVisualization.groupUid())
                .name(analyticsDhisVisualization.groupName())
                .visualizations(new ArrayList<>(Arrays.asList(analyticsDhisVisualization)))
                .build();
    }

    private AnalyticsDhisVisualizationsGroup updateGroup(
            AnalyticsDhisVisualizationsGroup oldGroup,
            AnalyticsDhisVisualization analyticsDhisVisualization
    ) {
        List<AnalyticsDhisVisualization> updatedVisualizations =
                new ArrayList<>(oldGroup.visualizations());
        updatedVisualizations.add(analyticsDhisVisualization);
        return oldGroup
                .toBuilder()
                .visualizations(updatedVisualizations)
                .build();
    }

    private List<AnalyticsDhisVisualizationsGroup> createGroupList(AnalyticsDhisVisualizationsGroup group) {
        return new ArrayList<>(Arrays.asList(group));
    }
}
