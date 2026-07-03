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
package org.hisp.dhis.android.core.wipe.internal

import org.hisp.dhis.android.core.analytics.AnalyticsDIModule
import org.hisp.dhis.android.core.arch.ArchDIModule
import org.hisp.dhis.android.core.attribute.AttributeDIModule
import org.hisp.dhis.android.core.category.CategoryDIModule
import org.hisp.dhis.android.core.common.CommonDIModule
import org.hisp.dhis.android.core.configuration.internal.ConfigurationDIModule
import org.hisp.dhis.android.core.constant.ConstantDIModule
import org.hisp.dhis.android.core.dataapproval.DataApprovalDIModule
import org.hisp.dhis.android.core.dataelement.DataElementDIModule
import org.hisp.dhis.android.core.dataset.DataSetDIModule
import org.hisp.dhis.android.core.datastore.DataStoreDIModule
import org.hisp.dhis.android.core.datavalue.DataValueDIModule
import org.hisp.dhis.android.core.domain.DomainDIModule
import org.hisp.dhis.android.core.enrollment.EnrollmentDIModule
import org.hisp.dhis.android.core.event.EventDIModule
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItemDIModule
import org.hisp.dhis.android.core.fileresource.FileResourceDIModule
import org.hisp.dhis.android.core.icon.IconDIModule
import org.hisp.dhis.android.core.imports.ImportsDIModule
import org.hisp.dhis.android.core.indicator.IndicatorDIModule
import org.hisp.dhis.android.core.legendset.LegendSetDIModule
import org.hisp.dhis.android.core.maintenance.MaintenanceDIModule
import org.hisp.dhis.android.core.map.MapDIModule
import org.hisp.dhis.android.core.note.NoteDIModule
import org.hisp.dhis.android.core.option.OptionDIModule
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitDIModule
import org.hisp.dhis.android.core.parser.internal.ParserDIModule
import org.hisp.dhis.android.core.period.PeriodDIModule
import org.hisp.dhis.android.core.program.ProgramDIModule
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListDIModule
import org.hisp.dhis.android.core.relationship.RelationshipDIModule
import org.hisp.dhis.android.core.resource.internal.ResourceDIModule
import org.hisp.dhis.android.core.server.ServerDIModule
import org.hisp.dhis.android.core.settings.SettingsDIModule
import org.hisp.dhis.android.core.sms.SmsDIModule
import org.hisp.dhis.android.core.systeminfo.SystemInfoDIModule
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDIModule
import org.hisp.dhis.android.core.tracker.TrackerDIModule
import org.hisp.dhis.android.core.usecase.UseCaseDIModule
import org.hisp.dhis.android.core.user.UserDIModule
import org.hisp.dhis.android.core.validation.ValidationDIModule
import org.hisp.dhis.android.core.visualization.VisualizationDIModule
import org.hisp.dhis.android.persistence.dependencyinjection.PersistenceDIModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

// The wipe module aggregates the ModuleWiper of every feature module (see D2ModuleWipers), so it
// depends on all of them. Declaring the includes explicitly also allows the Koin compile-time
// dependency verification to resolve them deterministically.
@Module(
    includes = [
        AnalyticsDIModule::class,
        ArchDIModule::class,
        AttributeDIModule::class,
        CategoryDIModule::class,
        CommonDIModule::class,
        ConfigurationDIModule::class,
        ConstantDIModule::class,
        DataApprovalDIModule::class,
        DataElementDIModule::class,
        DataSetDIModule::class,
        DataStoreDIModule::class,
        DataValueDIModule::class,
        DomainDIModule::class,
        EnrollmentDIModule::class,
        EventDIModule::class,
        ExpressionDimensionItemDIModule::class,
        FileResourceDIModule::class,
        IconDIModule::class,
        ImportsDIModule::class,
        IndicatorDIModule::class,
        LegendSetDIModule::class,
        MaintenanceDIModule::class,
        MapDIModule::class,
        NoteDIModule::class,
        OptionDIModule::class,
        OrganisationUnitDIModule::class,
        ParserDIModule::class,
        PeriodDIModule::class,
        PersistenceDIModule::class,
        ProgramDIModule::class,
        ProgramStageWorkingListDIModule::class,
        RelationshipDIModule::class,
        ResourceDIModule::class,
        ServerDIModule::class,
        SettingsDIModule::class,
        SmsDIModule::class,
        SystemInfoDIModule::class,
        TrackedEntityDIModule::class,
        TrackerDIModule::class,
        UseCaseDIModule::class,
        UserDIModule::class,
        ValidationDIModule::class,
        VisualizationDIModule::class,
    ],
)
@ComponentScan
internal class WipeDIModule
