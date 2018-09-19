package trip.models

import models.Place
import play.api.libs.json.{ Json, OFormat }

case class WayPoint(
  arrivalTime: String,
  departureTime: Option[String],
  place: Place,
  stopover: Boolean,
  wayPointType: String
)

object WayPoint {

  implicit val jsonFormat: OFormat[WayPoint] = Json.format[WayPoint]
}