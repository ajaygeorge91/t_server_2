package utils.auth

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import javax.inject.Inject
import play.api.i18n.{ Messages, MessagesApi }
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future

/**
 * Custom unsecured error handler.
 */
class CustomUnsecuredErrorHandler
  extends UnsecuredErrorHandler {

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(Unauthorized(Json.obj("success" -> false, "message" -> "Not Authorized")))
  }
}
