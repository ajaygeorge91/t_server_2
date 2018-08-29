package vehicle.models

import models.{ Address, Geo }

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

