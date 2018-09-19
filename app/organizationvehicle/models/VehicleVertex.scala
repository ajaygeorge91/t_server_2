package organizationvehicle.models

import com.example.database.graph.schema.VertexLabels
import gremlin.scala.{ id, label }
import org.joda.time.DateTime

@label(VertexLabels.Vehicle)
final case class VehicleVertex(
  @id id: Long,
  name: String,
  image: Option[String],
  description: Option[String],
  publicAccess: Boolean,
  createdAt: String,
  updatedAt: String)

object VehicleVertex {

  def create(createVehicle: CreateVehicle): VehicleVertex =
    VehicleVertex(
      id = 0L,
      name = createVehicle.name,
      image = createVehicle.image,
      description = createVehicle.description,
      publicAccess = createVehicle.publicAccess,
      createdAt = DateTime.now().toString,
      updatedAt = DateTime.now().toString
    )

  def update(organizationVertex: VehicleVertex, organization: Vehicle): VehicleVertex =
    VehicleVertex(
      id = organization.id,
      name = organization.name,
      image = organization.image,
      description = organization.description,
      publicAccess = organization.publicAccess,
      createdAt = organizationVertex.createdAt,
      updatedAt = DateTime.now().toString
    )

  def toVehicle(organizationVertex: VehicleVertex): Vehicle =
    Vehicle(
      id = organizationVertex.id,
      name = organizationVertex.name,
      image = organizationVertex.image,
      description = organizationVertex.description,
      publicAccess = organizationVertex.publicAccess
    )

}