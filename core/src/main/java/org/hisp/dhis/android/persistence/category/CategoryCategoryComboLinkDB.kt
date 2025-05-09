package org.hisp.dhis.android.persistence.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "CategoryCategoryComboLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDB::class,
            parentColumns = ["uid"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["category", "categoryCombo"], unique = true),
        Index(value = ["category"]),
        Index(value = ["categoryCombo"]),
    ],
)
internal data class CategoryCategoryComboLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val category: String,
    val categoryCombo: String,
    val sortOrder: Int?,
) : EntityDB<CategoryCategoryComboLink> {

    override fun toDomain(): CategoryCategoryComboLink {
        return CategoryCategoryComboLink.builder()
            .id(id.toLong())
            .category(category)
            .categoryCombo(categoryCombo)
            .sortOrder(sortOrder)
            .build()
    }

    companion object {
        fun CategoryCategoryComboLink.toDB(): CategoryCategoryComboLinkDB {
            return CategoryCategoryComboLinkDB(
                category = category()!!,
                categoryCombo = categoryCombo()!!,
                sortOrder = sortOrder(),
            )
        }
    }
}
