package notification

import akka.actor.ActorRef
import akka.event.{ EventBus, SubchannelClassification }
import akka.util.Subclassification
import javax.inject.Singleton

@Singleton
class NotificationEventBus extends EventBus with SubchannelClassification {

  type Event = MessageEnvelope
  type Classifier = String
  type Subscriber = ActorRef

  // SubClassification is an object providing `isEqual` and `isSubclass`
  // to be consumed by the other methods of this classifier
  protected val subclassification: Subclassification[Classifier] = new Subclassification[Classifier] {

    /** True if and only if x and y are of the same class. */
    def isEqual(x: Classifier, y: Classifier): Boolean = x.equalsIgnoreCase(y)

    /** True if and only if x is a subclass of y; equal classes must be considered sub-classes! */
    def isSubclass(x: Classifier, y: Classifier): Boolean = x.startsWith(y)
  }

  // is used for extracting the classifier from the incoming events
  protected def classify(event: Event): Classifier = event.topic

  // will be invoked for each event for all subscribers which registered
  // themselves for the event’s classifier
  protected def publish(event: Event, subscriber: Subscriber): Unit = subscriber.!(event.payload)

}
