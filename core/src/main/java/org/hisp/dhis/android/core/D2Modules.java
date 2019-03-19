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

package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.dataelement.DataElementModule;
import org.hisp.dhis.android.core.dataset.DataSetModule;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceModule;
import org.hisp.dhis.android.core.period.PeriodModule;
import org.hisp.dhis.android.core.program.ProgramModule;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.settings.SystemSettingModule;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.user.UserModule;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class D2Modules {

    final CategoryModule category;
    final DataElementModule dataElement;
    final DataSetModule dataSet;
    final DataValueModule dataValue;
    final EnrollmentModule enrollment;
    final EventModule events;
    final MaintenanceModule maintenance;
    final ProgramModule program;
    final SystemInfoModule systemInfo;
    final SystemSettingModule systemSetting;
    final PeriodModule periodModule;
    final RelationshipModule relationship;
    final TrackedEntityModule trackedEntity;
    final UserModule user;
    final SmsModule sms;

    @Inject
    D2Modules(CategoryModule category,
              DataElementModule dataElement,
              DataSetModule dataSet,
              DataValueModule dataValue,
              EnrollmentModule enrollment,
              EventModule events,
              MaintenanceModule maintenance,
              ProgramModule program,
              SystemInfoModule systemInfo,
              SystemSettingModule systemSetting,
              PeriodModule periodModule,
              RelationshipModule relationship,
              TrackedEntityModule trackedEntity,
              UserModule user,
              SmsModule sms) {
        this.category = category;
        this.dataElement = dataElement;
        this.dataSet = dataSet;
        this.dataValue = dataValue;
        this.enrollment = enrollment;
        this.events = events;
        this.maintenance = maintenance;
        this.program = program;
        this.systemInfo = systemInfo;
        this.systemSetting = systemSetting;
        this.periodModule = periodModule;
        this.relationship = relationship;
        this.trackedEntity = trackedEntity;
        this.user = user;
        this.sms = sms;
    }
}