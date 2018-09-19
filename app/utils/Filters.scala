package utils

import akka.stream.Materializer
import javax.inject.Inject
import notification.services.LiveNotificationService
import play.api.http.HttpFilters
import play.api.mvc.{ EssentialFilter, RequestHeader, Result }
import play.api.routing.{ HandlerDef, Router }
import play.filters.headers.SecurityHeadersFilter

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Provides filters.
 */
class Filters @Inject() (
  securityHeadersFilter: SecurityHeadersFilter,
  liveNotificationService: LiveNotificationService)(implicit val mat: Materializer, ec: ExecutionContext) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(securityHeadersFilter)

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val handlerDef: HandlerDef = requestHeader.attrs(Router.Attrs.HandlerDef)
      val action = handlerDef.controller + "." + handlerDef.method
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      liveNotificationService.sendLive(s"$action took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }

}
