package com.molostream.core.domain.usecase

import com.molostream.core.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

/** Streams whether the user is currently subscribed. */
class ObserveSubscriptionUseCase(
    private val repository: SubscriptionRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.subscribed
}

/** Persists a new subscription state. */
class SetSubscriptionUseCase(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(value: Boolean) = repository.setSubscribed(value)
}
