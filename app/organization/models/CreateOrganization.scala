package organization.models

import models.{ Address, Geo }
import play.api.libs.json.{ Json, OFormat }

final case class CreateOrganization(
  name: String,
  image: Option[String],
  description: Option[String],
  address: Option[Address],
  geo: Option[Geo],
  email: Option[String],
  telephone: Option[String],
  publicAccess: Boolean
)

object CreateOrganization {

  implicit val jsonFormat: OFormat[CreateOrganization] = Json.format[CreateOrganization]
}