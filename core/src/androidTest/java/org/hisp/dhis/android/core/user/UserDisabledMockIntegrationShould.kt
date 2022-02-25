package org.hisp.dhis.android.core.user

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class UserDisabledMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun delete_database_when_user_disabled() {
        // Enqueue user disabled response
        dhis2MockServer.enqueueMockResponse(401, "user/user_disabled.json")
        addDummyData()
        assertThat(d2.userModule().accountManager().getAccounts().size).isEqualTo(1)

        try {
            d2.dataValueModule().dataValues().blockingUpload()
        } catch (e: Exception) {
            val d2Error = e.cause as D2Error
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.USER_ACCOUNT_DISABLED)
        }
        assertThat(d2.userModule().accountManager().getAccounts().size).isEqualTo(0)
    }

    @Test
    fun do_not_delete_database_when_user_has_bad_credentials() {
        // Enqueue user bad credentials response
        dhis2MockServer.enqueueMockResponse(401, "user/user_unauthorized.json")
        addDummyData()
        assertThat(d2.userModule().accountManager().getAccounts().size).isEqualTo(1)

        try {
            d2.dataValueModule().dataValues().blockingUpload()
        } catch (e: Exception) {
            val d2Error = e.cause as D2Error
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
        }
        assertThat(d2.userModule().accountManager().getAccounts().size).isEqualTo(1)
    }

    private fun addDummyData() {
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "Ok9OQpitjQr",
            "DwrQJzeChWp",
            "DwrQJzeChWp"
        ).blockingSet("30")
    }
}
