package organization.models

import com.example.database.graph.schema.VertexLabels
import gremlin.scala.{ id, label }
import models.{ Address, Geo }
import org.joda.time.DateTime
import play.api.libs.json.Json

@label(VertexLabels.Organization)
final case class OrganizationVertex(
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

object OrganizationVertex {

  def create(createOrganization: CreateOrganization): OrganizationVertex =
    OrganizationVertex(
      id = 0L,
      name = createOrganization.name,
      image = createOrganization.image,
      description = createOrganization.description,
      address = createOrganization.address.map(address => Json.toJson(address).toString()),
      geo = createOrganization.geo.map(geo => Json.toJson(geo).toString()),
      email = createOrganization.email,
      telephone = createOrganization.telephone,
      publicAccess = createOrganization.publicAccess,
      createdAt = DateTime.now().toString,
      updatedAt = DateTime.now().toString
    )

  def update(organizationVertex: OrganizationVertex, organization: Organization): OrganizationVertex =
    OrganizationVertex(
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

  def toOrganization(organizationVertex: OrganizationVertex): Organization =
    Organization(
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