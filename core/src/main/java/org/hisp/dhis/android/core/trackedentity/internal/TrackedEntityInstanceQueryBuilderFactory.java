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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.apache.commons.lang3.time.DateUtils;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
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
import org.hisp.dhis.android.core.settings.EnrollmentScope;
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
class TrackedEntityInstanceQueryBuilderFactory {

    private final Resource.Type resourceType = Resource.Type.TRACKED_ENTITY_INSTANCE;

    private final ResourceHandler resourceHandler;
    private final UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;
    private final LinkStore<OrganisationUnitProgramLink> organisationUnitProgramLinkStore;
    private final ProgramStoreInterface programStore;
    private final ProgramSettingsObjectRepository programSettingsObjectRepository;

    @Inject
    TrackedEntityInstanceQueryBuilderFactory(
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

    List<TeiQuery.Builder> getTeiQueryBuilders(ProgramDataDownloadParams params) {

        String lastUpdated = params.uids().isEmpty() ? resourceHandler.getLastUpdated(resourceType) : null;

        ProgramSettings programSettings = programSettingsObjectRepository.blockingGet();

        List<TeiQuery.Builder> builders = new ArrayList<>();

        if (params.program() == null) {
            List<String> trackerPrograms = programStore.getUidsByProgramType(ProgramType.WITH_REGISTRATION);
            if (hasLimitByProgram(params, programSettings)) {
                for (String programUid : trackerPrograms) {
                    builders.addAll(queryPerProgram(params, programSettings, programUid, lastUpdated));
                }
            } else {
                Map<String, ProgramSetting> specificSettings = programSettings == null ?
                        Collections.emptyMap() : programSettings.specificSettings();

                for (Map.Entry<String, ProgramSetting> specificSetting : specificSettings.entrySet()) {
                    String programUid = specificSetting.getKey();
                    if (trackerPrograms.contains(programUid)) {
                        builders.addAll(queryPerProgram(params, programSettings, programUid, lastUpdated));
                    }
                }

                builders.addAll(queryGlobal(params, programSettings, lastUpdated));
            }
        } else {
            builders.addAll(queryPerProgram(params, programSettings, params.program(), lastUpdated));
        }

        return builders;
    }

    private List<TeiQuery.Builder> queryPerProgram(ProgramDataDownloadParams params,
                                                   ProgramSettings programSettings,
                                                   String programUid,
                                                   String globalLastUpdated) {
        int limit = getLimit(params, programSettings, programUid);

        if (limit == 0) {
            return Collections.emptyList();
        }

        EnrollmentStatus programStatus = getProgramStatus(params, programSettings, programUid);
        String programStartDate = getProgramStartDate(programSettings, programUid);

        String lastUpdated = globalLastUpdated == null && params.uids().isEmpty() ?
                getInitialLastpdated(programSettings, programUid) : globalLastUpdated;

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

        List<TeiQuery.Builder> builders = new ArrayList<>();

        if (hasLimitByOrgunit) {
            for (String orgUnitUid : orgUnits) {
                builders.add(getBuilderFor(lastUpdated, Collections.singletonList(orgUnitUid), ouMode, params, limit)
                        .program(programUid).programStatus(programStatus).programStartDate(programStartDate));
            }
        } else {
            builders.add(getBuilderFor(lastUpdated, orgUnits, ouMode, params, limit)
                    .program(programUid).programStatus(programStatus).programStartDate(programStartDate));
        }

        return builders;
    }

    private List<TeiQuery.Builder> queryGlobal(ProgramDataDownloadParams params,
                                               ProgramSettings programSettings,
                                               String globalLastUpdated) {
        int limit = getLimit(params, programSettings, null);

        if (limit == 0) {
            return Collections.emptyList();
        }

        String lastUpdated = globalLastUpdated == null && params.uids().isEmpty() ?
                getInitialLastpdated(programSettings, null) : globalLastUpdated;

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

        List<TeiQuery.Builder> builders = new ArrayList<>();

        if (hasLimitByOrgunit) {
            for (String orgUnitUid : orgUnits) {
                builders.add(getBuilderFor(lastUpdated, Collections.singletonList(orgUnitUid), ouMode, params, limit));
            }
        } else {
            builders.add(getBuilderFor(lastUpdated, orgUnits, ouMode, params, limit));
        }

        return builders;

    }

    private TeiQuery.Builder getBuilderFor(String lastUpdated, List<String> organisationUnits,
                                           OrganisationUnitMode organisationUnitMode,
                                           ProgramDataDownloadParams params,
                                           int limit) {
        return TeiQuery.builder()
                .lastUpdatedStartDate(lastUpdated)
                .orgUnits(organisationUnits)
                .ouMode(organisationUnitMode)
                .uids(params.uids())
                .limit(limit);
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
                    return scope.equals(LimitScope.PER_ORG_UNIT);
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
            if (specificSetting != null && specificSetting.teiDownload() != null) {
                return specificSetting.teiDownload();
            }

            ProgramSetting globalSetting = programSettings.globalSettings();
            if (globalSetting != null && globalSetting.teiDownload() != null) {
                return globalSetting.teiDownload();
            }
        }

        return ProgramDataDownloadParams.DEFAULT_LIMIT;
    }

