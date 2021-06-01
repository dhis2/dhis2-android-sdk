package org.hisp.dhis.android.core.datavalue.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataValuePostCallMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun post_dataValues_success() {
        dhis2MockServer.enqueueMockResponse("datavalueset/data_value_set_success.json")

        provideCorrectDataValues()

        d2.dataValueModule().dataValues().blockingUpload()
        val warnings = d2.dataValueModule().dataValues().byState().eq(State.SYNCED).blockingGet()
        assertThat(warnings.size).isEqualTo(2)
    }

    @Test
    fun post_dataValues_warning() {
        dhis2MockServer.enqueueMockResponse("datavalueset/data_value_set_warning.json")

        provideWarningDataValues()

        d2.dataValueModule().dataValues().blockingUpload()
        val warnings = d2.dataValueModule().dataValues().byState().eq(State.WARNING).blockingGet()
        assertThat(warnings.size).isEqualTo(1)
    }

    private fun provideCorrectDataValues() {
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "Ok9OQpitjQr",
            "DwrQJzeChWp",
            "DwrQJzeChWp"
        ).blockingSet("30")
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "vANAXwtLwcT",
            "bRowv6yZOF2",
            "bRowv6yZOF2"
        ).blockingSet("40")
    }

    private fun provideWarningDataValues() {
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "Ok9OQpitjQr",
            "DwrQJzeChWp",
            "DwrQJzeChWp"
        ).blockingSet("30")
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "vANAXwtLwcT",
            "bRowv6yZOF2",
            "bRowv6yZOF2"
        ).blockingSet("40L")
    }
}
