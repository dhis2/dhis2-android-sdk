package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "ProgramRule",
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
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
    ],
)
internal data class ProgramRuleDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val priority: Int?,
    val condition: String?,
    val program: String,
    val programStage: String?,
) : EntityDB<ProgramRule>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramRule {
        return ProgramRule.builder().apply {
            applyBaseIdentifiableFields(this@ProgramRuleDB)
            id(id?.toLong())
            priority(priority)
            condition(condition)
            program(ObjectWithUid.create(program))
            programStage?.let { programStage(ObjectWithUid.create(it)) }
        }.build()
    }
}

internal fun ProgramRule.toDB(): ProgramRuleDB {
    return ProgramRuleDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created()?.dateFormat(),
        lastUpdated = lastUpdated()?.dateFormat(),
        priority = priority(),
        condition = condition(),
        program = program()!!.uid(),
        programStage = programStage()?.uid(),
    )
}
