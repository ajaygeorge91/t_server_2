package trip.models

import play.api.libs.json.{ Json, OFormat }

/**
 * Itinerary
 */
case class Trip(
  id: Long,
  name: String,
  description: Option[String],
  fromLocation: WayPoint,
  toLocation: WayPoint,
  stopovers: List[WayPoint],
  totalDistance: Option[Double],
  totalTime: Option[String],
  publicAccess: Boolean)

object Trip {

  implicit val jsonFormat: OFormat[Trip] = Json.format[Trip]
}