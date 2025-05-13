package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.ProgramConfigurationSetting
import org.hisp.dhis.android.core.settings.ProgramItemHeader
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "ProgramConfigurationSetting")
internal data class ProgramConfigurationSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String?,
    val completionSpinner: Boolean?,
    val optionalSearch: Boolean?,
    val disableReferrals: Boolean?,
    val disableCollapsibleSections: Boolean?,
    val itemHeaderProgramIndicator: String?,
    val minimumLocationAccuracy: Int?,
    val disableManualLocation: Boolean?,
    val quickActions: QuickActionsDB?,
) : EntityDB<ProgramConfigurationSetting> {

    override fun toDomain(): ProgramConfigurationSetting {
        return ProgramConfigurationSetting.builder()
            .id(id?.toLong())
            .uid(uid)
            .completionSpinner(completionSpinner)
            .optionalSearch(optionalSearch)
            .disableReferrals(disableReferrals)
            .disableCollapsibleSections(disableCollapsibleSections)
            .itemHeader(
                itemHeaderProgramIndicator?.let {
                    ProgramItemHeader.builder()
                        .programIndicator(it)
                        .build()
                },
            )
            .minimumLocationAccuracy(minimumLocationAccuracy)
            .disableManualLocation(disableManualLocation)
            .quickActions(quickActions?.toDomain())
            .build()
    }
}

internal fun ProgramConfigurationSetting.toDB(): ProgramConfigurationSettingDB {
    return ProgramConfigurationSettingDB(
        uid = uid(),
        completionSpinner = completionSpinner(),
        optionalSearch = optionalSearch(),
        disableReferrals = disableReferrals(),
        disableCollapsibleSections = disableCollapsibleSections(),
        itemHeaderProgramIndicator = itemHeader()?.programIndicator(),
        minimumLocationAccuracy = minimumLocationAccuracy(),
        disableManualLocation = disableManualLocation(),
        quickActions = quickActions()?.toDB(),
    )
}
