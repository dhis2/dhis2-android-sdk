package org.hisp.dhis.android.core.analytics

sealed class LegendStrategy {
    object None : LegendStrategy()
    object ByDataItem : LegendStrategy()
    data class Fixed(val legendSetUid: String) : LegendStrategy()
}