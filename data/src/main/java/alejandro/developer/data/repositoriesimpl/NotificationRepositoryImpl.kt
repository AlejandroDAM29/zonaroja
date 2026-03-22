package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.local.datasources.NotificationLocalDataSource
import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.mappers.toEntity
import alejandro.developer.data.session.toUserScopeKey
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.repositories.AuthRepository
import alejandro.developer.domain.repositories.NotificationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationRepositoryImpl @Inject constructor(
    private val local: NotificationLocalDataSource,
    private val authRepository: AuthRepository
) : NotificationRepository {

    override fun observeNotifications(): Flow<List<AppNotificationModel>> {
        return authRepository.observeCurrentUserId().flatMapLatest { currentUserId ->
            local.observeNotifications(currentUserId.toUserScopeKey()).map { notifications ->
                notifications.map { it.toDomain() }
            }
        }
    }

    override fun observeUnreadNotificationsCount(): Flow<Int> {
        return authRepository.observeCurrentUserId().flatMapLatest { currentUserId ->
            local.observeUnreadNotificationsCount(currentUserId.toUserScopeKey())
        }
    }

    override fun observeNotification(notificationId: Long): Flow<AppNotificationModel?> {
        return authRepository.observeCurrentUserId().flatMapLatest { currentUserId ->
            local.observeNotification(notificationId, currentUserId.toUserScopeKey()).map { notification ->
                notification?.toDomain()
            }
        }
    }

    override suspend fun saveNotification(notification: IncomingNotificationModel): Long {
        return local.insertNotification(
            notification.toEntity(authRepository.getCurrentUserId().toUserScopeKey())
        )
    }

    override suspend fun markAsRead(notificationId: Long) {
        local.markAsRead(notificationId, authRepository.getCurrentUserId().toUserScopeKey())
    }

    override suspend fun deleteNotification(notificationId: Long) {
        local.deleteNotification(notificationId, authRepository.getCurrentUserId().toUserScopeKey())
    }
}
