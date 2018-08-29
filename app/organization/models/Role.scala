package organization.models

object Role {
  val ADMIN: String = "admin"
  val STAFF: String = "staff"

  val all: List[String] = List(ADMIN, STAFF)
}
