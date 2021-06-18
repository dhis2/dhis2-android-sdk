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
        // Given user sets correct data values
        dhis2MockServer.enqueueMockResponse("datavalueset/data_value_set_success.json")
        provideDataValues("30", "40")

        // When user sync data in order to upload the data values
        d2.dataValueModule().dataValues().blockingUpload()

        // Then all data set should be properly synced
        val synced = d2.dataValueModule().dataValues().bySyncState().eq(State.SYNCED).blockingGet()
        assertThat(synced.size).isEqualTo(2)
    }

    @Test
    fun post_dataValues_warning() {
        // Given user sets one unsupported type of data value
        dhis2MockServer.enqueueMockResponse("datavalueset/data_value_set_warning.json")
        provideDataValues("30", "40L")

        // When user sync data in order to upload the data values
        d2.dataValueModule().dataValues().blockingUpload()

        // Then one data set should marked as WARNING
        val warnings = d2.dataValueModule().dataValues().bySyncState().eq(State.WARNING).blockingGet()
        assertThat(warnings.size).isEqualTo(1)
    }

    @Test
    fun post_dataValues_undetermined_warning() {
        // Given user sets one undetermined data value
        dhis2MockServer.enqueueMockResponse("datavalueset/data_value_set_warning.json")
        provideDataValues("40", "50L")

        // When user sync data in order to upload the data values
        d2.dataValueModule().dataValues().blockingUpload()

        // Then all data values should be marked as WARNING
        val warnings = d2.dataValueModule().dataValues().bySyncState().eq(State.WARNING).blockingGet()
        assertThat(warnings.size).isEqualTo(2)
    }

    private fun provideDataValues(value1: String, value2: String) {
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "Ok9OQpitjQr",
            "DwrQJzeChWp",
            "DwrQJzeChWp"
        ).blockingSet(value1)
        d2.dataValueModule().dataValues().value(
            "20191021",
            "DiszpKrYNg8",
            "vANAXwtLwcT",
            "bRowv6yZOF2",
            "bRowv6yZOF2"
        ).blockingSet(value2)
    }
}
