package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "ProgramStageSectionDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["programStageSection"]),
        Index(value = ["dataElement"]),
    ],
)
internal data class ProgramStageSectionDataElementLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val programStageSection: String,
    val dataElement: String,
    val sortOrder: Int,
)
