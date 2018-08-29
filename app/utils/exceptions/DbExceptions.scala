package utils.exceptions

trait DbExceptions extends RuntimeException {
  val message: String
}

case class VertexNotFound(message: String) extends DbExceptions
