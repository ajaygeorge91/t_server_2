package models

import play.api.libs.json.{ Json, OFormat }

case class Place(
  id: Long,
  name: String,
  image: String,
  googlePlaceId: String, // google places place_id
  address: String, // google places formatted_address
  postalCode: String, // google places postal_code
  addressLocality: String, // google places administrative_area_level_2
  addressRegion: String, // google places administrative_area_level_1
  addressCountry: String, // google places country
  geo: Geo)

object Place {

  implicit val jsonFormat: OFormat[Place] = Json.format[Place]

}