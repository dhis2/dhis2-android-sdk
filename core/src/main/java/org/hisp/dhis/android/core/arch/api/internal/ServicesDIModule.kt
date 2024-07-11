package org.hisp.dhis.android.core.arch.api.internal

import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelService
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitService
import org.hisp.dhis.android.core.settings.internal.SettingService
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.ApiService
import org.hisp.dhis.android.core.systeminfo.internal.PingService
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeService
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterService
import org.hisp.dhis.android.core.usecase.stock.internal.StockUseCaseService
import org.hisp.dhis.android.core.user.internal.AuthorityService
import org.hisp.dhis.android.core.user.internal.UserService
import org.hisp.dhis.android.core.visualization.internal.TrackerVisualizationService
import org.hisp.dhis.android.core.visualization.internal.VisualizationService
import org.koin.dsl.module
import retrofit2.Retrofit

internal val servicesDIModule = module {
    single { get<Retrofit>().create(ApiService::class.java) }
    single { get<Retrofit>().create(AuthorityService::class.java) }
    single { get<Retrofit>().create(OrganisationUnitLevelService::class.java) }
    single { get<Retrofit>().create(OrganisationUnitService::class.java) }
    single { get<Retrofit>().create(PingService::class.java) }
    single { get<Retrofit>().create(SettingService::class.java) }
    single { get<Retrofit>().create(StockUseCaseService::class.java) }
    single { get<Retrofit>().create(SystemInfoService::class.java) }
    single { get<Retrofit>().create(TrackedEntityAttributeReservedValueService::class.java) }
    single { get<Retrofit>().create(TrackedEntityAttributeService::class.java) }
    single { get<Retrofit>().create(TrackedEntityInstanceFilterService::class.java) }
    single { get<Retrofit>().create(TrackedEntityTypeService::class.java) }
    single { get<Retrofit>().create(TrackerExporterService::class.java) }
    single { get<Retrofit>().create(TrackerVisualizationService::class.java) }
    single { get<Retrofit>().create(UserService::class.java) }
    single { get<Retrofit>().create(VisualizationService::class.java) }
}
