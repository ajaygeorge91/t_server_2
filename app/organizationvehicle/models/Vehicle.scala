package organizationvehicle.models

import play.api.libs.json.{ Json, OFormat }

final case class Vehicle(
  id: Long,
  name: String,
  image: Option[String],
  description: Option[String],
  publicAccess: Boolean
)

object Vehicle {

  implicit val jsonFormat: OFormat[Vehicle] = Json.format[Vehicle]
}