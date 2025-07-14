@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package org.hisp.dhis.android.core.user.internal

import kotlinx.serialization.Serializable

@Serializable
internal class LoginPayload(
    val username: String,
    val password: String,
    val twoFactorCode: String?
)