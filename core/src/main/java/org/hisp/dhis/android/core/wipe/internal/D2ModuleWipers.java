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

package org.hisp.dhis.android.core.wipe.internal;

import org.hisp.dhis.android.core.attribute.internal.AttributeModuleWiper;
import org.hisp.dhis.android.core.category.internal.CategoryModuleWiper;
import org.hisp.dhis.android.core.common.internal.CommonModuleWiper;
import org.hisp.dhis.android.core.constant.internal.ConstantModuleWiper;
import org.hisp.dhis.android.core.dataelement.internal.DataElementModuleWiper;
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleWiper;
import org.hisp.dhis.android.core.datastore.internal.LocalDataStoreModuleWiper;
import org.hisp.dhis.android.core.datavalue.internal.DataValueModuleWiper;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentModuleWiper;
import org.hisp.dhis.android.core.event.internal.EventModuleWiper;
import org.hisp.dhis.android.core.fileresource.internal.FileResourceModuleWiper;
import org.hisp.dhis.android.core.imports.internal.ImportModuleWiper;
import org.hisp.dhis.android.core.indicator.internal.IndicatorModuleWiper;
import org.hisp.dhis.android.core.legendset.internal.LegendSetModuleWiper;
import org.hisp.dhis.android.core.maintenance.internal.MaintenanceModuleWiper;
import org.hisp.dhis.android.core.option.internal.OptionModuleWiper;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleWiper;
import org.hisp.dhis.android.core.period.internal.PeriodModuleWiper;
import org.hisp.dhis.android.core.program.internal.ProgramModuleWiper;
import org.hisp.dhis.android.core.relationship.internal.RelationshipModuleWiper;
import org.hisp.dhis.android.core.resource.internal.ResourceModuleWiper;
import org.hisp.dhis.android.core.settings.internal.SettingModuleWiper;
import org.hisp.dhis.android.core.sms.internal.SMSModuleWiper;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleWiper;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityModuleWiper;
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobModuleWiper;
import org.hisp.dhis.android.core.user.internal.UserModuleWiper;
import org.hisp.dhis.android.core.validation.internal.ValidationModuleWiper;
import org.hisp.dhis.android.core.visualization.internal.VisualizationModuleWiper;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
@SuppressWarnings({"PMD.ExcessiveImports"})
final class D2ModuleWipers {
    final List<ModuleWiper> wipers;

    @Inject
    D2ModuleWipers(
            CategoryModuleWiper category,
            CommonModuleWiper common,
            ConstantModuleWiper constant,
            DataElementModuleWiper dataElement,
            DataSetModuleWiper dataSet,
            DataValueModuleWiper dataValue,
            ValidationModuleWiper validation,

            EnrollmentModuleWiper enrollment,
            EventModuleWiper event,
            FileResourceModuleWiper fileResource,
            ImportModuleWiper importModule,
            IndicatorModuleWiper indicator,
            LegendSetModuleWiper legendSet,
            LocalDataStoreModuleWiper localDataStore,
            MaintenanceModuleWiper maintenance,

            OptionModuleWiper option,
            OrganisationUnitModuleWiper organisationUnit,
            PeriodModuleWiper period,
            ProgramModuleWiper program,
            RelationshipModuleWiper relationship,

            ResourceModuleWiper resource,
            SystemInfoModuleWiper systemInfo,
            SettingModuleWiper systemSetting,
            SMSModuleWiper smsModuleWiper,
            UserModuleWiper user,
            TrackedEntityModuleWiper trackedEntity,
            AttributeModuleWiper attribute,
            TrackerJobModuleWiper trackerJob,
            VisualizationModuleWiper visualization) {

        this.wipers = Arrays.asList(
                category,
                common,
                constant,
                dataElement,
                dataSet,
                dataValue,
                validation,

                enrollment,
                event,
                fileResource,
                importModule,
                indicator,
                legendSet,
                localDataStore,
                maintenance,

                option,
                organisationUnit,
                period,
                program,
                relationship,

                resource,
                systemInfo,
                systemSetting,
                smsModuleWiper,
                user,
                trackedEntity,
                attribute,
                trackerJob,
                visualization);
    }
}