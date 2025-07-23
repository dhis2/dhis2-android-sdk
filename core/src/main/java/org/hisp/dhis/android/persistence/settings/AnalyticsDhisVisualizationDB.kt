package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.AnalyticsDhisVisualization
import org.hisp.dhis.android.core.settings.AnalyticsDhisVisualizationScope
import org.hisp.dhis.android.core.settings.AnalyticsDhisVisualizationType
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "AnalyticsDhisVisualization")
internal data class AnalyticsDhisVisualizationDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val scopeUid: String?,
    val scope: String?,
    val groupUid: String?,
    val groupName: String?,
    val timestamp: String?,
    val name: String?,
    val type: String,
) : EntityDB<AnalyticsDhisVisualization> {

    override fun toDomain(): AnalyticsDhisVisualization {
        return AnalyticsDhisVisualization.builder().apply {
            uid(uid)
            scopeUid(scopeUid)
            scope?.let { scope(AnalyticsDhisVisualizationScope.valueOf(it)) }
            groupUid(groupUid)
            groupName(groupName)
            timestamp(timestamp)
            name(name)
            type(AnalyticsDhisVisualizationType.valueOf(type))
        }.build()
    }
}

internal fun AnalyticsDhisVisualization.toDB(): AnalyticsDhisVisualizationDB {
    return AnalyticsDhisVisualizationDB(
        uid = uid(),
        scopeUid = scopeUid(),
        scope = scope()?.name,
        groupUid = groupUid(),
        groupName = groupName(),
        timestamp = timestamp(),
        name = name(),
        type = type().name,
    )
}
