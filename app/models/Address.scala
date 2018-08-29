package models

import play.api.libs.json.{ Json, OFormat }

final case class Address(
  addressLocality: String,
  streetAddress: Option[String],
  addressRegion: Option[String],
  addressCountry: Option[String],
  postalCode: Option[String]
)

object Address {

  implicit val jsonFormat: OFormat[Address] = Json.format[Address]
}