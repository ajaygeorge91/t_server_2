package common

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import play.api.libs.json.{ Json, Writes }
import play.api.mvc.{ AbstractController, ControllerComponents, Result }
import utils.exceptions.{ RoleExceptions, VertexNotFound }

import scala.util.control.NonFatal

/**
 * The base application controller.
 *
 * @param components  The Play controller components.
 */
class BaseApplicationController @Inject() (
  components: ControllerComponents
) extends AbstractController(components) with LazyLogging {

  private def apiSuccessResponse[T](code: Int, data: Option[T])(implicit nested: Writes[T]): Result = {
    Status(code)(
      Json.obj(
        "success" -> true,
        "data" -> data.map(d => nested.writes(d))
      )
    )
  }

  private def apiFailureResponse[T](code: Int, message: Option[String]): Result = {
    Status(code)(
      Json.obj(
        "success" -> false,
        "message" -> message
      )
    )
  }

  protected def success[T](data: T)(implicit nested: Writes[T]): Result = {
    apiSuccessResponse(OK, data = Some(data))
  }

  protected def failure(throwable: Throwable): Result = {
    throwable match {
      case e: VertexNotFound => failure(e.message)
      case e: RoleExceptions => unAuthorized(e.message)
      case NonFatal(e) =>
        logger.error(e.getMessage)
        error("Unknown error")
    }
  }

  protected def failure(message: String): Result = {
    apiFailureResponse(OK, message = Some(message))
  }

  protected def notFound(message: String): Result = {
    apiFailureResponse(NOT_FOUND, message = Some(message))
  }

  protected def unAuthorized(message: String): Result = {
    apiFailureResponse(UNAUTHORIZED, message = Some(message))
  }

  protected def badRequest(message: String): Result = {
    apiFailureResponse(BAD_REQUEST, message = Some(message))
  }

  protected def error(message: String): Result = {
    apiFailureResponse(INTERNAL_SERVER_ERROR, message = Some(message))
  }

}
