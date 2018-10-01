package common

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import play.api.data.FormError
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import utils.exceptions.{RoleExceptions, VertexNotFound}

import scala.util.control.NonFatal

/**
 * The base application controller.
 *
 * @param components  The Play controller components.
 */
class BaseApplicationController @Inject() (
  components: ControllerComponents
) extends AbstractController(components) with LazyLogging {

  private def apiSuccessResponse[T](code: Int, data: T)(implicit nested: Writes[T]): Result = {
    Status(code)(nested.writes(data))
  }

  protected def apiFailureResponse(code: Int, errorMap: Map[String, String]): Result = {
    Status(code)(Json.toJson(errorMap))
  }

  protected def success[T](data: T)(implicit nested: Writes[T]): Result = {
    apiSuccessResponse(OK, data = data)
  }

  protected def failure(throwable: Throwable): Result = {
    throwable match {
      case e: VertexNotFound => apiFailureResponse(NOT_FOUND, Map("message" -> e.message))
      case e: RoleExceptions => apiFailureResponse(UNAUTHORIZED, Map("message" -> e.message))
      case NonFatal(e) =>
        logger.error(e.getMessage)
        apiFailureResponse(INTERNAL_SERVER_ERROR, Map("message" ->"Unknown error"))
    }
  }

  protected def notFound(message: String): Result = {
    apiFailureResponse(NOT_FOUND, Map("message" -> message))
  }

  protected def unAuthorized(message: String): Result = {
    apiFailureResponse(UNAUTHORIZED, Map("message" -> message))
  }

  protected def badRequest(formErrors:Seq[FormError]): Result = {
    apiFailureResponse(BAD_REQUEST, formErrors.toMap[String,String])
  }

  protected def badRequest(message: String): Result = {
    apiFailureResponse(BAD_REQUEST, Map("message" -> message))
  }

}
