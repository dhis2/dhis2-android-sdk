package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import org.hisp.dhis.android.core.settings.FilterSetting
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "FilterSetting",
    primaryKeys = ["scope", "filterType", "uid"],
)
internal data class FilterSettingDB(
    val scope: String,
    val filterType: String,
    val uid: String,
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
        scope = scope()!!,
        filterType = filterType()!!,
        uid = uid(),
        sort = sort(),
        filter = filter(),
    )
}
