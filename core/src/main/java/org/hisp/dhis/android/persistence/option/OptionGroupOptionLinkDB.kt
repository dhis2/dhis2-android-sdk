package org.hisp.dhis.android.persistence.option

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.option.OptionGroupOptionLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "OptionGroupOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = OptionGroupDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionGroup"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = OptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["option"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["optionGroup", "option"],
)
internal data class OptionGroupOptionLinkDB(
    @ParentColumn val optionGroup: String,
    val option: String,
) : EntityDB<OptionGroupOptionLink> {

    override fun toDomain(): OptionGroupOptionLink {
        return OptionGroupOptionLink.builder()
            .optionGroup(optionGroup)
            .option(option)
            .build()
    }
}

internal fun OptionGroupOptionLink.toDB(): OptionGroupOptionLinkDB {
    return OptionGroupOptionLinkDB(
        optionGroup = optionGroup()!!,
        option = option()!!,
    )
}
