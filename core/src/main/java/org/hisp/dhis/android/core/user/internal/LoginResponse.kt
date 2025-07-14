@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package org.hisp.dhis.android.core.user.internal

import kotlinx.serialization.Serializable

@Serializable
internal class LoginResponse(val loginStatus: String)