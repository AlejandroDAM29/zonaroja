package alejandro.developer.data.subscriptions

import com.google.firebase.messaging.FirebaseMessaging
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await

private const val FCM_GENERAL_TOPIC = "zonaroja_general"

interface NotificationSubscriptionManager {
    suspend fun subscribeToGeneralTopic()

    suspend fun unsubscribeFromGeneralTopic()
}

@Singleton
class FirebaseNotificationSubscriptionManager @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : NotificationSubscriptionManager {

    override suspend fun subscribeToGeneralTopic() {
        firebaseMessaging.subscribeToTopic(FCM_GENERAL_TOPIC).await()
    }

    override suspend fun unsubscribeFromGeneralTopic() {
        firebaseMessaging.unsubscribeFromTopic(FCM_GENERAL_TOPIC).await()
    }
}
