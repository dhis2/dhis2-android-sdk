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
package org.hisp.dhis.android.core

import android.content.Context
import androidx.annotation.VisibleForTesting
import io.ktor.client.HttpClient
import org.hisp.dhis.android.core.analytics.AnalyticsModule
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.d2.internal.D2DIComponent
import org.hisp.dhis.android.core.arch.d2.internal.D2Modules
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.modules.internal.WithProgressDownloader
import org.hisp.dhis.android.core.attribute.AttributeModule
import org.hisp.dhis.android.core.category.CategoryModule
import org.hisp.dhis.android.core.constant.ConstantModule
import org.hisp.dhis.android.core.dataelement.DataElementModule
import org.hisp.dhis.android.core.dataset.DataSetModule
import org.hisp.dhis.android.core.datastore.DataStoreModule
import org.hisp.dhis.android.core.datavalue.DataValueModule
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule
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
import org.hisp.dhis.android.core.settings.SettingModule
import org.hisp.dhis.android.core.sms.SmsModule
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule
import org.hisp.dhis.android.core.usecase.UseCaseModule
import org.hisp.dhis.android.core.user.UserModule
import org.hisp.dhis.android.core.validation.ValidationModule
import org.hisp.dhis.android.core.visualization.VisualizationModule
import org.hisp.dhis.android.core.wipe.internal.WipeModule
import retrofit2.Retrofit

@Suppress("TooManyFunctions")
class D2 internal constructor(internal val d2DIComponent: D2DIComponent) {
    private val modules: D2Modules = d2DIComponent.modules

    @VisibleForTesting
    fun retrofit(): Retrofit {
        return d2DIComponent.retrofit
    }
    @VisibleForTesting
    fun ktor(): HttpClient {
        return d2DIComponent.ktor
    }

    @VisibleForTesting
    internal fun coroutineAPICallExecutor(): CoroutineAPICallExecutor {
        return d2DIComponent.coroutineApiCallExecutor
    }

    fun databaseAdapter(): DatabaseAdapter {
        return d2DIComponent.databaseAdapter
    }

    fun metadataModule(): WithProgressDownloader {
        return d2DIComponent.metadataModule
    }

    fun aggregatedModule(): AggregatedModule {
        return d2DIComponent.aggregatedModule
    }

    fun analyticsModule(): AnalyticsModule {
        return modules.analytics
    }

    fun systemInfoModule(): SystemInfoModule {
        return modules.systemInfo
    }

    @Deprecated(
        "Use settingModule() instead.",
        replaceWith = ReplaceWith(
            expression = "settingModule()",
        ),
    )
    fun systemSettingModule(): SettingModule {
        return modules.settingModule
    }

    fun settingModule(): SettingModule {
        return modules.settingModule
    }

    fun periodModule(): PeriodModule {
        return modules.periodModule
    }

    fun relationshipModule(): RelationshipModule {
        return modules.relationship
    }

    fun categoryModule(): CategoryModule {
        return modules.category
    }

    fun constantModule(): ConstantModule {
        return modules.constant
    }

    fun dataElementModule(): DataElementModule {
        return modules.dataElement
    }

    fun dataSetModule(): DataSetModule {
        return modules.dataSet
    }

    fun optionModule(): OptionModule {
        return modules.option
    }

    fun dataValueModule(): DataValueModule {
        return modules.dataValue
    }

    fun enrollmentModule(): EnrollmentModule {
        return modules.enrollment
    }

    fun eventModule(): EventModule {
        return modules.event
    }

    fun attributeModule(): AttributeModule {
        return modules.attributes
    }

    fun expressionDimensionItemModule(): ExpressionDimensionItemModule {
        return modules.expressionDimensionItem
    }

    fun fileResourceModule(): FileResourceModule {
        return modules.fileResource
    }

    fun iconModule(): IconModule {
        return modules.icon
    }

    fun importModule(): ImportModule {
        return modules.importModule
    }

    fun indicatorModule(): IndicatorModule {
        return modules.indicator
    }

    fun legendSetModule(): LegendSetModule {
        return modules.legendSet
    }

    fun dataStoreModule(): DataStoreModule {
        return modules.dataStore
    }

    fun maintenanceModule(): MaintenanceModule {
        return modules.maintenance
    }

    fun mapsModule(): MapModule {
        return modules.maps
    }

    fun noteModule(): NoteModule {
        return modules.note
    }

    fun programModule(): ProgramModule {
        return modules.program
    }

    fun useCaseModule(): UseCaseModule {
        return modules.useCase
    }

    fun organisationUnitModule(): OrganisationUnitModule {
        return modules.organisationUnit
    }

    fun trackedEntityModule(): TrackedEntityModule {
        return modules.trackedEntity
    }

    fun userModule(): UserModule {
        return modules.user
    }

    fun validationModule(): ValidationModule {
        return modules.validation
    }

    fun visualizationModule(): VisualizationModule {
        return modules.visualization
    }

    fun wipeModule(): WipeModule {
        return d2DIComponent.wipeModule
    }

    fun smsModule(): SmsModule {
        return modules.sms
    }

    internal fun context(): Context {
        return d2DIComponent.appContext
    }
}
