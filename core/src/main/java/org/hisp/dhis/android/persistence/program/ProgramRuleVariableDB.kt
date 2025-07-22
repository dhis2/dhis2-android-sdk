package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramRuleVariable
import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.dataelement.DataElementDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB

@Entity(
    tableName = "ProgramRuleVariable",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
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
        Index(value = ["program"]),
        Index(value = ["programStage"]),
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["dataElement"]),
    ],
)
internal data class ProgramRuleVariableDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val useCodeForOptionSet: Boolean?,
    val program: String,
    val programStage: String?,
    val dataElement: String?,
    val trackedEntityAttribute: String?,
    val programRuleVariableSourceType: String?,
) : EntityDB<ProgramRuleVariable>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramRuleVariable {
        return ProgramRuleVariable.builder().apply {
            applyBaseIdentifiableFields(this@ProgramRuleVariableDB)
            useCodeForOptionSet(useCodeForOptionSet)
            program(ObjectWithUid.create(program))
            programStage?.let { programStage(ObjectWithUid.create(it)) }
            dataElement?.let { dataElement(ObjectWithUid.create(it)) }
            trackedEntityAttribute?.let { trackedEntityAttribute(ObjectWithUid.create(it)) }
            programRuleVariableSourceType?.let {
                programRuleVariableSourceType(
                    ProgramRuleVariableSourceType.valueOf(it),
                )
            }
        }.build()
    }
}

internal fun ProgramRuleVariable.toDB(): ProgramRuleVariableDB {
    return ProgramRuleVariableDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        useCodeForOptionSet = useCodeForOptionSet(),
        program = program()!!.uid(),
        programStage = programStage()?.uid(),
        dataElement = dataElement()?.uid(),
        trackedEntityAttribute = trackedEntityAttribute()?.uid(),
        programRuleVariableSourceType = programRuleVariableSourceType()?.name,
    )
}
