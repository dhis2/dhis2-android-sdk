package org.hisp.dhis.android.core.user

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("Test for db deletion on user disabled. Only to be executed on demand")
@RunWith(D2JunitRunner::class)
class UserDisabledRxAPIMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    @Test
    fun delete_database_when_user_disabled_on_rx_api_call_executor() {
        dhis2MockServer.enqueueMockResponse(401, "user/user_disabled.json")
        assertThat(d2.userModule().accountManager().getAccounts().size).isEqualTo(1)

        try {
            d2.eventModule().eventDownloader().blockingDownload()
        } catch (e: Exception) {
            val d2Error = e.cause as D2Error
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.USER_ACCOUNT_DISABLED)
        }
        assertThat(d2.userModule().accountManager().getAccounts().size).isEqualTo(0)
    }
}
