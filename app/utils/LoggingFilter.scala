package utils

import akka.stream.Materializer
import javax.inject.Inject
import notification.services.LiveNotificationService
import play.api.mvc._
import play.api.routing.{ HandlerDef, Router }

import scala.concurrent.{ ExecutionContext, Future }

class LoggingFilter @Inject() (
  liveNotificationService: LiveNotificationService)(implicit val mat: Materializer, ec: ExecutionContext)
  extends Filter with Logger {

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val handlerDef: HandlerDef = requestHeader.attrs(Router.Attrs.HandlerDef)
      val action = handlerDef.controller + "." + handlerDef.method
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      liveNotificationService.sendLive(s"$action took ${requestTime}ms and returned ${result.header.status}")

      logger.trace(s"$action took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }

}
