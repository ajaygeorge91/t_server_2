package vehicle.models

import com.example.database.graph.schema.VertexLabels
import gremlin.scala.{ id, label }
import models.{ Address, Geo }
import org.joda.time.DateTime
import play.api.libs.json.Json

@label(VertexLabels.Vehicle)
final case class VehicleVertex(
  @id id: Long,
  name: String,
  image: Option[String],
  description: Option[String],
  address: Option[String],
  geo: Option[String],
  email: Option[String],
  telephone: Option[String],
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
      address = createVehicle.address.map(address => Json.toJson(address).toString()),
      geo = createVehicle.geo.map(geo => Json.toJson(geo).toString()),
      email = createVehicle.email,
      telephone = createVehicle.telephone,
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
      address = organization.address.map(address => Json.toJson(address).toString()),
      geo = organization.geo.map(geo => Json.toJson(geo).toString()),
      email = organization.email,
      telephone = organization.telephone,
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
      address = organizationVertex.address.map(address => Json.fromJson[Address](Json.parse(address)).get),
      geo = organizationVertex.geo.map(geo => Json.fromJson[Geo](Json.parse(geo)).get),
      email = organizationVertex.email,
      telephone = organizationVertex.telephone,
      publicAccess = organizationVertex.publicAccess
    )

}