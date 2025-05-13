package org.hisp.dhis.android.persistence.common

import org.hisp.dhis.android.core.common.FilterPeriod

internal data class FilterPeriodDB(
    val periodFrom: Int?,
    val periodTo: Int?,
) : EntityDB<FilterPeriod?> {

    override fun toDomain(): FilterPeriod? {
        return if (periodFrom != null && periodTo != null) {
            FilterPeriod.builder()
                .periodFrom(periodFrom)
                .periodTo(periodTo)
                .build()
        } else {
            null
        }
    }
}

internal fun FilterPeriod?.toDB(): FilterPeriodDB {
    return FilterPeriodDB(
        periodFrom = this?.periodFrom(),
        periodTo = this?.periodTo(),
    )
}
