package organization.models

import models.{ Address, Geo }

final case class CreateOrganization(
  name: String,
  image: Option[String],
  description: Option[String],
  address: Option[Address],
  geo: Option[Geo],
  email: Option[String],
  telephone: Option[String],
  publicAccess: Boolean
)

