package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.CustomIntent
import org.hisp.dhis.android.core.settings.CustomIntentRequest
import org.hisp.dhis.android.core.settings.CustomIntentResponse
import org.hisp.dhis.android.core.settings.CustomIntentResponseData
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "CustomIntent",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class CustomIntentDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val name: String?,
    val action: CustomIntentActionTypeListDB?,
    val packageName: String?,
    val requestArguments: StringStringMapDB?,
    val responseDataArgument: String?,
    val responseDataPath: String?,
) : EntityDB<CustomIntent> {

    override fun toDomain(): CustomIntent {
        return CustomIntent.builder().apply {
            id(id?.toLong())
            uid(uid)
            name(name)
            action(action?.toDomain())
            packageName(packageName)
            requestArguments?.let {
                request(
                    CustomIntentRequest.builder().arguments(requestArguments.toDomain()).build(),
                )
            }
            response(
                CustomIntentResponse.builder().data(
                    CustomIntentResponseData.builder()
                        .argument(responseDataArgument)
                        .path(responseDataPath)
                        .build(),
                ).build(),
            )
        }.build()
    }
}

internal fun CustomIntent.toDB(): CustomIntentDB {
    return CustomIntentDB(
        uid = uid(),
        name = name(),
        action = action()?.toDB(),
        packageName = packageName(),
        requestArguments = request()?.arguments()?.toDB(),
        responseDataArgument = response()?.data()?.argument(),
        responseDataPath = response()?.data()?.argument(),
    )
}
