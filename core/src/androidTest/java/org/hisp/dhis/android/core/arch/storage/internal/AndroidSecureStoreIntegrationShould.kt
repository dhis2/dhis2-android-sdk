package org.hisp.dhis.android.core.arch.storage.internal

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.io.encoding.Base64

@RunWith(D2JunitRunner::class)
class AndroidSecureStoreIntegrationShould {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val store = AndroidSecureStore(context)
    private val rawPreferences = context.getSharedPreferences(SECURE_PREFERENCES_FILE, Context.MODE_PRIVATE)

    @After
    fun tearDown() {
        store.removeData(KEY_A)
        store.removeData(KEY_B)
    }

    @Test
    fun read_back_stored_values_from_a_new_instance() {
        val value = "x".repeat(1000)
        store.setData(KEY_A, value)

        assertThat(AndroidSecureStore(context).getData(KEY_A)).isEqualTo(value)
    }

    @Test
    fun store_values_in_their_own_preferences_file() {
        store.setData(KEY_A, "x".repeat(1000))

        assertThat(store.getAllKeys().filter { it.startsWith(KEY_A) }).containsExactly(KEY_A)
        assertThat(rawPreferences.contains(KEY_A)).isTrue()
        assertThat(AndroidInsecureStore(context).getAllKeys()).doesNotContain(KEY_A)
    }

    @Test
    fun remove_values() {
        store.setData(KEY_A, "value")
        store.setData(KEY_A, null)

        assertThat(store.getData(KEY_A)).isNull()
        assertThat(rawPreferences.contains(KEY_A)).isFalse()
    }

    @Test
    fun reject_values_swapped_between_keys() {
        store.setData(KEY_A, "value-a")
        store.setData(KEY_B, "value-b")
        val rawA = rawPreferences.getString(KEY_A, null)
        val rawB = rawPreferences.getString(KEY_B, null)
        rawPreferences.edit().putString(KEY_A, rawB).putString(KEY_B, rawA).commit()

        assertThrows(RuntimeException::class.java) { store.getData(KEY_A) }
        assertThrows(RuntimeException::class.java) { store.getData(KEY_B) }
    }

    @Test
    fun reject_a_flipped_byte() {
        store.setData(KEY_A, "value")
        val bytes = Base64.decode(rawPreferences.getString(KEY_A, null)!!)
        bytes[bytes.size - 1] = (bytes[bytes.size - 1].toInt() xor 1).toByte()
        rawPreferences.edit().putString(KEY_A, Base64.encode(bytes)).commit()

        assertThrows(RuntimeException::class.java) { store.getData(KEY_A) }
    }

    @Test
    fun reject_a_truncated_value() {
        store.setData(KEY_A, "value")
        val raw = rawPreferences.getString(KEY_A, null)!!
        rawPreferences.edit().putString(KEY_A, raw.substring(0, raw.length / 2)).commit()

        assertThrows(RuntimeException::class.java) { store.getData(KEY_A) }
    }

    private companion object {
        const val SECURE_PREFERENCES_FILE = "dhis2_secure_store"
        const val KEY_A = "android_secure_store_test_a"
        const val KEY_B = "android_secure_store_test_b"
    }
}
