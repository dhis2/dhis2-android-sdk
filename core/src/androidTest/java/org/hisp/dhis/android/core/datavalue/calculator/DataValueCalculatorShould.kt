package org.hisp.dhis.android.core.datavalue.calculator

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataValueCalculatorShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun evaluate_sum_all_values() {
        val result = d2.dataValueModule().calculator()
            .evaluate()

        assertThat(result).isEqualTo(57.0)
    }

    @Test
    fun evaluate_sum_by_data_element() {
        val result = d2.dataValueModule().calculator()
            .withDataElement("g9eOBujte1U")
            .evaluate()

        assertThat(result).isEqualTo(57.0)
    }

    @Test
    fun evaluate_sum_by_non_existing_data_element() {
        val emptyResult = d2.dataValueModule().calculator()
            .withDataElement("non-existing-uid")
            .evaluate()

        assertThat(emptyResult).isEqualTo(0.0)
    }

    @Test
    fun evaluate_sum_by_period() {
        val result = d2.dataValueModule().calculator()
            .withPeriod("201907")
            .evaluate()

        assertThat(result).isEqualTo(24.0)
    }

    @Test
    fun evaluate_sum_by_period_and_category_option_combo() {
        val result = d2.dataValueModule().calculator()
            .withPeriod("201907")
            .withCategoryOptionCombo("Gmbgme7z9BF")
            .evaluate()

        assertThat(result).isEqualTo(12.0)
    }

    @Test
    fun evaluate_sum_by_aggregation_type() {
        val result = d2.dataValueModule().calculator()
            .withAggregationType(AggregationType.AVERAGE)
            .evaluate()

        assertThat(result).isEqualTo(11.4)
    }

    @Test
    fun evaluate_sum_by_created_after() {
        val result = d2.dataValueModule().calculator()
            .withCreatedAfter(DateUtils.DATE_FORMAT.parse("2012-01-01T00:00:00.000+0000"))
            .evaluate()

        assertThat(result).isEqualTo(35.0)
    }

    @Test
    fun evaluate_using_last_parameter() {
        val result = d2.dataValueModule().calculator()
            .withPeriod("2017")
            .withPeriod("2018")
            .evaluate()

        assertThat(result).isEqualTo(10.0)
    }
}
