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

package org.hisp.dhis.android.core.arch.d2.internal;

import org.hisp.dhis.android.core.analytics.AnalyticsModule;
import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.constant.ConstantModule;
import org.hisp.dhis.android.core.dataelement.DataElementModule;
import org.hisp.dhis.android.core.dataset.DataSetModule;
import org.hisp.dhis.android.core.datastore.DataStoreModule;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.fileresource.FileResourceModule;
import org.hisp.dhis.android.core.imports.internal.ImportModule;
import org.hisp.dhis.android.core.indicator.IndicatorModule;
import org.hisp.dhis.android.core.legendset.LegendSetModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceModule;
import org.hisp.dhis.android.core.note.NoteModule;
import org.hisp.dhis.android.core.option.OptionModule;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModule;
import org.hisp.dhis.android.core.period.PeriodModule;
import org.hisp.dhis.android.core.program.ProgramModule;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.settings.SettingModule;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.android.core.validation.ValidationModule;
import org.hisp.dhis.android.core.visualization.VisualizationModule;

import javax.inject.Inject;

import dagger.Reusable;

@SuppressWarnings({"PMD.TooManyFields"})
@Reusable
public final class D2Modules {

    public final AnalyticsModule analytics;
    public final CategoryModule category;
    public final ConstantModule constant;
    public final DataElementModule dataElement;
    public final DataSetModule dataSet;
    public final OptionModule option;
    public final DataValueModule dataValue;
    public final EnrollmentModule enrollment;
    public final EventModule event;
    public final FileResourceModule fileResource;
    public final ImportModule importModule;
    public final IndicatorModule indicator;
    public final LegendSetModule legendSet;
    public final DataStoreModule dataStore;
    public final MaintenanceModule maintenance;
    public final NoteModule note;
    public final ProgramModule program;
    public final OrganisationUnitModule organisationUnit;
    public final SystemInfoModule systemInfo;
    public final SettingModule settingModule;
    public final PeriodModule periodModule;
    public final RelationshipModule relationship;
    public final TrackedEntityModule trackedEntity;
    public final UserModule user;
    public final ValidationModule validation;
    public final VisualizationModule visualization;
    public final SmsModule sms;

    @Inject
    public D2Modules(AnalyticsModule analytics,
                     CategoryModule category,
                     ConstantModule constant,
                     DataElementModule dataElement,
                     DataSetModule dataSet,
                     OptionModule option,
                     DataValueModule dataValue,
                     EnrollmentModule enrollment,
                     EventModule event,
                     FileResourceModule fileResource,
                     ImportModule importModule,
                     IndicatorModule indicator,
                     LegendSetModule legendSet,
                     DataStoreModule dataStore,
                     MaintenanceModule maintenance,
                     NoteModule note,
                     ProgramModule program,
                     OrganisationUnitModule organisationUnit,
                     SystemInfoModule systemInfo,
                     SettingModule settingModule,
                     PeriodModule periodModule,
                     RelationshipModule relationship,
                     TrackedEntityModule trackedEntity,
                     UserModule user,
                     ValidationModule validation,
                     VisualizationModule visualization,
                     SmsModule sms) {
        this.analytics = analytics;
        this.category = category;
        this.constant = constant;
        this.dataElement = dataElement;
        this.option = option;
        this.dataSet = dataSet;
        this.dataValue = dataValue;
        this.enrollment = enrollment;
        this.event = event;
        this.fileResource = fileResource;
        this.importModule = importModule;
        this.indicator = indicator;
        this.legendSet = legendSet;
        this.dataStore = dataStore;
        this.maintenance = maintenance;
        this.note = note;
        this.program = program;
        this.organisationUnit = organisationUnit;
        this.systemInfo = systemInfo;
        this.settingModule = settingModule;
        this.periodModule = periodModule;
        this.relationship = relationship;
        this.trackedEntity = trackedEntity;
        this.user = user;
        this.validation = validation;
        this.visualization = visualization;
        this.sms = sms;
    }
}