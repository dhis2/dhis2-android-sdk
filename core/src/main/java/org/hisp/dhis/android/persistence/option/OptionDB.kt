package org.hisp.dhis.android.persistence.option

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields

@Entity(
    tableName = "Option",
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
        Index(value = ["optionSet", "code"]),
    ],
)
internal data class OptionDB(
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
    val sortOrder: Int?,
    override val color: String?,
    override val icon: String?,
) : EntityDB<Option>, BaseIdentifiableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): Option {
        return Option.builder()
            .applyBaseIdentifiableFields(this@OptionDB)
            .applyStyleFields(this@OptionDB)
            .id(id?.toLong())
            .optionSet(ObjectWithUid.create(optionSet))
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun Option.toDB(): OptionDB {
    return OptionDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        optionSet = optionSet()!!.uid(),
        sortOrder = sortOrder(),
        color = style().color(),
        icon = style().icon(),
    )
}
