package com.molostream.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.molostream.core.datastore.crypto.KeystoreCrypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Persists the subscription flag in a Preferences DataStore. The value is
 * encrypted with [KeystoreCrypto] before storage, so the on-disk file only
 * ever contains keystore-protected ciphertext.
 */
class SubscriptionPreferences(
    private val dataStore: DataStore<Preferences>,
    private val crypto: KeystoreCrypto,
) {
    private val subscribedKey = stringPreferencesKey("subscribed_enc")

    val subscribed: Flow<Boolean> = dataStore.data
        .catch { error ->
            // A corrupt/missing file should read as "not subscribed", not crash.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            prefs[subscribedKey]?.let { encoded ->
                runCatching { crypto.decrypt(encoded).toBoolean() }.getOrDefault(false)
            } ?: false
        }

    suspend fun setSubscribed(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[subscribedKey] = crypto.encrypt(value.toString())
        }
    }
}
