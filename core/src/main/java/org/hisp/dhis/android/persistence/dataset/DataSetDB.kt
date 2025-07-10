package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.CustomText
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetDisplayOptions
import org.hisp.dhis.android.core.dataset.TabsDirection
import org.hisp.dhis.android.core.dataset.TextAlign
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.common.AccessDB
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "DataSet",
    foreignKeys = [
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["categoryCombo"]),
    ],
)
internal data class DataSetDB(
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
    val periodType: String?,
    val categoryCombo: String,
    val mobile: Boolean?,
    val version: Int?,
    val expiryDays: Int?,
    val timelyDays: Int?,
    val notifyCompletingUser: Boolean?,
    val openFuturePeriods: Int?,
    val fieldCombinationRequired: Boolean?,
    val validCompleteOnly: Boolean?,
    val noValueRequiresComment: Boolean?,
    val skipOffline: Boolean?,
    val dataElementDecoration: Boolean?,
    val renderAsTabs: Boolean?,
    val renderHorizontally: Boolean?,
    val accessDataWrite: AccessDB?,
    val workflow: String?,
    override val color: String?,
    override val icon: String?,
    val header: String?,
    val subHeader: String?,
    val customTextAlign: String?,
    val tabsDirection: String?,
) : EntityDB<DataSet>, BaseNameableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): DataSet {
        return DataSet.builder().apply {
            applyBaseNameableFields(this@DataSetDB)
            applyStyleFields(this@DataSetDB)
            periodType?.let { periodType(PeriodType.valueOf(it)) }
            categoryCombo(ObjectWithUid.create(categoryCombo))
            mobile(mobile)
            version(version)
            expiryDays(expiryDays?.toDouble())
            timelyDays(timelyDays?.toDouble())
            notifyCompletingUser(notifyCompletingUser)
            openFuturePeriods(openFuturePeriods)
            fieldCombinationRequired(fieldCombinationRequired)
            validCompleteOnly(validCompleteOnly)
            noValueRequiresComment(noValueRequiresComment)
            skipOffline(skipOffline)
            dataElementDecoration(dataElementDecoration)
            renderAsTabs(renderAsTabs)
            renderHorizontally(renderHorizontally)
            accessDataWrite?.let { access(it.toDomain()) }
            workflow?.let { workflow(ObjectWithUid.create(it)) }
            displayOptions(
                DataSetDisplayOptions.builder().apply {
                    customText(
                        CustomText.builder().apply {
                            header(header)
                            subHeader(subHeader)
                            customTextAlign?.let { align(TextAlign.valueOf(it)) }
                        }.build(),
                    )
                    tabsDirection?.let { tabsDirection(TabsDirection.valueOf(it)) }
                }.build(),
            )
        }.build()
    }
}

internal fun DataSet.toDB(): DataSetDB {
    return DataSetDB(
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
        periodType = periodType()?.name,
        categoryCombo = categoryCombo()!!.uid(),
        mobile = mobile(),
        version = version(),
        expiryDays = expiryDays()?.toInt(),
        timelyDays = timelyDays()?.toInt(),
        notifyCompletingUser = notifyCompletingUser(),
        openFuturePeriods = openFuturePeriods(),
        fieldCombinationRequired = fieldCombinationRequired(),
        validCompleteOnly = validCompleteOnly(),
        noValueRequiresComment = noValueRequiresComment(),
        skipOffline = skipOffline(),
        dataElementDecoration = dataElementDecoration(),
        renderAsTabs = renderAsTabs(),
        renderHorizontally = renderHorizontally(),
        accessDataWrite = access().toDB(),
        workflow = workflow()?.uid(),
        color = style().color(),
        icon = style().icon(),
        header = displayOptions()?.customText()?.header(),
        subHeader = displayOptions()?.customText()?.subHeader(),
        customTextAlign = displayOptions()?.customText()?.align()?.name,
        tabsDirection = displayOptions()?.tabsDirection()?.name,
    )
}
