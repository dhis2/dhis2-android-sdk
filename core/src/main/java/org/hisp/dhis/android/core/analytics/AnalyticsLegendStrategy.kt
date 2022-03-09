package org.hisp.dhis.android.core.analytics

sealed class AnalyticsLegendStrategy {
    object None : AnalyticsLegendStrategy()
    object ByDataItem : AnalyticsLegendStrategy()
    data class Fixed(val legendSetUid: String) : AnalyticsLegendStrategy()
}
