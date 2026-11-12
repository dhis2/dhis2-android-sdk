package org.hisp.dhis.android.core.arch.storage.internal

import android.content.Context
import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.arch.storage.internal.LegacyRsaSecureStoreWriter.Companion.LEGACY_PREFERENCES_FILE
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2SecureStore
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class LegacyRsaSecureStoreMigrationIntegrationShould {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val legacyPreferences = context.getSharedPreferences(LEGACY_PREFERENCES_FILE, Context.MODE_PRIVATE)
    private val securePreferences = context.getSharedPreferences(SECURE_PREFERENCES_FILE, Context.MODE_PRIVATE)

    private val password = (0 until 1000).joinToString { it.toString() }
    private val authState = AuthState()
    private var databaseConfigs: String? = null

    @Before
    fun setUp() {
        databaseConfigs = legacyPreferences.getString(DB_CONFIGS_KEY, null)
        clearStores()

        val writer = LegacyRsaSecureStoreWriter(context)
        writer.setChunkedData("username", "admin")
        writer.setChunkedData("serverUrl", SERVER_URL)
        writer.setChunkedData("password", password)
        writer.setChunkedData("oicState", authState.jsonSerializeString())
        writer.setChunkedData("authorizationType", AuthorizationType.OPEN_ID_CONNECT.name)
        writer.setData(DB_PASSWORD_KEY, DB_PASSWORD)
        writer.setData("oauth2_client_id", "client")
        writer.setData("oauth2_is_registered", "true")
        legacyPreferences.edit().putString(DB_CONFIGS_KEY, DB_CONFIGS_VALUE).commit()
    }

    @After
    fun tearDown() {
        clearStores()
        legacyPreferences.edit().apply {
            if (databaseConfigs == null) remove(DB_CONFIGS_KEY) else putString(DB_CONFIGS_KEY, databaseConfigs)
        }.commit()
    }

    @Test
    fun keep_every_value_readable() {
        val secureStore = AndroidSecureStore(context)

        val credentials = CredentialsSecureStoreImpl(ChunkedSecureStore(secureStore)).get()!!
        assertThat(credentials.username).isEqualTo("admin")
        assertThat(credentials.serverUrl).isEqualTo(SERVER_URL)
        assertThat(credentials.password).isEqualTo(password)
        assertThat(credentials.openIDConnectState?.jsonSerializeString()).isEqualTo(authState.jsonSerializeString())
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
        assertThat(OAuth2SecureStore(secureStore).clientId).isEqualTo("client")
        assertThat(OAuth2SecureStore(secureStore).isRegistered).isTrue()
        assertThat(DatabaseEncryptionPasswordManager.create(secureStore).getPassword(DB_NAME)).isEqualTo(DB_PASSWORD)
    }

    @Test
    fun remove_the_legacy_entries_and_key_but_not_the_insecure_entries() {
        AndroidSecureStore(context)

        assertThat(legacyPreferences.all.keys.filter { it.isTestKey() }).isEmpty()
        assertThat(LegacyRsaSecureStoreWriter.hasLegacyKey()).isFalse()
        assertThat(legacyPreferences.getString(DB_CONFIGS_KEY, null)).isEqualTo(DB_CONFIGS_VALUE)
    }

    @Test
    fun do_nothing_when_run_again() {
        AndroidSecureStore(context)
        val migrated = rawSecureEntries()

        AndroidSecureStore(context)

        assertThat(rawSecureEntries()).isEqualTo(migrated)
    }

    @Test
    fun not_overwrite_values_already_present_in_the_new_store() {
        ChunkedSecureStore(AndroidSecureStore(context)).setData("username", "new-user")
        LegacyRsaSecureStoreWriter(context).setChunkedData("username", "admin")

        val secureStore = ChunkedSecureStore(AndroidSecureStore(context))

        assertThat(secureStore.getData("username")).isEqualTo("new-user")
        assertThat(secureStore.getData("serverUrl")).isEqualTo(SERVER_URL)
    }

    @Test
    fun keep_the_legacy_key_and_the_values_that_cannot_be_decrypted() {
        val undecryptable = Base64.encodeToString(ByteArray(RSA_BLOCK_SIZE), Base64.DEFAULT)
        legacyPreferences.edit().putString(UNDECRYPTABLE_KEY, undecryptable).commit()

        val secureStore = ChunkedSecureStore(AndroidSecureStore(context))

        assertThat(secureStore.getData("username")).isEqualTo("admin")
        assertThat(legacyPreferences.all.keys.filter { it.isTestKey() }).containsExactly(UNDECRYPTABLE_KEY)
        assertThat(LegacyRsaSecureStoreWriter.hasLegacyKey()).isTrue()
    }

    private fun rawSecureEntries(): Map<String, *> {
        return securePreferences.all.filterKeys { it.isTestKey() }
    }

    private fun clearStores() {
        securePreferences.edit().apply {
            securePreferences.all.keys.filter { it.isTestKey() }.forEach { remove(it) }
        }.commit()
        legacyPreferences.edit().apply {
            legacyPreferences.all.keys.filter { it.isTestKey() }.forEach { remove(it) }
        }.commit()
        if (LegacyRsaSecureStoreWriter.hasLegacyKey()) {
            LegacyRsaSecureStoreWriter.deleteLegacyKey()
        }
    }

    private fun String.isTestKey(): Boolean {
        return TEST_KEY_PREFIXES.any { startsWith(it) }
    }

    private companion object {
        const val SECURE_PREFERENCES_FILE = "dhis2_secure_store"
        const val SERVER_URL = "https://dhis2.org"
        const val DB_NAME = "legacy_migration_test.db"
        const val DB_PASSWORD_KEY = "DBPW_<>_$DB_NAME"
        const val DB_PASSWORD = "0123456789abcdefghijABCDEFGHIJ01"
        const val DB_CONFIGS_KEY = "DB_CONFIGS"
        const val DB_CONFIGS_VALUE = "{\"accounts\":[]}"
        const val UNDECRYPTABLE_KEY = "oauth2_undecryptable"
        const val RSA_BLOCK_SIZE = 256
        val TEST_KEY_PREFIXES = listOf(
            "username",
            "serverUrl",
            "password",
            "oicState",
            "authorizationType",
            "oauth2_",
            DB_PASSWORD_KEY,
        )
    }
}
