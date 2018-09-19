package notification.services

import javax.inject.Inject
import notification.{ MessageEnvelope, NotificationEventBus }

class LiveNotificationService @Inject() (notificationEventBus: NotificationEventBus) {

  def sendLive(message: String): Unit = {
    notificationEventBus.publish(MessageEnvelope("live", message))
  }

}
