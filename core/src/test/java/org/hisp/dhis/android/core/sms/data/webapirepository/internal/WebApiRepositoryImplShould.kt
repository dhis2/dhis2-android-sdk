package org.hisp.dhis.android.core.sms.data.webapirepository.internal

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WebApiRepositoryImplShould {

    private val apiService: ApiService = mock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val defalutMetadataConfig = GetMetadataIdsConfig()

    private lateinit var testWebRepository: WebApiRepositoryImpl

    @Before
    fun init() {
        testWebRepository = WebApiRepositoryImpl(apiService, dhisVersionManager)
    }

    @Test
    fun `Include users query if version lower than 2_35`() {
        whenever(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35)) doReturn false

        testWebRepository.metadataCall(defalutMetadataConfig)
        verify(apiService).getMetadataIds(
            notNull(), notNull(), notNull(), notNull(), notNull(),
            notNull(), notNull()
        )
    }

    @Test
    fun `Exclude users if version greater or equal to 2_35`() {
        whenever(dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35)) doReturn true

        testWebRepository.metadataCall(defalutMetadataConfig)
        verify(apiService).getMetadataIds(
            notNull(), notNull(), notNull(), isNull(), notNull(),
            notNull(), notNull()
        )
    }
}
