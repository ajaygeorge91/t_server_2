package utils.exceptions

trait RoleExceptions extends RuntimeException {
  val message: String
}

case class NoPermissionForRole(message: String) extends DbExceptions

case class NoRoleFound(message: String) extends DbExceptions
