/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.event.internal;

import org.apache.commons.lang3.time.DateUtils;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.settings.DownloadPeriod;
import org.hisp.dhis.android.core.settings.LimitScope;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettings;
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
@SuppressWarnings({"PMD.GodClass"})
class EventQueryBundleFactory {

    private final Resource.Type resourceType = Resource.Type.EVENT;

    private final ResourceHandler resourceHandler;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final LinkStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore;
    private final ProgramStoreInterface programStore;
    private final ProgramSettingsObjectRepository programSettingsObjectRepository;

    @Inject
    EventQueryBundleFactory(
            ResourceHandler resourceHandler,
            UserOrganisationUnitLinkStore userOrganisationUnitLinkStore,
            LinkStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore,
            ProgramStoreInterface programStore,
            ProgramSettingsObjectRepository programSettingsObjectRepository) {
        this.resourceHandler = resourceHandler;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.organisationUnitProgramLinkStore = organisationUnitProgramLinkStore;
        this.programStore = programStore;
        this.programSettingsObjectRepository = programSettingsObjectRepository;
    }

    List<EventQueryBundle> getEventQueryBundles(ProgramDataDownloadParams params) {

        String lastUpdated = params.uids().isEmpty() ? resourceHandler.getLastUpdated(resourceType) : null;

        ProgramSettings programSettings = programSettingsObjectRepository.blockingGet();

        List<EventQueryBundle> builders = new ArrayList<>();

        if (params.program() == null) {
            List<String> eventPrograms = programStore.getUidsByProgramType(ProgramType.WITHOUT_REGISTRATION);
            if (hasLimitByProgram(params, programSettings)) {
                for (String programUid : eventPrograms) {
                    builders.addAll(queryPerProgram(params, programSettings, programUid, lastUpdated));
                }
            } else {
                Map<String, ProgramSetting> specificSettings = programSettings == null ?
                        Collections.emptyMap() : programSettings.specificSettings();

                for (Map.Entry<String, ProgramSetting> specificSetting : specificSettings.entrySet()) {
                    String programUid = specificSetting.getKey();
                    if (eventPrograms.contains(programUid)) {
                        builders.addAll(queryPerProgram(params, programSettings, programUid, lastUpdated));
                        eventPrograms.remove(programUid);
                    }
                }

                builders.addAll(queryGlobal(params, programSettings, eventPrograms, lastUpdated));
            }
        } else {
            builders.addAll(queryPerProgram(params, programSettings, params.program(), lastUpdated));
        }

        return builders;
    }

    private List<EventQueryBundle> queryPerProgram(ProgramDataDownloadParams params,
                                                   ProgramSettings programSettings,
                                                   String programUid,
                                                   String lastUpdated) {
        int limit = getLimit(params, programSettings, programUid);

        if (limit == 0) {
            return Collections.emptyList();
        }

        String eventStartDate = getEventStartDate(programSettings, programUid);
        List<String> programs = Collections.singletonList(programUid);
        OrganisationUnitMode ouMode;
        List<String> orgUnits;

        boolean hasLimitByOrgunit = hasLimitByOrgUnit(params, programSettings, programUid);

        if (params.orgUnits().size() > 0) {
            ouMode = OrganisationUnitMode.SELECTED;
            orgUnits = params.orgUnits();
        } else if (hasLimitByOrgunit) {
            ouMode = OrganisationUnitMode.SELECTED;
            orgUnits = getLinkedCaptureOrgUnitUids(programUid);
        } else {
            ouMode = OrganisationUnitMode.DESCENDANTS;
            orgUnits = getRootCaptureOrgUnitUids();
        }

        List<EventQueryBundle> builders = new ArrayList<>();

        if (hasLimitByOrgunit) {
            for (String orgUnitUid : orgUnits) {
                builders.add(getBuilderFor(lastUpdated, Collections.singletonList(orgUnitUid), programs, ouMode,
                        eventStartDate, limit));
            }
        } else {
            builders.add(getBuilderFor(lastUpdated, orgUnits, programs, ouMode, eventStartDate, limit));
        }

        return builders;
    }

    private List<EventQueryBundle> queryGlobal(ProgramDataDownloadParams params,
                                               ProgramSettings programSettings,
                                               List<String> programList,
                                               String lastUpdated) {
        int limit = getLimit(params, programSettings, null);

        if (limit == 0) {
            return Collections.emptyList();
        }

        String eventStartDate = getEventStartDate(programSettings, null);
        OrganisationUnitMode ouMode;
        List<String> orgUnits;

        boolean hasLimitByOrgunit = hasLimitByOrgUnit(params, programSettings, null);

        if (params.orgUnits().size() > 0) {
            ouMode = OrganisationUnitMode.SELECTED;
            orgUnits = params.orgUnits();
        } else if (hasLimitByOrgunit) {
            ouMode = OrganisationUnitMode.SELECTED;
            orgUnits = getCaptureOrgUnitUids();
        } else {
            ouMode = OrganisationUnitMode.DESCENDANTS;
            orgUnits = getRootCaptureOrgUnitUids();
        }

        List<EventQueryBundle> builders = new ArrayList<>();

        if (hasLimitByOrgunit) {
            for (String orgUnitUid : orgUnits) {
                builders.add(getBuilderFor(lastUpdated, Collections.singletonList(orgUnitUid), programList, ouMode,
                        eventStartDate, limit));
            }
        } else {
            builders.add(getBuilderFor(lastUpdated, orgUnits, programList, ouMode, eventStartDate, limit));
        }

        return builders;

    }

