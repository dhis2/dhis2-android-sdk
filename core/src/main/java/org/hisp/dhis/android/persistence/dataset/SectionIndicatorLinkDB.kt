package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.dataset.internal.SectionIndicatorLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.indicator.IndicatorDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "SectionIndicatorLink",
    foreignKeys = [
        ForeignKey(
            entity = SectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["section"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = IndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["section", "indicator"],
)
internal data class SectionIndicatorLinkDB(
    @ParentColumn val section: String,
    val indicator: String,
) : EntityDB<SectionIndicatorLink> {

    override fun toDomain(): SectionIndicatorLink {
        return SectionIndicatorLink.builder()
            .section(section)
            .indicator(indicator)
            .build()
    }
}

internal fun SectionIndicatorLink.toDB(): SectionIndicatorLinkDB {
    return SectionIndicatorLinkDB(
        section = section()!!,
        indicator = indicator()!!,
    )
}
