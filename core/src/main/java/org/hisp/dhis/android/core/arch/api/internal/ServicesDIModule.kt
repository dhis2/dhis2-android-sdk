package org.hisp.dhis.android.core.arch.api.internal

import org.hisp.dhis.android.core.settings.internal.SettingService
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.ApiService
import org.hisp.dhis.android.core.systeminfo.internal.PingService
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoService
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterService
import org.hisp.dhis.android.core.user.internal.AuthorityService
import org.hisp.dhis.android.core.user.internal.UserService
import org.koin.dsl.module
import retrofit2.Retrofit

internal val servicesDIModule = module {
    single { get<Retrofit>().create(ApiService::class.java) }
    single { get<Retrofit>().create(AuthorityService::class.java) }
    single { get<Retrofit>().create(PingService::class.java) }
    single { get<Retrofit>().create(SettingService::class.java) }
    single { get<Retrofit>().create(SystemInfoService::class.java) }
    single { get<Retrofit>().create(TrackerExporterService::class.java) }
    single { get<Retrofit>().create(UserService::class.java) }
}
