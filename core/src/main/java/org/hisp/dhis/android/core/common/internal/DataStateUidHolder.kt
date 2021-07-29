package org.hisp.dhis.android.core.common.internal

data class DataStateUidHolder(
    val trackedEntities: List<String>,
    val enrollments: List<String>,
    val events: List<String>
)
