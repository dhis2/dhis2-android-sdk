package org.hisp.dhis.android.persistence.sms

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSOngoingSubmission
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "SMSOngoingSubmission")
internal data class SMSOngoingSubmissionDB(
    @PrimaryKey
    val submissionId: Int,
    val type: String,
) : EntityDB<SMSOngoingSubmission> {

    override fun toDomain(): SMSOngoingSubmission {
        return SMSOngoingSubmission.builder()
            .submissionId(submissionId)
            .type(SubmissionType.valueOf(type))
            .build()
    }
}

internal fun SMSOngoingSubmission.toDB(): SMSOngoingSubmissionDB {
    return SMSOngoingSubmissionDB(
        submissionId = submissionId(),
        type = type().name,
    )
}
