package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.CustomIntent
import org.hisp.dhis.android.core.settings.CustomIntentRequest
import org.hisp.dhis.android.core.settings.CustomIntentResponse
import org.hisp.dhis.android.core.settings.CustomIntentResponseData
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "CustomIntent")
internal data class CustomIntentDB(
    @PrimaryKey
    val uid: String,
    val name: String?,
    val action: CustomIntentActionTypeListDB?,
    val packageName: String?,
    val requestArguments: CustomIntentRequestArgumentsDB?,
    val responseDataExtras: ResponseDataExtrasDB?,
) : EntityDB<CustomIntent> {

    override fun toDomain(): CustomIntent {
        return CustomIntent.builder().apply {
            uid(uid)
            name(name)
            action(action?.toDomain() ?: emptyList())
            packageName(packageName)
            requestArguments?.let {
                request(
                    CustomIntentRequest.builder().arguments(requestArguments.toDomain()).build(),
                )
            }
            responseDataExtras?.let {
                response(
                    CustomIntentResponse.builder().data(
                        CustomIntentResponseData.builder()
                            .extras(it.toDomain())
                            .build(),
                    ).build(),
                )
            }
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
        responseDataExtras = response()?.data()?.extras()?.toDB(),
    )
}
