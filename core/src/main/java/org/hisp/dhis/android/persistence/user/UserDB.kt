package org.hisp.dhis.android.persistence.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "User")
internal data class UserDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val birthday: String?,
    val education: String?,
    val gender: String?,
    val jobTitle: String?,
    val surname: String?,
    val firstName: String?,
    val introduction: String?,
    val employer: String?,
    val interests: String?,
    val languages: String?,
    val email: String?,
    val phoneNumber: String?,
    val nationality: String?,
    val username: String?,
) : EntityDB<User>, BaseIdentifiableObjectDB {

    override fun toDomain(): User {
        return User.builder().apply {
            applyBaseIdentifiableFields(this@UserDB)
            birthday(birthday)
            education(education)
            gender(gender)
            jobTitle(jobTitle)
            surname(surname)
            firstName(firstName)
            introduction(introduction)
            employer(employer)
            interests(interests)
            languages(languages)
            email(email)
            phoneNumber(phoneNumber)
            nationality(nationality)
            username(username)
        }.build()
    }
}

internal fun User.toDB(): UserDB {
    return UserDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        birthday = birthday(),
        education = education(),
        gender = gender(),
        jobTitle = jobTitle(),
        surname = surname(),
        firstName = firstName(),
        introduction = introduction(),
        employer = employer(),
        interests = interests(),
        languages = languages(),
        email = email(),
        phoneNumber = phoneNumber(),
        nationality = nationality(),
        username = username(),
    )
}
