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
package org.hisp.dhis.android.core.arch.d2.internal

import org.hisp.dhis.android.core.analytics.AnalyticsModule
import org.hisp.dhis.android.core.attribute.AttributeModule
import org.hisp.dhis.android.core.category.CategoryModule
import org.hisp.dhis.android.core.constant.ConstantModule
import org.hisp.dhis.android.core.dataelement.DataElementModule
import org.hisp.dhis.android.core.dataset.DataSetModule
import org.hisp.dhis.android.core.datastore.DataStoreModule
import org.hisp.dhis.android.core.datavalue.DataValueModule
import org.hisp.dhis.android.core.enrollment.EnrollmentModule
import org.hisp.dhis.android.core.event.EventModule
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItemModule
import org.hisp.dhis.android.core.fileresource.FileResourceModule
import org.hisp.dhis.android.core.icon.IconModule
import org.hisp.dhis.android.core.imports.internal.ImportModule
import org.hisp.dhis.android.core.indicator.IndicatorModule
import org.hisp.dhis.android.core.legendset.LegendSetModule
import org.hisp.dhis.android.core.maintenance.MaintenanceModule
import org.hisp.dhis.android.core.map.MapModule
import org.hisp.dhis.android.core.note.NoteModule
import org.hisp.dhis.android.core.option.OptionModule
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModule
import org.hisp.dhis.android.core.period.PeriodModule
import org.hisp.dhis.android.core.program.ProgramModule
import org.hisp.dhis.android.core.relationship.RelationshipModule
import org.hisp.dhis.android.core.server.ServerModule
import org.hisp.dhis.android.core.settings.SettingModule
import org.hisp.dhis.android.core.sms.SmsModule
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule
import org.hisp.dhis.android.core.usecase.UseCaseModule
import org.hisp.dhis.android.core.user.UserModule
import org.hisp.dhis.android.core.validation.ValidationModule
import org.hisp.dhis.android.core.visualization.VisualizationModule
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class D2Modules(
    val analytics: AnalyticsModule,
    val attributes: AttributeModule,
    val category: CategoryModule,
    val constant: ConstantModule,
    val dataElement: DataElementModule,
    val dataSet: DataSetModule,
    val option: OptionModule,
    val dataValue: DataValueModule,
    val enrollment: EnrollmentModule,
    val event: EventModule,
    val expressionDimensionItem: ExpressionDimensionItemModule,
    val fileResource: FileResourceModule,
    val importModule: ImportModule,
    val icon: IconModule,
    val indicator: IndicatorModule,
    val legendSet: LegendSetModule,
    val dataStore: DataStoreModule,
    val maintenance: MaintenanceModule,
    val maps: MapModule,
    val note: NoteModule,
    val program: ProgramModule,
    val server: ServerModule,
    val useCase: UseCaseModule,
    val organisationUnit: OrganisationUnitModule,
    val systemInfo: SystemInfoModule,
    val settingModule: SettingModule,
    val periodModule: PeriodModule,
    val relationship: RelationshipModule,
    val trackedEntity: TrackedEntityModule,
    val user: UserModule,
    val validation: ValidationModule,
    val visualization: VisualizationModule,
    val sms: SmsModule,
)
