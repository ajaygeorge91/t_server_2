package models

import play.api.libs.json.{ Json, OFormat }

case class Address(
  addressLocality: String,
  streetAddress: String,
  addressRegion: String,
  addressCountry: String,
  postalCode: String)

object Address {

  implicit val jsonFormat: OFormat[Address] = Json.format[Address]
}