    private EventQueryBundle getBuilderFor(String lastUpdated,
                                           List<String> organisationUnits,
                                           List<String> programs,
                                           OrganisationUnitMode organisationUnitMode,
                                           String eventStartDate,
                                           int limit) {
        return EventQueryBundle.builder()
                .lastUpdatedStartDate(lastUpdated)
                .orgUnitList(organisationUnits)
                .ouMode(organisationUnitMode)
                .programList(programs)
                .limit(limit)
                .eventStartDate(eventStartDate)
                .build();
    }

    private List<String> getRootCaptureOrgUnitUids() {
        return userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids();
    }

    private List<String> getCaptureOrgUnitUids() {
        return userOrganisationUnitLinkStore
                .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);
    }

    private List<String> getLinkedCaptureOrgUnitUids(String programUid) {
        List<String> ous = getCaptureOrgUnitUids();

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM, programUid)
                .appendInKeyStringValues(OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT, ous)
                .build();

        List<String> linkedOrgunits = new ArrayList<>();
        for (OrganisationUnitProgramLink link : organisationUnitProgramLinkStore.selectWhere(whereClause)) {
            linkedOrgunits.add(link.organisationUnit());
        }

        return linkedOrgunits;
    }

    private boolean hasLimitByProgram(ProgramDataDownloadParams params, ProgramSettings programSettings) {
        if (params.limitByProgram() != null) {
            return params.limitByProgram();
        }

        if (programSettings != null && programSettings.globalSettings() != null) {
            LimitScope scope = programSettings.globalSettings().settingDownload();

            if (scope != null) {
                return scope.equals(LimitScope.PER_OU_AND_PROGRAM) || scope.equals(LimitScope.PER_PROGRAM);
            }
        }

        return false;
    }

    private boolean hasLimitByOrgUnit(ProgramDataDownloadParams params, ProgramSettings programSettings,
                                      String programUid) {
        if (params.limitByOrgunit() != null) {
            return params.limitByOrgunit();
        }

        if (programSettings != null) {
            ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);

            if (specificSetting != null) {
                LimitScope scope = specificSetting.settingDownload();

                if (scope != null) {
                    return scope.equals(LimitScope.ALL_ORG_UNITS);
                }
            }

            if (programSettings.globalSettings() != null) {
                LimitScope scope = programSettings.globalSettings().settingDownload();

                if (scope != null) {
                    return scope.equals(LimitScope.PER_OU_AND_PROGRAM) || scope.equals(LimitScope.PER_ORG_UNIT);
                }
            }
        }

        return false;
    }

    private int getLimit(ProgramDataDownloadParams params,
                         ProgramSettings programSettings,
                         String programUid) {
        if (params.limit() != null && isGlobalOrUserDefinedProgram(params, programUid)) {
            return params.limit();
        }

        if (programSettings != null) {
            ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
            if (specificSetting != null && specificSetting.eventsDownload() != null) {
                return specificSetting.eventsDownload();
            }

            ProgramSetting globalSetting = programSettings.globalSettings();
            if (globalSetting != null && globalSetting.eventsDownload() != null) {
                return globalSetting.eventsDownload();
            }
        }

        return ProgramDataDownloadParams.DEFAULT_LIMIT;
    }

    private String getEventStartDate(ProgramSettings programSettings, String programUid) {
        DownloadPeriod period = null;
        if (programSettings != null) {
            ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
            ProgramSetting globalSetting = programSettings.globalSettings();

            if (hasEventDateDownload(specificSetting)) {
                period = specificSetting.eventDateDownload();
            } else if (hasEventDateDownload(globalSetting)) {
                period = globalSetting.eventDateDownload();
            }
        }

        if (period == null || period == DownloadPeriod.ANY) {
            return null;
        } else {
            Date eventStartDate = DateUtils.addMonths(new Date(), -period.getMonths());
            return BaseIdentifiableObject.dateToSpaceDateStr(eventStartDate);
        }
    }

    private boolean hasEventDateDownload(ProgramSetting programSetting) {
        return programSetting != null && programSetting.eventDateDownload() != null;
    }

    private boolean isGlobalOrUserDefinedProgram(ProgramDataDownloadParams params, String programUid) {
        return programUid == null || programUid.equals(params.program());
    }
}