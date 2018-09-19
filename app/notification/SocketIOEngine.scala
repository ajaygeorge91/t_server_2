package notification

import akka.NotUsed
import akka.actor.{ ActorRef, ActorSystem }
import akka.stream._
import akka.stream.scaladsl.{ BroadcastHub, Flow, Keep, Sink, Source }
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{ Inject, Provider, Singleton }
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc.{ AnyContentAsEmpty, Request }
import play.engineio.EngineIOController
import play.socketio.scaladsl.SocketIO
import play.socketio.scaladsl.SocketIOEventCodec.{ SocketIOEventsDecoder, SocketIOEventsEncoder }
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SocketIOEngine @Inject() (
  silhouette: Silhouette[DefaultEnv],
  socketIO: SocketIO,
  notificationEventBus: NotificationEventBus)(
  implicit
  val ec: ExecutionContext,
  implicit val mat: Materializer,
  implicit val actorSystem: ActorSystem
) extends Provider[EngineIOController]
  with LazyLogging {

  private val BUFFER_SIZE: Int = 32

  import play.socketio.scaladsl.SocketIOEventCodec._

  // This will decode String "FROM_CLIENT" events coming in
  val decoder: SocketIOEventsDecoder[String] = decodeByName {
    case "MESSAGE" => decodeJson[String]
  }

  // This will encode String "FROM_SERVER" events going out
  val encoder: SocketIOEventsEncoder[String] = encodeByType[String] {
    case _: String => "MESSAGE" -> encodeJson[String]
  }

  private def notificationFlow(channel: String) = {

    Logger.logger.error(s"channel : $channel")
    // choose the buffer size of the actor source and how the actor
    // will react to its overflow
    val eventListenerSourceActorRef = Source.actorRef[String](BUFFER_SIZE, OverflowStrategy.dropHead)

    val (actor1: ActorRef, source: Source[String, NotUsed]) = eventListenerSourceActorRef
      //we need both actor and source for next step(Keep.both)
      .toMat(BroadcastHub.sink[String])(Keep.both)
      .run

    // subscribe to the event bus using actor
    notificationEventBus.subscribe(actor1, channel)

    // send back a flow.
    Flow.fromSinkAndSourceCoupled(Sink.ignore, source)

  }

  // Here we create an EngineIOController to handle requests for our notification
  // system, and we add the notification flow under the "/notification" namespace.

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  override lazy val get: EngineIOController =
    socketIO.builder
      .onConnectAsync { (request, _) =>
        {
          implicit val req: Request[AnyContentAsEmpty.type] = Request(request, AnyContentAsEmpty)
          val r1 = silhouette.UserAwareRequestHandler { securedRequest =>
            Future.successful(HandlerResult(Ok, securedRequest.identity))
          }
          r1.map {
            //              case HandlerResult(_, Some(user)) => Right(user)
            //              case HandlerResult(r, None)       => Left(r)
            case HandlerResult(_, data) =>
              data
          }
        }
      }
      .addNamespace(decoder, encoder) {
        case (session, notification) if notification.split('?').head == "/notification" =>
          session.data match {
            case None =>
              val channel: String = "public"
              notificationFlow(channel)
            case Some(userId) =>
              val channel: String = UserChannelUtils.getChannelFromUserId(userId.id)
              notificationFlow(channel)
          }
      }
      .addNamespace(decoder, encoder) {
        case (session, notification) if notification.split('?').head == "/live" =>

          val channel: String = "live"
          notificationFlow(channel)

      }
      .createController

}
