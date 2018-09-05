package vehicle.models

import models.{ Address, Geo }
import play.api.libs.json.{ Json, OFormat }

final case class CreateVehicle(
  name: String,
  image: Option[String],
  description: Option[String],
  address: Option[Address],
  geo: Option[Geo],
  email: Option[String],
  telephone: Option[String],
  publicAccess: Boolean,
  vehicleMode: String = Mode.OWNS
)

object CreateVehicle {

  implicit val jsonFormat: OFormat[CreateVehicle] = Json.format[CreateVehicle]
}