    private EnrollmentStatus getProgramStatus(ProgramDataDownloadParams params,
                                              ProgramSettings programSettings,
                                              String programUid) {

        if (params.programStatus() != null && isGlobalOrUserDefinedProgram(params, programUid)) {
            return enrollmentScopeToProgramStatus(params.programStatus());
        }

        if (programSettings != null) {
            ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
            if (specificSetting != null && specificSetting.enrollmentDownload() != null) {
                return enrollmentScopeToProgramStatus(specificSetting.enrollmentDownload());
            }

            ProgramSetting globalSetting = programSettings.globalSettings();
            if (globalSetting != null && globalSetting.enrollmentDownload() != null) {
                return enrollmentScopeToProgramStatus(globalSetting.enrollmentDownload());
            }
        }

        return null;
    }

    private String getProgramStartDate(ProgramSettings programSettings, String programUid) {
        DownloadPeriod period = null;
        if (programSettings != null) {
            ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
            ProgramSetting globalSetting = programSettings.globalSettings();

            if (hasEnrollmentDateDownload(specificSetting)) {
                period = specificSetting.enrollmentDateDownload();
            } else if (hasEnrollmentDateDownload(globalSetting)) {
                period = globalSetting.enrollmentDateDownload();
            }
        }

        if (period == null || period == DownloadPeriod.ANY) {
            return null;
        } else {
            Date programStartDate = DateUtils.addMonths(new Date(), -period.getMonths());
            return BaseIdentifiableObject.dateToSpaceDateStr(programStartDate);
        }
    }

    private String getInitialLastpdated(ProgramSettings programSettings, String programUid) {
        DownloadPeriod period = null;
        if (programSettings != null) {
            ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
            ProgramSetting globalSetting = programSettings.globalSettings();

            if (hasUpdateDownload(specificSetting)) {
                period = specificSetting.updateDownload();
            } else if (hasUpdateDownload(globalSetting)) {
                period = globalSetting.updateDownload();
            }
        }

        if (period == null || period == DownloadPeriod.ANY) {
            return null;
        } else {
            Date initialLastUpdated = DateUtils.addMonths(new Date(), -period.getMonths());
            return BaseIdentifiableObject.dateToSpaceDateStr(initialLastUpdated);
        }
    }

    private EnrollmentStatus enrollmentScopeToProgramStatus(EnrollmentScope enrollmentScope) {
        if (enrollmentScope != null && enrollmentScope.equals(EnrollmentScope.ONLY_ACTIVE)) {
            return EnrollmentStatus.ACTIVE;
        } else {
            return null;
        }
    }

    private boolean hasEnrollmentDateDownload(ProgramSetting programSetting) {
        return programSetting != null && programSetting.enrollmentDateDownload() != null;
    }

    private boolean hasUpdateDownload(ProgramSetting programSetting) {
        return programSetting != null && programSetting.updateDownload() != null;
    }

    private boolean isGlobalOrUserDefinedProgram(ProgramDataDownloadParams params, String programUid) {
        return programUid == null || programUid.equals(params.program());
    }
}