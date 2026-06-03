package com.molostream.core.data

import com.molostream.core.datastore.SubscriptionPreferences
import com.molostream.core.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

/** Backs [SubscriptionRepository] with the encrypted DataStore. */
class SubscriptionRepositoryImpl(
    private val preferences: SubscriptionPreferences,
) : SubscriptionRepository {
    override val subscribed: Flow<Boolean> = preferences.subscribed
    override suspend fun setSubscribed(value: Boolean) = preferences.setSubscribed(value)
}
