package trip.models

import com.example.database.graph.schema.VertexLabels
import gremlin.scala.{ id, label }
import org.joda.time.DateTime

@label(VertexLabels.Trip)
final case class TripVertex(
  @id id: Long,
  name: String,
  description: Option[String],
  fromLocation: WayPoint,
  toLocation: WayPoint,
  stopovers: List[WayPoint],
  totalDistance: Option[Double],
  totalTime: Option[String],
  publicAccess: Boolean,
  createdAt: String,
  updatedAt: String)

object TripVertex {
  //
  //  def create(createTrip: CreateTrip): TripVertex =
  //    TripVertex(
  //      id = 0L,
  //      name = createTrip.name,
  //      description = createTrip.description,
  //      fromLocation = createTrip.fromLocation,
  //      toLocation = createTrip.toLocation,
  //      stopovers = createTrip.stopovers,
  //      totalDistance = createTrip.totalDistance,
  //      totalTime = createTrip.totalTime,
  //      publicAccess = createTrip.publicAccess,
  //      createdAt = DateTime.now().toString,
  //      updatedAt = DateTime.now().toString
  //    )
  //
  //  def update(organizationVertex: TripVertex, organization: Trip): TripVertex =
  //    TripVertex(
  //      id = organization.id,
  //      name = organization.name,
  //      description = organization.description,
  //      publicAccess = organization.publicAccess,
  //      createdAt = organizationVertex.createdAt,
  //      updatedAt = DateTime.now().toString
  //    )
  //
  //  def toTrip(organizationVertex: TripVertex): Trip =
  //    Trip(
  //      id = organizationVertex.id,
  //      name = organizationVertex.name,
  //      description = organizationVertex.description,
  //      publicAccess = organizationVertex.publicAccess
  //    )

}