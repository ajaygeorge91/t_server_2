package trip.models

final case class CreateTrip(
  name: String,
  description: Option[String],
  fromLocation: WayPoint,
  toLocation: WayPoint,
  stopovers: Option[List[WayPoint]],
  totalDistance: Option[Double],
  totalTime: Option[String],
  publicAccess: Boolean
)

