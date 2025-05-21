package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB

@Entity(
    tableName = "ProgramTrackedEntityAttribute",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["program"]),
    ],
)
internal data class ProgramTrackedEntityAttributeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val mandatory: Boolean?,
    val trackedEntityAttribute: String,
    val allowFutureDate: Boolean?,
    val displayInList: Boolean?,
    val program: String,
    val sortOrder: Int?,
    val searchable: Boolean?,
) : EntityDB<ProgramTrackedEntityAttribute>, BaseNameableObjectDB {

    override fun toDomain(): ProgramTrackedEntityAttribute {
        return ProgramTrackedEntityAttribute.builder()
            .id(id?.toLong())
            .applyBaseNameableFields(this)
            .mandatory(mandatory)
            .trackedEntityAttribute(ObjectWithUid.create(trackedEntityAttribute))
            .allowFutureDate(allowFutureDate)
            .displayInList(displayInList)
            .program(ObjectWithUid.create(program))
            .sortOrder(sortOrder)
            .searchable(searchable)
            .build()
    }
}

internal fun ProgramTrackedEntityAttribute.toDB(): ProgramTrackedEntityAttributeDB {
    return ProgramTrackedEntityAttributeDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        mandatory = mandatory(),
        trackedEntityAttribute = trackedEntityAttribute()!!.uid(),
        allowFutureDate = allowFutureDate(),
        displayInList = displayInList(),
        program = program()!!.uid(),
        sortOrder = sortOrder(),
        searchable = searchable(),
    )
}
