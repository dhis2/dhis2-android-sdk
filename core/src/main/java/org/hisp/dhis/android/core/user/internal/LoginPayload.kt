package org.hisp.dhis.android.core.user.internal

internal class LoginPayload(
    val username: String,
    val password: String,
    val twoFactorCode: String?
)