package organization.models

object Role {
  val ADMIN: String = "admin"
  val READ: String = "read"

  val all: List[String] = List(ADMIN, READ)
}
