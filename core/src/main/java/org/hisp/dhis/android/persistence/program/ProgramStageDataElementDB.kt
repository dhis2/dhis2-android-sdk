package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "ProgramStageDataElement",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
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
        Index(value = ["uid"], unique = true),
        Index(value = ["programStage"]),
        Index(value = ["dataElement"]),
    ],
)
internal data class ProgramStageDataElementDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val displayInReports: Boolean?,
    val compulsory: Boolean?,
    val allowProvidedElsewhere: Boolean?,
    val sortOrder: Int?,
    val allowFutureDate: Boolean?,
    val dataElement: String,
    val programStage: String,
) : EntityDB<ProgramStageDataElement>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramStageDataElement {
        return ProgramStageDataElement.builder().apply {
            applyBaseIdentifiableFields(this@ProgramStageDataElementDB)
            id(id?.toLong())
            displayInReports(displayInReports)
            compulsory(compulsory)
            allowProvidedElsewhere(allowProvidedElsewhere)
            sortOrder(sortOrder)
            allowFutureDate(allowFutureDate)
            dataElement(DataElement.builder().uid(dataElement).build())
            programStage(ObjectWithUid.create(programStage))
        }.build()
    }
}

internal fun ProgramStageDataElement.toDB(): ProgramStageDataElementDB {
    return ProgramStageDataElementDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        displayInReports = displayInReports(),
        compulsory = compulsory(),
        allowProvidedElsewhere = allowProvidedElsewhere(),
        sortOrder = sortOrder(),
        allowFutureDate = allowFutureDate(),
        dataElement = dataElement()!!.uid()!!,
        programStage = programStage()!!.uid(),
    )
}
