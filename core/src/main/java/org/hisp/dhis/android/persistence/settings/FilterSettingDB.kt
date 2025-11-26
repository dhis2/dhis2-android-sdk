package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.FilterSetting
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "FilterSetting",
)
internal data class FilterSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val scope: String?,
    val filterType: String?,
    val uid: String?,
    val sort: Boolean?,
    val filter: Boolean?,
) : EntityDB<FilterSetting> {

    override fun toDomain(): FilterSetting {
        return FilterSetting.builder()
            .scope(scope)
            .filterType(filterType)
            .uid(uid)
            .sort(sort)
            .filter(filter)
            .build()
    }
}

internal fun FilterSetting.toDB(): FilterSettingDB {
    return FilterSettingDB(
        scope = scope(),
        filterType = filterType(),
        uid = uid(),
        sort = sort(),
        filter = filter(),
    )
}
