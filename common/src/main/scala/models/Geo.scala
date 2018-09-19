package models

import play.api.libs.json.{ Json, OFormat }

case class Geo(
  latitude: Double,
  longitude: Double,
  elevation: Option[Double])

object Geo {

  implicit val jsonFormat: OFormat[Geo] = Json.format[Geo]
}