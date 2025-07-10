package org.hisp.dhis.android.persistence.option

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "OptionSet",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class OptionSetDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val version: Int?,
    val valueType: String?,
) : EntityDB<OptionSet>, BaseIdentifiableObjectDB {

    override fun toDomain(): OptionSet {
        return OptionSet.builder().apply {
            applyBaseIdentifiableFields(this@OptionSetDB)
            version(version)
            valueType?.let { valueType(ValueType.valueOf(it)) }
        }.build()
    }
}

internal fun OptionSet.toDB(): OptionSetDB {
    return OptionSetDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        version = version(),
        valueType = valueType()?.name,
    )
}
