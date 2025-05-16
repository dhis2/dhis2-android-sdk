package org.hisp.dhis.android.persistence.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListAttributeValueFilter
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListEventDataFilter
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.persistence.event.EventFilterDB
import org.hisp.dhis.android.persistence.programstageworkinglist.ProgramStageWorkingListDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceFilterDB

@Entity(
    tableName = "ItemFilter",
    foreignKeys = [
        ForeignKey(
            entity = EventFilterDB::class,
            parentColumns = ["uid"],
            childColumns = ["eventFilter"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityInstanceFilterDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstanceFilter"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramStageWorkingListDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageWorkingList"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["eventFilter"]),
        Index(value = ["trackedEntityInstanceFilter"]),
        Index(value = ["programStageWorkingList"]),
    ],
)
internal data class ItemFilterDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val eventFilter: String?,
    val dataItem: String?,
    val trackedEntityInstanceFilter: String?,
    val attribute: String?,
    val programStageWorkingList: String?,
    val sw: String?,
    val ew: String?,
    override val le: String?,
    override val ge: String?,
    override val gt: String?,
    override val lt: String?,
    override val eq: String?,
    override val inProperty: StringSetDB?,
    override val like: String?,
    override val dateFilter: DateFilterPeriodDB?,
) : FilterOperatorsDB {

    fun toEventDataFilterDomain(): EventDataFilter {
        return EventDataFilter.builder()
            .applyFilterOperatorsFields(this@ItemFilterDB)
            .id(id?.toLong())
            .eventFilter(eventFilter)
            .dataItem(dataItem)
            .build()
    }

    fun toAttributeValueFilterDomain(): AttributeValueFilter {
        return AttributeValueFilter.builder()
            .applyFilterOperatorsFields(this@ItemFilterDB)
            .id(id?.toLong())
            .trackedEntityInstanceFilter(trackedEntityInstanceFilter)
            .attribute(attribute)
            .sw(sw)
            .ew(ew)
            .build()
    }

    fun toProgramStageWorkingListEventDataFilterDomain(): ProgramStageWorkingListEventDataFilter {
        return ProgramStageWorkingListEventDataFilter.builder()
            .applyFilterOperatorsFields(this@ItemFilterDB)
            .id(id?.toLong())
            .programStageWorkingList(programStageWorkingList)
            .dataItem(dataItem)
            .build()
    }

    fun toProgramStageWorkingListAttributeValueFilterDomain(): ProgramStageWorkingListAttributeValueFilter {
        return ProgramStageWorkingListAttributeValueFilter.builder()
            .applyFilterOperatorsFields(this@ItemFilterDB)
            .id(id?.toLong())
            .attribute(attribute)
            .programStageWorkingList(programStageWorkingList)
            .sw(sw)
            .ew(ew)
            .build()
    }
}

internal fun EventDataFilter.toDB(): ItemFilterDB {
    return ItemFilterDB(
        eventFilter = eventFilter(),
        dataItem = dataItem(),
        trackedEntityInstanceFilter = null,
        attribute = null,
        programStageWorkingList = null,
        sw = null,
        ew = null,
        le = le(),
        ge = ge(),
        gt = gt(),
        lt = lt(),
        eq = eq(),
        inProperty = `in`()?.toDB(),
        like = like(),
        dateFilter = dateFilter()?.toDB(),
    )
}

internal fun AttributeValueFilter.toDB(): ItemFilterDB {
    return ItemFilterDB(
        eventFilter = null,
        dataItem = null,
        trackedEntityInstanceFilter = trackedEntityInstanceFilter(),
        attribute = attribute(),
        programStageWorkingList = null,
        sw = sw(),
        ew = ew(),
        le = le(),
        ge = ge(),
        gt = gt(),
        lt = lt(),
        eq = eq(),
        inProperty = `in`()?.toDB(),
        like = like(),
        dateFilter = dateFilter()?.toDB(),
    )
}

internal fun ProgramStageWorkingListEventDataFilter.toDB(): ItemFilterDB {
    return ItemFilterDB(
        eventFilter = null,
        dataItem = dataItem(),
        trackedEntityInstanceFilter = null,
        attribute = null,
        programStageWorkingList = programStageWorkingList(),
        sw = null,
        ew = null,
        le = le(),
        ge = ge(),
        gt = gt(),
        lt = lt(),
        eq = eq(),
        inProperty = `in`()?.toDB(),
        like = like(),
        dateFilter = dateFilter()?.toDB(),
    )
}

internal fun ProgramStageWorkingListAttributeValueFilter.toDB(): ItemFilterDB {
    return ItemFilterDB(
        eventFilter = null,
        dataItem = null,
        trackedEntityInstanceFilter = null,
        attribute = attribute(),
        programStageWorkingList = programStageWorkingList(),
        sw = sw(),
        ew = ew(),
        le = le(),
        ge = ge(),
        gt = gt(),
        lt = lt(),
        eq = eq(),
        inProperty = `in`()?.toDB(),
        like = like(),
        dateFilter = dateFilter()?.toDB(),
    )
}
