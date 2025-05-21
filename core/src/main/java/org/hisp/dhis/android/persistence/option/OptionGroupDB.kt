package org.hisp.dhis.android.persistence.option

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.option.OptionGroup
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "OptionGroup",
    foreignKeys = [
        ForeignKey(
            entity = OptionSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["optionSet"]),
    ],
)
internal data class OptionGroupDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val optionSet: String,
) : EntityDB<OptionGroup>, BaseIdentifiableObjectDB {

    override fun toDomain(): OptionGroup {
        return OptionGroup.builder()
            .applyBaseIdentifiableFields(this@OptionGroupDB)
            .id(id?.toLong())
            .optionSet(ObjectWithUid.create(optionSet))
            .build()
    }
}

internal fun OptionGroup.toDB(): OptionGroupDB {
    return OptionGroupDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        optionSet = optionSet()!!.uid(),
    )
}
