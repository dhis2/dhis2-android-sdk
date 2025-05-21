package org.hisp.dhis.android.persistence.legendset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramIndicatorDB

@Entity(
    tableName = "ProgramIndicatorLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = LegendSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["legendSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["programIndicator", "legendSet"], unique = true),
        Index(value = ["programIndicator"]),
        Index(value = ["legendSet"]),
    ],
)
internal data class ProgramIndicatorLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val programIndicator: String,
    val legendSet: String,
    val sortOrder: Int?,
) : EntityDB<ProgramIndicatorLegendSetLink> {

    override fun toDomain(): ProgramIndicatorLegendSetLink {
        return ProgramIndicatorLegendSetLink.builder()
            .id(id?.toLong())
            .programIndicator(programIndicator)
            .legendSet(legendSet)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun ProgramIndicatorLegendSetLink.toDB(): ProgramIndicatorLegendSetLinkDB {
    return ProgramIndicatorLegendSetLinkDB(
        programIndicator = programIndicator()!!,
        legendSet = legendSet()!!,
        sortOrder = sortOrder(),
    )
}
