package org.hisp.dhis.android.persistence.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLink
import org.hisp.dhis.android.persistence.common.EntityDB

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
    indices = [
        Index(value = ["categoryOptionCombo", "categoryOption"], unique = true),
        Index(value = ["categoryOptionCombo"]),
        Index(value = ["categoryOption"]),
    ],
)
internal data class CategoryOptionComboCategoryOptionLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val categoryOptionCombo: String,
    val categoryOption: String,
) : EntityDB<CategoryOptionComboCategoryOptionLink> {

    override fun toDomain(): CategoryOptionComboCategoryOptionLink {
        return CategoryOptionComboCategoryOptionLink.builder()
            .id(id?.toLong())
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
