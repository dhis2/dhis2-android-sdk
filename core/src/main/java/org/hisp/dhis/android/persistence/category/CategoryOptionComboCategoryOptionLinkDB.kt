package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "CategoryOptionComboCategoryOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryOptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOption"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["categoryOptionCombo", "categoryOption"],
)
internal data class CategoryOptionComboCategoryOptionLinkDB(
    @ParentColumn val categoryOptionCombo: String,
    val categoryOption: String,
) : EntityDB<CategoryOptionComboCategoryOptionLink> {

    override fun toDomain(): CategoryOptionComboCategoryOptionLink {
        return CategoryOptionComboCategoryOptionLink.builder()
            .categoryOptionCombo(categoryOptionCombo)
            .categoryOption(categoryOption)
            .build()
    }
}

internal fun CategoryOptionComboCategoryOptionLink.toDB(): CategoryOptionComboCategoryOptionLinkDB {
    return CategoryOptionComboCategoryOptionLinkDB(
        categoryOptionCombo = categoryOptionCombo()!!,
        categoryOption = categoryOption()!!,
    )
}
