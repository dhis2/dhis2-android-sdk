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
package org.hisp.dhis.android.core.wipe.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.attribute.internal.AttributeModuleWiper
import org.hisp.dhis.android.core.category.internal.CategoryModuleWiper
import org.hisp.dhis.android.core.common.internal.CommonModuleWiper
import org.hisp.dhis.android.core.constant.internal.ConstantModuleWiper
import org.hisp.dhis.android.core.dataelement.internal.DataElementModuleWiper
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleWiper
import org.hisp.dhis.android.core.datastore.internal.LocalDataStoreModuleWiper
import org.hisp.dhis.android.core.datavalue.internal.DataValueModuleWiper
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentModuleWiper
import org.hisp.dhis.android.core.event.internal.EventModuleWiper
import org.hisp.dhis.android.core.fileresource.internal.FileResourceModuleWiper
import org.hisp.dhis.android.core.imports.internal.ImportModuleWiper
import org.hisp.dhis.android.core.indicator.internal.IndicatorModuleWiper
import org.hisp.dhis.android.core.legendset.internal.LegendSetModuleWiper
import org.hisp.dhis.android.core.maintenance.internal.MaintenanceModuleWiper
import org.hisp.dhis.android.core.map.internal.MapModuleWiper
import org.hisp.dhis.android.core.option.internal.OptionModuleWiper
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleWiper
import org.hisp.dhis.android.core.period.internal.PeriodModuleWiper
import org.hisp.dhis.android.core.program.internal.ProgramModuleWiper
import org.hisp.dhis.android.core.usecase.internal.UseCaseModuleWiper
import org.hisp.dhis.android.core.relationship.internal.RelationshipModuleWiper
import org.hisp.dhis.android.core.resource.internal.ResourceModuleWiper
import org.hisp.dhis.android.core.settings.internal.SettingModuleWiper
import org.hisp.dhis.android.core.sms.internal.SMSModuleWiper
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleWiper
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityModuleWiper
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobModuleWiper
import org.hisp.dhis.android.core.user.internal.UserModuleWiper
import org.hisp.dhis.android.core.validation.internal.ValidationModuleWiper
import org.hisp.dhis.android.core.visualization.internal.VisualizationModuleWiper

@Reusable
@Suppress("LongParameterList")
internal class D2ModuleWipers @Inject constructor(
        attribute: AttributeModuleWiper,
        category: CategoryModuleWiper,
        common: CommonModuleWiper,
        constant: ConstantModuleWiper,
        dataElement: DataElementModuleWiper,
        dataSet: DataSetModuleWiper,
        dataValue: DataValueModuleWiper,
        enrollment: EnrollmentModuleWiper,
        event: EventModuleWiper,
        fileResource: FileResourceModuleWiper,
        importModule: ImportModuleWiper,
        indicator: IndicatorModuleWiper,
        legendSet: LegendSetModuleWiper,
        localDataStore: LocalDataStoreModuleWiper,
        maintenance: MaintenanceModuleWiper,
        map: MapModuleWiper,
        option: OptionModuleWiper,
        organisationUnit: OrganisationUnitModuleWiper,
        period: PeriodModuleWiper,
        program: ProgramModuleWiper,
        useCase: UseCaseModuleWiper,
        relationship: RelationshipModuleWiper,
        resource: ResourceModuleWiper,
        smsModuleWiper: SMSModuleWiper,
        systemInfo: SystemInfoModuleWiper,
        systemSetting: SettingModuleWiper,
        trackedEntity: TrackedEntityModuleWiper,
        trackerJob: TrackerJobModuleWiper,
        user: UserModuleWiper,
        validation: ValidationModuleWiper,
        visualization: VisualizationModuleWiper
) {
    val wipers: List<ModuleWiper>

    init {
        wipers = listOf(
            attribute,
            category,
            common,
            constant,
            dataElement,
            dataSet,
            dataValue,
            enrollment,
            event,
            fileResource,
            importModule,
            indicator,
            legendSet,
            localDataStore,
            maintenance,
            map,
            option,
            organisationUnit,
            period,
            program,
            useCase,
            relationship,
            resource,
            smsModuleWiper,
            systemInfo,
            systemSetting,
            trackedEntity,
            trackerJob,
            user,
            validation,
            visualization
        )
    }
}
